package com.example.visualtaskmgr.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.UUID

class Task(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    isCompleted: Boolean = false
) {
    var isCompleted by mutableStateOf(isCompleted)
}
