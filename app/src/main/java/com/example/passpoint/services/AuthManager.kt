// AuthManager.kt

package com.example.passpoint.services

import android.content.Context
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
    fun logoutUser(context: Context, onComplete: () -> Unit) {
        auth.signOut()
        onComplete()  // Notify that logout is complete
    }

    // Get current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

}