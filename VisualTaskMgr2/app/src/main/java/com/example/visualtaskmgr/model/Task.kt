package com.example.visualtaskmgr.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDateTime
import java.util.UUID

enum class Priority {
    LOW, MEDIUM, HIGH
}

enum class SortOption(val displayName: String) {
    DEFAULT("ברירת מחדל"),
    PRIORITY_HIGH_TO_LOW("עדיפות (גבוהה לנמוכה)"),
    ALPHABETICAL("לפי א'-ב'"),
    COMPLETED_LAST("לא הושלמו תחילה")
}

enum class ThemeOption(val displayName: String) {
    SYSTEM("מערכת"),
    LIGHT("בהיר"),
    DARK("כהה")
}

class Task(
    val id: String = UUID.randomUUID().toString(),
    title: String,
    description: String = "",
    isCompleted: Boolean = false,
    priority: Priority = Priority.MEDIUM,
    targetDateTime: LocalDateTime = LocalDateTime.now().plusDays(1)
) {
    var title by mutableStateOf(title)
    var description by mutableStateOf(description)
    var isCompleted by mutableStateOf(isCompleted)
    var priority by mutableStateOf(priority)
    var targetDateTime by mutableStateOf(targetDateTime)
}
