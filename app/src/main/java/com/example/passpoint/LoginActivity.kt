package com.example.passpoint

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.passpoint.ui.components.TextFieldWithLabel
import com.example.passpoint.ui.theme.PassPointTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PassPointTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        }
    }
}

@Composable
fun LoginScreen() {
    // Get the current context (this is the activity context)
    val context = LocalContext.current

    // State for storing input values
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Add the image above the input fields
        Image(
            painter = painterResource(id = R.drawable.passpoint),
            contentDescription = "Passpoint Logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), // You can adjust the height as needed
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add input fields and pass state to TextFieldWithLabel
        TextFieldWithLabel(label = "Email", textState = emailState)
        Spacer(modifier = Modifier.height(8.dp))
        TextFieldWithLabel(label = "Password", textState = passwordState)
        Spacer(modifier = Modifier.height(16.dp))

        // Clickable text to navigate to RegistrationActivity
        Text(
            text = "Go to Registration",
            color = Color.Blue,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { navigateToRegistration(context) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Login Button that navigates to MainActivity
        Button(onClick = {
            // Access the values of the fields
            val email = emailState.value
            val password = passwordState.value

            // Handle the login logic, for now, just print the values
            println("Email: $email, Password: $password")

            // Example: Navigate to MainActivity after login
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Login")
        }
    }
}

// Function to navigate to RegistrationActivity
fun navigateToRegistration(context: android.content.Context) {
    val intent = Intent(context, RegistrationActivity::class.java)
    context.startActivity(intent) // Start the RegistrationActivity
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PassPointTheme {
        LoginScreen()
    }
}