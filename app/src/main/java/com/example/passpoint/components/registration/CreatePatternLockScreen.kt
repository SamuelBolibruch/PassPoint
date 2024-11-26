package com.example.passpoint.components.registration

import PatternLockComponent
import android.app.Activity
import android.util.Log
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
import com.behametrics.logger.Logger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CreatePatternLockScreen() {
    val firestore = FirebaseFirestore.getInstance() // Inicializácia Firestore
    val auth = FirebaseAuth.getInstance() // Inicializácia Firebase Auth
    val context = LocalContext.current // Získaj kontext

    var firstPattern by remember { mutableStateOf<List<Int>?>(null) }
    var step by remember { mutableStateOf(1) } // 1: Zadaj heslo, 2: Potvrď heslo
    var message by remember { mutableStateOf("Draw your pattern") }
    var attempts by remember { mutableStateOf(0) } // Premenná na sledovanie pokusov

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

        if (step != 4) {
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
                            // Vzor bol potvrdený, ulož do Firestore
                            val currentUser = auth.currentUser
                            val uid = currentUser?.uid

                            if (uid != null) {
                                val patternData = hashMapOf(
                                    "pattern" to firstPattern // Pole čísiel predstavujúce vzor
                                )

                                firestore.collection("users").document(uid)
                                    .update(patternData as Map<String, Any>) // Použitie `update` na aktualizáciu dokumentu
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Pattern successfully set!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        message = "Now train your biometrics!"
                                        Logger.start(context as Activity)
                                        step = 3
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Failed to save pattern: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error: User not logged in!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            true
                        } else {
                            Toast.makeText(
                                context,
                                "Patterns do not match. Try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                            message = "Choose Your Pattern"
                            step = 1
                            firstPattern = null
                            false
                        }
                    }

                    3 -> {
                        // V kroku 3 opakovane kontroluj, či je zadaný vzor správny
                        if (firstPattern == ids) {
                            attempts++
                            Log.d("PatternLock", "Attempts: $attempts")  // Logovanie počtu pokusov
                            if (attempts >= 10) {
                                // Ak sa správne zadal vzor 10-krát
                                Toast.makeText(
                                    context,
                                    "Pattern successfully trained 10 times!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Pokračovať k ďalšiemu kroku, napr. ukončiť proces
                                message = "You can now use your pattern"
                                Logger.stop(context as Activity) // Vypne Logger
                                step = 4 // Alebo iný krok, ak je to potrebné
                            } else {
                                message = "Pattern correct! $attempts/10"
                            }
                        } else {
                            message = "Incorrect pattern, try again."
                        }

                        true
                    }

                    else -> false
                }
            }
        }
    }
}