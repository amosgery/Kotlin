package com.example.myfirstapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TicTacToeViewModel : ViewModel() {
    // State to hold the board: a 3x3 grid (2D list)
    var board by mutableStateOf(List(3) { List(3) { "" } })
        private set

    // State to track whose turn it is: true for 'X', false for 'O'
    var xIsNext by mutableStateOf(true)
        private set

    // Determines the status text displayed to the user
    val status: String
        get() {
            val winner = calculateWinner(board)
            return when {
                winner != null -> "Winner: $winner"
                board.all { row -> row.all { it.isNotEmpty() } } -> "Draw!"
                else -> "Next player: ${if (xIsNext) "X" else "O"}"
            }
        }

    val winner: String?
        get() = calculateWinner(board)

    fun onSquareClick(row: Int, col: Int) {
        if (board[row][col].isEmpty() && winner == null) {
            // Create a deep copy of the 2D list to trigger state update
            val newBoard = board.map { it.toMutableList() }
            newBoard[row][col] = if (xIsNext) "X" else "O"
            board = newBoard
            xIsNext = !xIsNext
        }
    }

    fun resetGame() {
        board = List(3) { List(3) { "" } }
        xIsNext = true
    }

    private fun calculateWinner(grid: List<List<String>>): String? {
        // Check Rows
        for (row in 0..2) {
            if (grid[row][0].isNotEmpty() && grid[row][0] == grid[row][1] && grid[row][0] == grid[row][2]) {
                return grid[row][0]
            }
        }
        // Check Columns
        for (col in 0..2) {
            if (grid[0][col].isNotEmpty() && grid[0][col] == grid[1][col] && grid[0][col] == grid[2][col]) {
                return grid[0][col]
            }
        }
        // Check Diagonals
        if (grid[0][0].isNotEmpty() && grid[0][0] == grid[1][1] && grid[0][0] == grid[2][2]) {
            return grid[0][0]
        }
        if (grid[0][2].isNotEmpty() && grid[0][2] == grid[1][1] && grid[0][2] == grid[2][0]) {
            return grid[0][2]
        }
        return null
    }
}
