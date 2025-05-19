package com.example.passpoint.components.registration

import android.content.Intent
import android.util.Patterns
import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.passpoint.LoginActivity
import com.example.passpoint.R
import com.example.passpoint.components.TextFieldWithLabel

@Composable
fun RegistrationFormScreen(
    navController: NavController,
    emailState: MutableState<String>,
    passwordState: MutableState<String>,
    repeatPasswordState: MutableState<String>,
) {
    // Funkcia na overenie sily hesla
    fun isPasswordStrong(password: String): Boolean {
        val passwordRegex =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W&&[^\\s]])[A-Za-z\\d\\W&&[^\\s]]{8,}\$".toRegex()
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

//         3. Overenie sily hesla
        if (!isPasswordStrong(password)) {
            onValidationComplete(false, "Heslo musí obsahovať aspoň jedno veľké písmeno, jedno malé písmeno, číslicu a špeciálny znak.")
            return
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
        TextFieldWithLabel(label = "Password", textState = passwordState, optional = false, isPassword = true)
        Spacer(modifier = Modifier.height(8.dp))
        TextFieldWithLabel(label = "Repeat Password", textState = repeatPasswordState, optional = false, isPassword = true)
        Spacer(modifier = Modifier.height(16.dp))

        // Already have an account? Clickable text
        Text(
            text = "Already have an account?",
            modifier = Modifier.clickable {
                // Navigate to login screen when clicked
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            },
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)        )

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