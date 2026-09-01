package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculatorScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var display by remember { mutableStateOf("0") }
    var firstOperand by remember { mutableStateOf<Double?>(null) }
    var operator by remember { mutableStateOf<String?>(null) }
    var waitingForSecondOperand by remember { mutableStateOf(false) }

    fun onNumberClick(number: String) {
        if (waitingForSecondOperand) {
            display = number
            waitingForSecondOperand = false
        } else {
            display = if (display == "0") number else display + number
        }
    }

    fun onDecimalClick() {
        if (waitingForSecondOperand) {
            display = "0."
            waitingForSecondOperand = false
        } else if (!display.contains(".")) {
            display += "."
        }
    }

    fun onOperatorClick(op: String) {
        val currentValue = display.toDoubleOrNull() ?: 0.0
        if (firstOperand == null) {
            firstOperand = currentValue
        } else if (operator != null && !waitingForSecondOperand) {
            val result = calculate(firstOperand!!, currentValue, operator!!)
            display = formatResult(result)
            firstOperand = result
        }
        operator = op
        waitingForSecondOperand = true
    }

    fun onEqualsClick() {
        val currentValue = display.toDoubleOrNull() ?: 0.0
        if (firstOperand != null && operator != null) {
            val result = calculate(firstOperand!!, currentValue, operator!!)
            display = formatResult(result)
            firstOperand = null
            operator = null
            waitingForSecondOperand = false
        }
    }

    fun onClearClick() {
        display = "0"
        firstOperand = null
        operator = null
        waitingForSecondOperand = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = display,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 24.dp),
            maxLines = 1
        )

        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("C", "0", ".", "+"),
            listOf("=")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { symbol ->
                    CalculatorButton(
                        symbol = symbol,
                        modifier = Modifier
                            .weight(1f)
                            .then(if (symbol != "=") Modifier.aspectRatio(1f) else Modifier.height(64.dp)),
                        onClick = {
                            when {
                                symbol.all { it.isDigit() } -> onNumberClick(symbol)
                                symbol == "." -> onDecimalClick()
                                symbol == "C" -> onClearClick()
                                symbol == "=" -> onEqualsClick()
                                else -> onOperatorClick(symbol)
                            }
                        }
                    )
                }
                if (row.size < 4 && !row.contains("=")) {
                    repeat(4 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

            }
        }
    }
}

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = if (symbol.all { it.isDigit() } || symbol == ".") {
            ButtonDefaults.buttonColors()
        } else if (symbol == "=" || symbol == "C") {
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        } else {
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        }
       
    ) {
        Text(text = symbol, fontSize = 24.sp)
    }
}

fun calculate(first: Double, second: Double, operator: String): Double {
    return when (operator) {
        "+" -> first + second
        "-" -> first - second
        "*" -> first * second
        "/" -> if (second != 0.0) first / second else 0.0
        else -> second
    }
}

fun formatResult(result: Double): String {
    return if (result % 1 == 0.0) {
        result.toLong().toString()
    } else {
        result.toString()
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalculatorTheme {
        CalculatorScreen()
    }
}
