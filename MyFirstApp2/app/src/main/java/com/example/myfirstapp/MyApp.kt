package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// 1. New Composable for the whole screen
@Composable
fun MyApp() {
    var name by rememberSaveable() { mutableStateOf("Amos") }
    var textInput by rememberSaveable() { mutableStateOf("") }
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        label = { Text("Enter name") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (textInput != name) {
                                name = textInput
                                Toast.makeText(context, "Name updated to $name", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = textInput.length >= 2
                    ) {
                        Text("Update")
                    }
                }
                MyGreeting(
                    name = name,
                    from = "Emma",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),

            ) {
                Button(
                    onClick = {
                        val intent = Intent(context, Calculator::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text("Go to Calculator")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val intent = Intent(context, TicTacToeActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text("Go to Tic Tac Toe")
                }
            }
        }
    }
}

// 2. Update MainActivity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

                MyApp()

        }
    }
}

// 3. Update Preview
@Preview(showBackground = true)
@Composable
fun MyPreview() {

        MyApp() // Now shows everything!

}