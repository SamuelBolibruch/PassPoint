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

        accelFile.writeText("")
        gyroFile.writeText("")
        touchFile.writeText("")

        accelFile.writeText("input,session_id,timestamp,x,y,z\n")
        gyroFile.writeText("input,session_id,timestamp,x,y,z\n")
        touchFile.writeText("input,session_id,timestamp,event_type,event_type_detail,pointer_id,x,y,pressure,size,touch_major,touch_minor,raw_x,raw_y\n")

        val activity = context as Activity
        Logger.start(activity)

        PatternLockComponent { ids ->

            CoroutineScope(Dispatchers.Main).launch {
                Logger.stop(activity)
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

fun uploadFiles(files: List<File>, onResult: (Boolean) -> Unit) {
    val client = OkHttpClient()

    val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

    val fileMap = mapOf(
        "accelerometer" to files.find { it.name.contains("accelerometer") },
        "gyroscope" to files.find { it.name.contains("gyroscope") },
        "touch" to files.find { it.name.contains("touch") }
    )

    for ((fieldName, file) in fileMap) {
        if (file != null) {
            builder.addFormDataPart(
                fieldName, // "touch", "accelerometer", alebo "gyroscope"
                file.name,
                RequestBody.create("text/csv".toMediaTypeOrNull(), file)
            )
        }
    }

    val requestBody = builder.build()

    val request = Request.Builder()
        .url("https://tp-production-97a4.up.railway.app/process/")
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                onResult(false)
            }
        }

        override fun onResponse(call: Call, response: Response) {
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                onResult(response.isSuccessful)
            }
        }
    })
}

