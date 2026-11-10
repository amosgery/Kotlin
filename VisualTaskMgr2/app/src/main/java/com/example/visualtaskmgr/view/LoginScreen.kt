package com.example.visualtaskmgr.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.visualtaskmgr.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var localError by remember { mutableStateOf<String?>(null) }

    val errorMessage = authViewModel.errorMessage ?: localError

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isSignUpMode) "הרשמה למערכת" else "התחברות למערכת",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { input: String ->
                            email = input
                            localError = null
                            authViewModel.clearError()
                        },
                        label = { Text("דוא\"ל (Email)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { input: String ->
                            password = input
                            localError = null
                            authViewModel.clearError()
                        },
                        label = { Text("סיסמה (Password)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Confirm Password Field (Sign Up mode only)
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { input: String ->
                                confirmPassword = input
                                localError = null
                                authViewModel.clearError()
                            },
                            label = { Text("אימות סיסמה") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Error Message
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Submit Button / Loading Indicator
                    if (authViewModel.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Button(
                            onClick = {
                                localError = null
                                authViewModel.clearError()

                                if (isSignUpMode) {
                                    if (password != confirmPassword) {
                                        localError = "הסיסמאות אינן תואמות"
                                        return@Button
                                    }
                                    authViewModel.signUp(email, password, onSuccess = onLoginSuccess)
                                } else {
                                    authViewModel.signIn(email, password, onSuccess = onLoginSuccess)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = if (isSignUpMode) "צור חשבון חדש" else "התחבר")
                        }
                    }

                    // Toggle Sign In / Sign Up Mode
                    TextButton(
                        onClick = {
                            isSignUpMode = !isSignUpMode
                            localError = null
                            authViewModel.clearError()
                        }
                    ) {
                        Text(
                            text = if (isSignUpMode)
                                "כבר יש לך חשבון? התחבר כאן"
                            else
                                "אין לך חשבון? הירשם כאן"
                        )
                    }
                }
            }
        }
    }
}
