package com.example.myfirstapp // Declares the package name for the file

import android.os.Bundle // Imports Bundle for state management in Activity
import androidx.activity.ComponentActivity // Base class for Compose activities
import androidx.activity.compose.setContent // Function to set the UI content using Compose
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke // Used to define border thickness and color
import androidx.compose.foundation.layout.Arrangement // Alignment options for layouts
import androidx.compose.foundation.layout.Box // A basic layout container (unused here but imported)
import androidx.compose.foundation.layout.Column // Vertical layout container
import androidx.compose.foundation.layout.Row // Horizontal layout container
import androidx.compose.foundation.layout.Spacer // Empty space for layout adjustments
import androidx.compose.foundation.layout.aspectRatio // Modifier to maintain aspect ratio
import androidx.compose.foundation.layout.fillMaxSize // Modifier to fill the entire screen
import androidx.compose.foundation.layout.fillMaxWidth // Modifier to fill horizontal space
import androidx.compose.foundation.layout.height // Modifier to set specific height
import androidx.compose.foundation.layout.padding // Modifier to add internal padding
import androidx.compose.foundation.layout.size // Modifier to set width and height
import androidx.compose.material3.Button // Material Design standard button
import androidx.compose.material3.MaterialTheme // Accesses the app's design theme (colors, fonts)
import androidx.compose.material3.OutlinedButton // A button with a border and no fill
import androidx.compose.material3.Surface // Background container for UI components
import androidx.compose.material3.Text // Displays text on the screen
import androidx.compose.runtime.Composable // Marks a function as a Compose UI component
import androidx.compose.ui.Alignment // Alignment options for child elements
import androidx.compose.ui.Modifier // Used to modify UI element behavior/look
import androidx.compose.ui.graphics.Color // Provides standard color values
import androidx.compose.ui.text.font.FontWeight // Provides font weight options (e.g., Bold)
import androidx.compose.ui.unit.dp // Density-independent pixels for sizes
import androidx.compose.ui.unit.sp // Scale-independent pixels for fonts

// Main Activity class for the Tic Tac Toe game
class TicTacToeActivity : ComponentActivity() {
    // ViewModel to handle the game logic and state
    private val viewModel: TicTacToeViewModel by viewModels()

    // Called when the Activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the UI of the activity using Composable functions
        setContent {
            // Surface provides the background color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(), // Makes the background cover the full screen
                color = MaterialTheme.colorScheme.background // Sets background color from theme
            ) {
                TicTacToeGame(viewModel) // Calls the main game Composable
            }
        }
    }
}

@Composable
fun TicTacToeGame(viewModel: TicTacToeViewModel) {
    // Access game state and logic from the ViewModel
    val board = viewModel.board
    val status = viewModel.status

    // Main layout container (Vertical)
    Column(
        modifier = Modifier
            .fillMaxSize() // Fills entire parent height/width
            .padding(16.dp), // Adds 16dp padding around edges
        horizontalAlignment = Alignment.CenterHorizontally, // Centers items horizontally
        verticalArrangement = Arrangement.Center // Centers items vertically
    ) {
        // Game Title
        Text(text = "Tic Tac Toe", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        // Vertical spacing
        Spacer(modifier = Modifier.height(24.dp))
        // Game status text (Current player or winner)
        Text(text = status, fontSize = 24.sp)
        // Vertical spacing
        Spacer(modifier = Modifier.height(24.dp))

        // Grid container (3 rows)
        Column {
            for (row in 0..2) {
                // Horizontal row container
                Row {
                    for (col in 0..2) {
                        // Individual square button
                        Square(
                            value = board[row][col], // Current value from 2D list
                            onClick = { viewModel.onSquareClick(row, col) } // Handle click via ViewModel with 2D coordinates
                        )
                    }
                }
            }
        }

        // Vertical spacing
        Spacer(modifier = Modifier.height(32.dp))
        // Button to restart the game
        Button(onClick = { viewModel.resetGame() }) {
            Text("Reset Game") // Label on the button
        }
    }
}

// Composable representing a single cell in the grid
@Composable
fun Square(value: String, onClick: () -> Unit) {
    // Outlined button used as a game cell
    OutlinedButton(
        onClick = onClick, // Action performed when clicked
        modifier = Modifier
            .size(100.dp) // Fixed size of 100x100 dp
            .padding(4.dp), // Spacing between cells
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary), // primary color border
        shape = MaterialTheme.shapes.small // Slightly rounded corners
    ) {
        // Displays 'X' or 'O' inside the cell
        Text(
            text = value,
            fontSize = 40.sp, // Large font size
            fontWeight = FontWeight.Bold,
            color = if (value == "X") Color.Blue else Color.Red // Color differentiation for players
        )
    }
}
