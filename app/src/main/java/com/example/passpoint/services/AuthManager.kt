// AuthManager.kt

package com.example.passpoint.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Register method
    fun registerUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null) // Success
                } else {
                    onComplete(false, task.exception?.message) // Error
                }
            }
    }

    // Login method
    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null) // Success
                } else {
                    onComplete(false, task.exception?.message) // Error
                }
            }
    }

    // Logout method
    fun logoutUser() {
        auth.signOut()
    }

    // Get current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Check if email already exists in Firebase Authentication
    fun checkIfEmailExists(email: String, onResult: (Boolean) -> Unit) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Skontrolujeme, či existuje priradený sign-in method
                    val signInMethods = task.result?.signInMethods
                    if (!signInMethods.isNullOrEmpty()) {
                        // Email už existuje
                        onResult(true)
                    } else {
                        // Email neexistuje
                        onResult(false)
                    }
                } else {
                    // Ak sa vyskytla chyba pri overovaní emailu
                    onResult(false)
                }
            }
    }
}