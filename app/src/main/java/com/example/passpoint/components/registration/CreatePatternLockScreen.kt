package com.example.passpoint.components.registration

import PatternLockComponent
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.behametrics.logger.Logger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File

suspend fun sendPostRequests(vzor_id: String) {
    val client = OkHttpClient()
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid ?: run {
        Log.e("SendPostRequests", "User not authenticated")
        return
    }

    val types = listOf("orientation", "sensor_accelerometer", "sensor_gyroscope", "touch")

    for (type in types) {
        val filePath = "/data/data/com.example.passpoint/files/logs/$type.csv"
        val file = File(filePath)
        Log.d("SendPostRequests", "File path: ${file.absolutePath}")
        if (!file.exists()) {
            Log.e("SendPostRequests", "File not found at ${file.absolutePath}")
            continue
        }

        val requestBody = try {
            Log.d("SendPostRequests", "Building request body...")
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userid", userId)
                .addFormDataPart("type", type)
                .addFormDataPart("vzor_id", vzor_id)
                .addFormDataPart("file", file.name, file.asRequestBody("text/csv".toMediaTypeOrNull()))
                .build()
        } catch (e: Exception) {
            Log.e("SendPostRequests", "Error building request body: ${e.message}")
            continue
        }


        val request = Request.Builder()
            .url("https://biopassword.jecool.net/db/saveData.php")
            .post(requestBody)
            .addHeader("Content-Type", "multipart/form-data")
            .build()

        try {
            Log.d("SendPostRequests", "Sending request for type: $type...")
            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()

                val responseBody = response.body?.string() ?: "No body in response"
                if (!response.isSuccessful) {
                    Log.e("SendPostRequests", "Request failed: ${response.message}")
                    Log.e("SendPostRequests", "Response code: ${response.code}")
                    Log.e("SendPostRequests", "Response body: $responseBody")
                } else {
                    Log.d("SendPostRequests", "Response body: $responseBody")
                    try {
                        val jsonResponse = JSONObject(responseBody)
                        val message = jsonResponse.optString("message", "No message")
                        Log.d("SendPostRequests", "Message: $message")

                        // Vymazanie obsahu súboru po úspešnom odoslaní
                        Log.d("SendPostRequests", "File content cleared successfully for type: $type")
                    } catch (e: JSONException) {
                        Log.e("SendPostRequests", "Failed to parse JSON: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SendPostRequests", "Error during request: ${e.message}")
            e.printStackTrace()
        }
    }
}


@Composable
fun CreatePatternLockScreen() {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val activity = context as Activity

    var firstPattern by remember { mutableStateOf<List<Int>?>(null) }
    var step by remember { mutableStateOf(1) }
    var message by remember { mutableStateOf("Draw your pattern") }
    var attempts by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(48.dp))

        if (step == 4 || step == 5) {
            Text("Follow this pattern:", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            PatternGuide(step)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (step != 6) {
            PatternLockComponent { ids ->
                when (step) {
                    1 -> {
                        firstPattern = ids
                        step = 2
                        message = "Confirm Your Pattern"
                        true


                    }
                    2 -> {
                        if (firstPattern == ids) {
                            val currentUser = auth.currentUser
                            val uid = currentUser?.uid

                            if (uid != null) {
                                val patternData = hashMapOf("pattern" to firstPattern)
                                firestore.collection("users").document(uid)
                                    .update(patternData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Pattern successfully set!", Toast.LENGTH_SHORT).show()
                                        message = "Now train your biometrics with your pattern"
                                        Logger.start(activity)
                                        step = 3

                                        File("/data/data/com.example.passpoint/files/logs/sensor_accelerometer.csv").writeText("")
                                        File("/data/data/com.example.passpoint/files/logs/sensor_gyroscope.csv").writeText("")
                                        File("/data/data/com.example.passpoint/files/logs/touch.csv").writeText("")

                                        File("/data/data/com.example.passpoint/files/logs/sensor_accelerometer.csv").writeText("input,session_id,timestamp,x,y,z\n")
                                        File("/data/data/com.example.passpoint/files/logs/sensor_gyroscope.csv").writeText("input,session_id,timestamp,x,y,z\n")
                                        File("/data/data/com.example.passpoint/files/logs/touch.csv").writeText("input,session_id,timestamp,event_type,event_type_detail,pointer_id,x,y,pressure,size,touch_major,touch_minor,raw_x,raw_y\n")
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Failed to save pattern: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                Toast.makeText(context, "Error: User not logged in!", Toast.LENGTH_SHORT).show()
                            }
                            true
                        } else {
                            Toast.makeText(context, "Patterns do not match. Try again.", Toast.LENGTH_SHORT).show()
                            message = "Choose Your Pattern"
                            step = 1
                            firstPattern = null
                            false
                        }
                    }
                    3 -> {
                        if (firstPattern == ids) {
                            attempts++
                            Log.d("PatternLock", "Attempts: $attempts")
                            if (attempts >= 2) { //TODO ZMENIT NA 25
                                Toast.makeText(context, "Pattern successfully trained 25 times!", Toast.LENGTH_SHORT).show()
                                message = "Now train predefined pattern!"
                                step = 4
                                attempts = 0

                                // Stop logger BEFORE sending data
                                //Logger.stop(activity)
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(100) // Small delay to ensure logger stopped
                                    sendPostRequests("1")
                                    Toast.makeText(context, "Data sent to server!", Toast.LENGTH_SHORT).show()
                                    File("/data/data/com.example.passpoint/files/logs/sensor_accelerometer.csv").writeText("")
                                    File("/data/data/com.example.passpoint/files/logs/sensor_gyroscope.csv").writeText("")
                                    File("/data/data/com.example.passpoint/files/logs/touch.csv").writeText("")

                                    File("/data/data/com.example.passpoint/files/logs/sensor_accelerometer.csv").writeText("input,session_id,timestamp,x,y,z\n")
                                    File("/data/data/com.example.passpoint/files/logs/sensor_gyroscope.csv").writeText("input,session_id,timestamp,x,y,z\n")
                                    File("/data/data/com.example.passpoint/files/logs/touch.csv").writeText("input,session_id,timestamp,event_type,event_type_detail,pointer_id,x,y,pressure,size,touch_major,touch_minor,raw_x,raw_y\n")
                                }
                            } else {
                                message = "Pattern correct! $attempts/25"
                            }
                        } else {
                            message = "Incorrect pattern, try again."
                        }
                        true
                    }
                    4 -> {
                        if (arrayListOf(0, 3, 6, 7, 8) == ids) {
                            attempts++
                            Log.d("PatternLock", "Predefined pattern attempts: $attempts")
                            if (attempts >= 2) { //TODO ZMENIT NA 25
                                Toast.makeText(context, "Predefined pattern trained successfully!", Toast.LENGTH_SHORT).show()
                                message = "Now train with second predefined pattern!"
                                step = 5
                                attempts = 0

                                // Stop logger BEFORE sending data
                               // Logger.stop(activity)
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(100)
                                    sendPostRequests("2")
                                    File("/data/data/com.example.passpoint/files/logs/sensor_accelerometer.csv").writeText("")
                                    File("/data/data/com.example.passpoint/files/logs/sensor_gyroscope.csv").writeText("")
                                    File("/data/data/com.example.passpoint/files/logs/touch.csv").writeText("")

                                    File("/data/data/com.example.passpoint/files/logs/sensor_accelerometer.csv").writeText("input,session_id,timestamp,x,y,z\n")
                                    File("/data/data/com.example.passpoint/files/logs/sensor_gyroscope.csv").writeText("input,session_id,timestamp,x,y,z\n")
                                    File("/data/data/com.example.passpoint/files/logs/touch.csv").writeText("input,session_id,timestamp,event_type,event_type_detail,pointer_id,x,y,pressure,size,touch_major,touch_minor,raw_x,raw_y\n")
                                    Toast.makeText(context, "Data sent to server!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                message = "Pattern correct! $attempts/25"
                            }
                        } else {
                            message = "Incorrect predefined pattern, try again."
                        }
                        true
                    }
                    5 -> {
                        if (arrayListOf(4, 2, 5, 7, 6, 3, 8, 0) == ids) {
                            attempts++
                            Log.d("PatternLock", "Second predefined pattern attempts: $attempts")
                            if (attempts >= 2) { //TODO ZMENIT NA 25
                                Toast.makeText(context, "Second predefined pattern trained successfully!", Toast.LENGTH_SHORT).show()
                                message = "You can now use your pattern"
                                step = 6

                                // Final stop - no restart needed
                                Logger.stop(activity)
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(100)
                                    sendPostRequests("3")
                                    File("/data/data/com.example.passpoint/files/logs/sensor_accelerometer.csv").writeText("")
                                    File("/data/data/com.example.passpoint/files/logs/sensor_gyroscope.csv").writeText("")
                                    File("/data/data/com.example.passpoint/files/logs/touch.csv").writeText("")

                                    File("/data/data/com.example.passpoint/files/logs/sensor_accelerometer.csv").writeText("input,session_id,timestamp,x,y,z\n")
                                    File("/data/data/com.example.passpoint/files/logs/sensor_gyroscope.csv").writeText("input,session_id,timestamp,x,y,z\n")
                                    File("/data/data/com.example.passpoint/files/logs/touch.csv").writeText("input,session_id,timestamp,event_type,event_type_detail,pointer_id,x,y,pressure,size,touch_major,touch_minor,raw_x,raw_y\n")
                                    Toast.makeText(context, "Data sent to server!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                message = "Pattern correct! $attempts/25"
                            }
                        } else {
                            message = "Incorrect second predefined pattern, try again."
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }
}

@Composable
fun PatternGuide(step: Int) {
    val pattern = when (step) {
        4 -> listOf(0, 3, 6, 7, 8) // First predefined pattern
        5 -> listOf(4, 2, 5, 7, 6, 3, 8, 0) // Second predefined pattern
        else -> emptyList()
    }

    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.RED // Set text color to red
        textSize = 100f // Increase the text size for numbers
        typeface = android.graphics.Typeface.DEFAULT_BOLD // Make text bold
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
    }

    if (pattern.isNotEmpty()) {
        Canvas(modifier = Modifier.size(250.dp)) {
            val gridSize = 3
            val cellSize = size.width / gridSize

            for (i in 0 until gridSize) {
                for (j in 0 until gridSize) {
                    val index = i * gridSize + j
                    val centerX = j * cellSize + cellSize / 2
                    val centerY = i * cellSize + cellSize / 2

                    // Draw the circle for the point (larger circles)
                    drawCircle(
                        color = if (pattern.contains(index)) Color.Blue else Color.Gray,
                        radius = 25f, // Increase the radius of the circles
                        center = Offset(centerX, centerY)
                    )

                    // If the point is part of the pattern, display its order (index + 1)
                    if (pattern.contains(index)) {
                        val number = (pattern.indexOf(index) + 1).toString() // The order number
                        val textWidth = paint.measureText(number)
                        val textHeight = paint.descent() - paint.ascent()

                        // Draw the number slightly to the right of the center
                        drawContext.canvas.nativeCanvas.drawText(
                            number,
                            centerX + 80, // Slightly move the number to the right
                            centerY + textHeight / 4,
                            paint
                        )
                    }
                }
            }

            for (i in 0 until pattern.size - 1) {
                val startIdx = pattern[i]
                val endIdx = pattern[i + 1]

                val startX = (startIdx % gridSize) * cellSize + cellSize / 2
                val startY = (startIdx / gridSize) * cellSize + cellSize / 2
                val endX = (endIdx % gridSize) * cellSize + cellSize / 2
                val endY = (endIdx / gridSize) * cellSize + cellSize / 2

                drawLine(
                    color = Color.Blue,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 5f
                )
            }
        }
    }
}



