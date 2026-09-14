package com.example.visualtaskmgr.model

import java.util.UUID

data class Task(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val isCompleted: Boolean = false
)
