package com.example.passpoint.components.registration

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.passpoint.components.DropdownWithLabel
import com.example.passpoint.components.TextFieldWithLabel
import com.example.passpoint.services.AuthManager
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun AdditionalInformations(
    navController: NavController,
    email: String,
    password: String,
    authManager: AuthManager // Accept the authManager as a parameter
) {
    val context = LocalContext.current
    val userNameState = remember { mutableStateOf("") }
    val ageState = remember { mutableStateOf("0-18") }
    val genderState = remember { mutableStateOf("Male") }
    val handState = remember { mutableStateOf("Right-handed") }


    val firestore = FirebaseFirestore.getInstance()

    // Layout for the "Continue" screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextFieldWithLabel(label = "Username", textState = userNameState, optional = true)

        // Spacer for spacing
        Spacer(modifier = Modifier.height(16.dp))

        // Add the dropdown menu
        DropdownWithLabel(
            items = listOf("0-18", "18-25", "26-35", "36-45", "46-60", "60+"),
            label = "Select your age:",
            textState = ageState
        )

        DropdownWithLabel(
            items = listOf("Male", "Female", "Other"),
            label = "Select your gender:",
            textState = genderState
        )

        DropdownWithLabel(
            items = listOf("Right-handed", "Left-handed"),
            label = "Select your preferred hand:",
            textState = handState
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            authManager.registerUser(email = email, password = password) { success, errorMessage ->
                if (success) {
                    // Získanie UID aktuálne prihláseného používateľa
                    val currentUser = authManager.getCurrentUser()
                    val uid = currentUser?.uid

                    if (uid != null) {
                        // Pripraviť dáta na uloženie do Firestore
                        val userData = hashMapOf(
                            "username" to userNameState.value,
                            "age" to ageState.value,
                            "gender" to genderState.value,
                            "handPreference" to handState.value,
                            "email" to email // Pre istotu uložíme aj email
                        )

                        // Zápis údajov do kolekcie "users"
                        firestore.collection("users").document(uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "User data saved successfully", Toast.LENGTH_SHORT).show()
                                navController.navigate("create_pattern_password")
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Failed to save data: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(context, "Error: User ID not found!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Registration Failed: ${errorMessage ?: "Unknown Error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }) {
            Text(text = "Complete Registration")
        }
    }
}