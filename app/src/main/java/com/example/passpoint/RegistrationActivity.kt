package com.example.passpoint

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 1: Perform Firebase Authentication operation
        createTestUser()

        // Step 2: Set the Compose UI
        setContent {
            PassPointTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegistrationScreen()
                }
            }
        }
    }

    private fun createTestUser() {
        // Initialize FirebaseAuth
        val auth = FirebaseAuth.getInstance()

        // Test user details
        val testEmail = "testuser@example.com"
        val testPassword = "password123"

        // Create a new user
        auth.createUserWithEmailAndPassword(testEmail, testPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Show success message
                    Toast.makeText(
                        this,
                        "User created: ${auth.currentUser?.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Show error message
                    Toast.makeText(
                        this,
                        "Failed to create user: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}

@Composable
fun RegistrationScreen() {
    // Get the current context
    val context = LocalContext.current

    // State for storing input values
    val nameState = remember { mutableStateOf("") }
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
        TextFieldWithLabel(label = "Name", textState = nameState)
        Spacer(modifier = Modifier.height(8.dp))
        TextFieldWithLabel(label = "Email", textState = emailState)
        Spacer(modifier = Modifier.height(8.dp))
        TextFieldWithLabel(label = "Password", textState = passwordState)
        Spacer(modifier = Modifier.height(16.dp))

        // Clickable text to navigate to LoginActivity
        Text(
            text = "Go to Login",
            color = Color.Blue,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Register button that navigates to MainActivity
        Button(onClick = {
            // Access the values of the fields
            val name = nameState.value
            val email = emailState.value
            val password = passwordState.value

            // Handle the registration logic, for now, just print the values
            println("Name: $name, Email: $email, Password: $password")

            // Example: Navigate to MainActivity after registration
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Register")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    PassPointTheme {
        RegistrationScreen()
    }
}