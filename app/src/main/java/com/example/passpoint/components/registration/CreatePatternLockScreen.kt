package com.example.passpoint.components.registration

import PatternLockComponent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreatePatternLockScreen() {
    val context = LocalContext.current // Získaj kontext
    var firstPattern by remember { mutableStateOf<List<Int>?>(null) }
    var step by remember { mutableStateOf(1) } // 1: Zadaj heslo, 2: Potvrď heslo
    var message by remember { mutableStateOf("Draw your pattern") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            fontSize = 24.sp, // Zvýšenie veľkosti textu
            fontWeight = FontWeight.Bold // Nastavenie tučného textu
        )

        Spacer(modifier = Modifier.height(48.dp))

        if(step != 3){
            PatternLockComponent { ids ->
                when (step) {
                    1 -> {
                        firstPattern = ids
                        step = 2
                        message = "Confirm Your Pattern"
                        true // Vráti `true`, ak je pattern správny
                    }
                    2 -> {
                        if (firstPattern == ids) {
                            Toast.makeText(context, "Pattern successfully set!", Toast.LENGTH_SHORT).show()
                            message = "Pattern successfully set!"
                            step = 3
                            true
                        } else {
                            Toast.makeText(context, "Patterns do not match. Try again.", Toast.LENGTH_SHORT).show()
                            message = "Choose Your Pattern"
                            step = 1
                            firstPattern = null
                            false
                        }
                    }
                    else -> false
                }
            }
        }
    }
}