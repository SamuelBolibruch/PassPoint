package com.example.passpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.passpoint.components.registration.AdditionalInformations
import com.example.passpoint.components.registration.RegistrationFormScreen
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
                            repeatPasswordState = repeatPasswordState
                        )
                    }
                    composable("additional_information_screen") {
                        AdditionalInformations(
                            navController = navController,
                            email = emailState.value,
                            password = passwordState.value,
                            authManager = authManager
                        )
                    }
                    composable("create_pattern_password") {
                        Text(text = "Ahojky")
                    }
                }
            }
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

//@Preview(showBackground = true)
//@Composable
//fun AdditionalInformationScreenPreview() {
//    PassPointTheme {
//        // Preview for the additional information screen
//        AdditionalInformations(
//            navController = rememberNavController(),
//            email = "John Doe",
//            password = "johndoe@example.com",
//            repeatPassword = "password123"
//        )
//    }
//}