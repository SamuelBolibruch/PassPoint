package com.example.passpoint

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.passpoint.components.TextFieldWithLabel
import com.example.passpoint.services.AuthManager
import com.example.passpoint.ui.theme.PassPointTheme

class RegistrationActivity : ComponentActivity() {
    private val authManager = AuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PassPointTheme {
                val navController = rememberNavController()

                // State for storing input values
                val emailState = remember { mutableStateOf("") }
                val passwordState = remember { mutableStateOf("") }
                val repeatPasswordState = remember { mutableStateOf("") }

                // Navigation host, setting up the graph
                NavHost(
                    navController = navController,
                    startDestination = "registration_form_screen"
                ) {
                    composable("registration_form_screen") {
                        RegistrationFormScreen(
                            navController = navController,
                            emailState = emailState,
                            passwordState = passwordState,
                            repeatPasswordState = repeatPasswordState,
                            authManager = authManager // Passing it to the screen
                        )
                    }
                    composable("additional_information_screen") {
                        AdditionalInformations(
                            navController = navController,
                            email = emailState.value,
                            password = passwordState.value,
                            repeatPassword = repeatPasswordState.value
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegistrationFormScreen(
    navController: NavController,
    emailState: MutableState<String>,
    passwordState: MutableState<String>,
    repeatPasswordState: MutableState<String>,
    authManager: AuthManager // Accept the authManager as a parameter
) {
    // Funkcia na overenie sily hesla
    fun isPasswordStrong(password: String): Boolean {
        val passwordRegex =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}\$".toRegex()
        return password.matches(passwordRegex)
    }

    fun validateRegistrationForm(
        email: String,
        password: String,
        repeatPassword: String,
        onValidationComplete: (Boolean, String?) -> Unit
    ) {
        // 1. Overenie formátu e-mailu
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onValidationComplete(false, "Neplatný formát e-mailu")
            return
        }

        // 2. Overenie, či sa heslá zhodujú
        if (password != repeatPassword) {
            onValidationComplete(false, "Heslá sa nezhodujú")
            return
        }

        // 4. Overenie, či e-mail už existuje
        authManager.checkIfEmailExists(email) { exists ->
            if (exists) {
                onValidationComplete(false, "Tento e-mail už existuje.")
            } else {
                // Ak všetky kontroly prejdú, validácia je úspešná
                onValidationComplete(true, null)
            }
        }

        // Ak všetky kontroly prejdú, validácia je úspešná
        onValidationComplete(true, null)
    }

    // Get the context to show the Toast
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.passpoint),
            contentDescription = "Passpoint Logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), // You can adjust the height as needed
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Form Fields
        TextFieldWithLabel(label = "Email", textState = emailState, optional = false)
        Spacer(modifier = Modifier.height(8.dp))
        TextFieldWithLabel(label = "Password", textState = passwordState, optional = false)
        Spacer(modifier = Modifier.height(8.dp))
        TextFieldWithLabel(
            label = "Repeat Password",
            textState = repeatPasswordState,
            optional = false
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Validate the form
            validateRegistrationForm(
                emailState.value,
                passwordState.value,
                repeatPasswordState.value
            ) { isValid, message ->
                if (isValid) {
                    // If the form is valid, navigate to the next screen
                    navController.navigate("additional_information_screen")
                } else {
                    // If the form is invalid, show the error message
                    Toast.makeText(context, message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text(text = "Continue")
        }
    }
}

@Composable
fun AdditionalInformations(
    navController: NavController,
    email: String,
    password: String,
    repeatPassword: String
) {
    // Layout for the "Continue" screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Name: $email")
        Text(text = "Email: $password")
        Text(text = "Password: $repeatPassword")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Perform some action (like submitting data)
            println("User Registration Completed")
        }) {
            Text(text = "Complete Registration")
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun RegistrationFormScreenPreview() {
//    PassPointTheme {
//        // Create a mock AuthManager
//        val mockAuthManager = object : AuthManager() {
//            override fun checkIfEmailExists(email: String, onResult: (Boolean) -> Unit) {
//                // Mock response: Assume no email exists
//                onResult(false)
//            }
//        }
//
//        // Call RegistrationFormScreen with a mock AuthManager
//        RegistrationFormScreen(
//            navController = rememberNavController(),
//            emailState = remember { mutableStateOf("") },
//            passwordState = remember { mutableStateOf("") },
//            repeatPasswordState = remember { mutableStateOf("") },
//            authManager = mockAuthManager // Passing mock AuthManager here
//        )
//    }
//}

@Preview(showBackground = true)
@Composable
fun AdditionalInformationScreenPreview() {
    PassPointTheme {
        // Preview for the additional information screen
        AdditionalInformations(
            navController = rememberNavController(),
            email = "John Doe",
            password = "johndoe@example.com",
            repeatPassword = "password123"
        )
    }
}