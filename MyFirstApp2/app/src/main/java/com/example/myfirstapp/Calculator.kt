package com.example.myfirstapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Calculator : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                CalculatorScreen()
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var number1 by remember { mutableStateOf("") }
    var number2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("Result: ") }
    var showDialog by remember { mutableStateOf(false) }
    val activity = (LocalContext.current as? Activity)

    // Handle back press
    BackHandler {
        showDialog = true
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Exit") },
            text = { Text("Are you sure you want to go back?") },
            confirmButton = {
                TextButton(onClick = { activity?.finish() }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Simple Calculator", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = number1,
            onValueChange = { number1 = it },
            label = { Text("Enter Number 1") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = number2,
            onValueChange = { number2 = it },
            label = { Text("Enter Number 2") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { result = calculate(number1, number2, "+") }) { Text("+") }
            Button(onClick = { result = calculate(number1, number2, "-") }) { Text("-") }
            Button(onClick = { result = calculate(number1, number2, "*") }) { Text("*") }
            Button(onClick = { result = calculate(number1, number2, "/") }) { Text("/") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = result, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
    }
}

private fun calculate(n1: String, n2: String, operator: String): String {
    val num1 = n1.toDoubleOrNull()
    val num2 = n2.toDoubleOrNull()

    if (num1 == null || num2 == null) return "Please enter valid numbers"

    return when (operator) {
        "+" -> "Result: ${num1 + num2}"
        "-" -> "Result: ${num1 - num2}"
        "*" -> "Result: ${num1 * num2}"
        "/" -> if (num2 != 0.0) "Result: ${num1 / num2}" else "Cannot divide by zero"
        else -> "Error"
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    MaterialTheme {
        CalculatorScreen()
    }
}
