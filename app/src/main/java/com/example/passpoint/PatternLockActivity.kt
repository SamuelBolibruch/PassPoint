package com.example.passpoint

import PatternLockComponent
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.behametrics.logger.Logger
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException

class PatternLockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PatternLockScreen()
        }
    }
}

@Composable
fun PatternLockScreen() {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }
    val isSuccess = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                (context as Activity).finish()
            },
            title = { Text(if (isSuccess.value) "Success" else "Authentication Failed") },
            text = { Text(dialogMessage.value) },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        (context as Activity).finish()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Please draw your pattern", modifier = Modifier.padding(16.dp))

        val accelFile = File("/data/data/com.example.passpoint/files/logs/sensor_accelerometer.csv")
        val gyroFile = File("/data/data/com.example.passpoint/files/logs/sensor_gyroscope.csv")
        val touchFile = File("/data/data/com.example.passpoint/files/logs/touch.csv")

        // Clear the existing content in the files
        accelFile.writeText("")
        gyroFile.writeText("")
        touchFile.writeText("")

        // Write headers with vzor_id column
        accelFile.writeText("input,session_id,timestamp,x,y,z\n")
        gyroFile.writeText("input,session_id,timestamp,x,y,z\n")
        touchFile.writeText("input,session_id,timestamp,event_type,event_type_detail,pointer_id,x,y,pressure,size,touch_major,touch_minor,raw_x,raw_y\n")

        val activity = context as Activity
        Logger.start(activity)

        PatternLockComponent { ids ->
            CoroutineScope(Dispatchers.Main).launch {
                Logger.stop(activity)

                delay(2000)

                addVzorIdToFile(accelFile)
                addVzorIdToFile(gyroFile)
                addVzorIdToFile(touchFile)

                uploadFiles(listOf(accelFile, gyroFile, touchFile)) { success, match ->
                    if (success) {
                        if (match) {
                            dialogMessage.value = "Authentication successful!"
                            isSuccess.value = true
                        } else {
                            dialogMessage.value = "Authentication failed."
                            isSuccess.value = false
                        }
                        showDialog.value = true
                    } else {
                        Toast.makeText(context, "Failed to send biometrics.", Toast.LENGTH_SHORT).show()
                        activity.finish()
                    }
                }
            }
            true
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// Function to add vzor_id to each file without adding it to the header
fun addVzorIdToFile(file: File) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"
    val lines = file.readLines().toMutableList()

    // Pridaj hlavičky
    lines[0] = lines[0] + ",vzor_id,userid"

    for (i in 1 until lines.size) {
        lines[i] = lines[i] + ",1,$currentUserId"  // vzor_id = 1, userid = Firebase UID
    }

    file.writeText(lines.joinToString("\n"))
}

fun uploadFiles(files: List<File>, onResult: (Boolean, Boolean) -> Unit) {
    val client = OkHttpClient()

    val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
    for (file in files) {
        val fieldName = when {
            file.name.contains("accelerometer", ignoreCase = true) -> "accelerometer"
            file.name.contains("gyroscope", ignoreCase = true) -> "gyroscope"
            file.name.contains("touch", ignoreCase = true) -> "touch"
            else -> "unknown"
        }

        builder.addFormDataPart(
            fieldName,
            file.name,
            RequestBody.create("text/csv".toMediaTypeOrNull(), file)
        )
    }

    val requestBody = builder.build()

    val request = Request.Builder()
        .url("https://tp-production-97a4.up.railway.app/process/")
        .post(requestBody)
        .build()

    Log.d("PatternLockActivity", "Sending request to server...")

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d("PatternLockActivity", "Request failed: ${e.message}")
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                onResult(false, false)
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            Log.d("PatternLockActivity", "Server response: $responseBody")

            val isMatch = try {
                responseBody?.contains("\"match\":true") ?: false
            } catch (e: Exception) {
                false
            }

            android.os.Handler(android.os.Looper.getMainLooper()).post {
                onResult(response.isSuccessful, isMatch)
            }
        }
    })
}