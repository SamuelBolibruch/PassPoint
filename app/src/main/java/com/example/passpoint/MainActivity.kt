package com.example.passpoint

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.behametrics.logger.Logger
import com.example.passpoint.services.AuthManager
import com.example.passpoint.ui.theme.PassPointTheme


class MainActivity : ComponentActivity() {
    private val authManager = AuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.start(this)
        setContent {
            PassPointTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PatternInput(authManager = authManager)
                }
            }
        }
    }
}

@Composable
fun PatternInput(modifier: Modifier = Modifier, authManager: AuthManager) {
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(modifier = Modifier.weight(1f))

            repeat(3) { rowIndex ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(3) { columnIndex ->
                        CircularButton(index = rowIndex * 3 + columnIndex + 1)
                    }
                }
            }

            // Add a Spacer for spacing between pattern and logout button
            Spacer(modifier = Modifier.height(32.dp))

            // Logout button at the bottom
            Button(
                onClick = {
                    // Call logout function and handle intent to navigate
                    authManager.logoutUser(context) {
                        Toast.makeText(
                            context,
                            "Successfully logged out",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Intent to navigate to LoginActivity
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)

                        // Optionally finish the current activity to remove it from the back stack
                        (context as? MainActivity)?.finish()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(text = "Logout")
            }
        }
    }
}

@Composable
fun CircularButton(index: Int) {
    Button(
        onClick = { /* Handle button click */ },
        shape = CircleShape,
        modifier = Modifier
            .size(64.dp) // Set button size
            .background(Color.LightGray, shape = CircleShape)
    ) {
        Text(text = index.toString(), color = Color.Black)
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PatternInputPreview() {
//    PassPointTheme {
//        PatternInput()
//    }
//}
