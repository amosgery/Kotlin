package com.example.visualtaskmgr.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.UUID

enum class Priority {
    LOW, MEDIUM, HIGH
}

class Task(
    val id: UUID = UUID.randomUUID(),
    title: String,
    isCompleted: Boolean = false,
    priority: Priority = Priority.MEDIUM
) {
    var title by mutableStateOf(title)
    var isCompleted by mutableStateOf(isCompleted)
    var priority by mutableStateOf(priority)
}

