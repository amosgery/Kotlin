package com.example.visualtaskmgr.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var currentUser by mutableStateOf<FirebaseUser?>(auth.currentUser)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        currentUser = firebaseAuth.currentUser
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit = {}) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "יש למלא דוא\"ל וסיסמה"
            return
        }

        isLoading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    currentUser = auth.currentUser
                    onSuccess()
                } else {
                    errorMessage = task.exception?.localizedMessage ?: "התחברות נכשלה"
                }
            }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit = {}) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "יש למלא דוא\"ל וסיסמה"
            return
        }
        if (password.length < 6) {
            errorMessage = "הסיסמה חייבת להכיל לפחות 6 תווים"
            return
        }

        isLoading = true
        errorMessage = null

        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    currentUser = auth.currentUser
                    onSuccess()
                } else {
                    errorMessage = task.exception?.localizedMessage ?: "הרשמה נכשלה"
                }
            }
    }

    fun signOut() {
        auth.signOut()
        currentUser = null
    }

    fun clearError() {
        errorMessage = null
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
    }
}
