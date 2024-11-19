package com.example.passpoint

import PatternLockComponent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.passpoint.components.TextFieldWithLabel
import com.example.passpoint.services.AuthManager
import com.example.passpoint.ui.theme.PassPointTheme

class LoginActivity : ComponentActivity() {
    private val authManager = AuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PassPointTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(authManager = authManager)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(authManager: AuthManager) {
    // Get the current context (this is the activity context)
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) } // Stav pre otvorenie dialógu

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
        TextFieldWithLabel(label = "Email", textState = emailState, optional = false)
        Spacer(modifier = Modifier.height(8.dp))
        TextFieldWithLabel(label = "Password", textState = passwordState, optional = false)

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.pattern_lock), // Váš obrázok pattern lock
            contentDescription = "Pattern lock",
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .clickable {
                    // Zobraziť dialóg pri kliknutí na obrázok
                    openDialog.value = true
                },
            contentScale = ContentScale.Fit
        )

        if (openDialog.value) {
            Dialog(
                onDismissRequest = { openDialog.value = false }
            ) {
                // Vlastný layout dialógu
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    PatternLockComponent()
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Don't have an account?? Clickable text
        Text(
            text = "Don't have an account?",
            modifier = Modifier.clickable {
                // Navigate to login screen when clicked
                val intent = Intent(context, RegistrationActivity::class.java)
                context.startActivity(intent)
            },
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val email = emailState.value
            val password = passwordState.value

            // Call the login method from AuthManager
            authManager.loginUser(email = email, password = password) { success, error ->
                if (success) {
                    // If login is successful, navigate to MainActivity
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                } else {
                    // If login fails, show an error message in Toast
                    Toast.makeText(
                        context,
                        "Login Failed: ${error ?: "Unknown Error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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

//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    PassPointTheme {
//        LoginScreen()
//    }
//}