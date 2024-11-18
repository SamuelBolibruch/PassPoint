package com.example.passpoint

import android.os.Bundle
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.passpoint.ui.components.TextFieldWithLabel
import com.example.passpoint.ui.theme.PassPointTheme

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PassPointTheme {
                // Setup the navigation
                val navController = rememberNavController()

                // State for storing input values
                val nameState = remember { mutableStateOf("") }
                val emailState = remember { mutableStateOf("") }
                val passwordState = remember { mutableStateOf("") }

                // Navigation host, setting up the graph
                NavHost(
                    navController = navController,
                    startDestination = "registration_form_screen"
                ) {
                    composable("registration_form_screen") {
                        RegistrationFormScreen(
                            navController = navController,
                            nameState = nameState,
                            emailState = emailState,
                            passwordState = passwordState
                        )
                    }
                    composable("additional_information_screen") {
                        AdditionalInformations(
                            navController = navController,
                            name = nameState.value,
                            email = emailState.value,
                            password = passwordState.value
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
    nameState: MutableState<String>,
    emailState: MutableState<String>,
    passwordState: MutableState<String>
) {
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
        TextFieldWithLabel(label = "Name", textState = nameState)
        Spacer(modifier = Modifier.height(8.dp))
        TextFieldWithLabel(label = "Email", textState = emailState)
        Spacer(modifier = Modifier.height(8.dp))
        TextFieldWithLabel(label = "Password", textState = passwordState)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Navigate to additional information screen, passing the values
            navController.navigate("additional_information_screen")
        }) {
            Text(text = "Continue")
        }
    }
}

@Composable
fun AdditionalInformations(
    navController: NavController,
    name: String,
    email: String,
    password: String
) {
    // Layout for the "Continue" screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Name: $name")
        Text(text = "Email: $email")
        Text(text = "Password: $password")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Perform some action (like submitting data)
            println("User Registration Completed")
        }) {
            Text(text = "Complete Registration")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationFormScreenPreview() {
    PassPointTheme {
        // Call RegistrationFormScreen without NavController for the preview
        RegistrationFormScreen(
            navController = rememberNavController(),
            nameState = remember { mutableStateOf("") },
            emailState = remember { mutableStateOf("") },
            passwordState = remember { mutableStateOf("") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdditionalInformationScreenPreview() {
    PassPointTheme {
        // Preview for the additional information screen
        AdditionalInformations(
            navController = rememberNavController(),
            name = "John Doe",
            email = "johndoe@example.com",
            password = "password123"
        )
    }
}