package com.example.passpoint

import java.io.File
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.behametrics.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import okhttp3.*
import java.io.IOException
import PatternLockComponent
import android.util.Log
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull

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
            }
            Logger.stop(activity)
            // Add vzor_id column to each row (but not in header)

            CoroutineScope(Dispatchers.Main).launch {
                delay(3000) // 3 seconds delay

                addVzorIdToFile(accelFile)
                addVzorIdToFile(gyroFile)
                addVzorIdToFile(touchFile)
            }
            // Upload the files
            uploadFiles(listOf(accelFile, gyroFile, touchFile)) { success ->
                if (success) {
                    Toast.makeText(context, "Login biometrics sent!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to send biometrics.", Toast.LENGTH_SHORT).show()
                }
                activity.finish()
            }
            true
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// Function to add vzor_id to each file without adding it to the header
fun addVzorIdToFile(file: File) {
    val lines = file.readLines().toMutableList()

    lines[0] = lines[0] + ",vzor_id"

    for (i in 1 until lines.size) {
        lines[i] = lines[i] + ",1"  // Append vzor_id=1 to each row
    }

    file.writeText(lines.joinToString("\n"))
}

fun uploadFiles(files: List<File>, onResult: (Boolean) -> Unit) {
    val client = OkHttpClient()

    val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
    for (file in files) {
        builder.addFormDataPart(
            "files",
            file.name,
            RequestBody.create("text/csv".toMediaTypeOrNull(), file)
        )
    }
    val requestBody = builder.build()

    val request = Request.Builder()
        .url("http://tp-production-97a4.up.railway.app/process/") // správny endpoint
        .post(requestBody)
        .build()

    Log.d("PatternLockActivity", "Sending request to server...") // Log pre kontrolu, že požiadavka ide
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d("PatternLockActivity", "Request failed: ${e.message}") // Log pri zlyhaní požiadavky
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                onResult(false)
            }
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                Log.d("PatternLockActivity", "Request sent successfully!") // Log pri úspešnom odoslaní požiadavky
            } else {
                Log.d("PatternLockActivity", "Request failed with status code: ${response.code}") // Log pri zlyhaní
            }
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                onResult(response.isSuccessful)
            }
        }
    })
}
