package com.example.visualtaskmgr.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.visualtaskmgr.model.Priority
import com.example.visualtaskmgr.model.SortOption
import com.example.visualtaskmgr.model.Task
import com.example.visualtaskmgr.model.ThemeOption
import java.time.LocalDateTime

class TaskViewModel : ViewModel() {
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks

    var sortOption by mutableStateOf(SortOption.DEFAULT)
    var themeOption by mutableStateOf(ThemeOption.SYSTEM)

    val sortedTasks: List<Task>
        get() = when (sortOption) {
            SortOption.DEFAULT -> _tasks
            SortOption.PRIORITY_HIGH_TO_LOW -> _tasks.sortedByDescending { it.priority.ordinal }
            SortOption.ALPHABETICAL -> _tasks.sortedBy { it.title.lowercase() }
            SortOption.COMPLETED_LAST -> _tasks.sortedBy { it.isCompleted }
        }

    fun getTaskById(taskId: String): Task? {
        return _tasks.find { it.id.toString() == taskId }
    }

    fun addTask(
        title: String,
        description: String = "",
        priority: Priority = Priority.MEDIUM,
        targetDateTime: LocalDateTime = LocalDateTime.now().plusDays(1)
    ) {
        if (title.isNotBlank()) {
            _tasks.add(
                Task(
                    title = title,
                    description = description,
                    priority = priority,
                    targetDateTime = targetDateTime
                )
            )
        }
    }

    fun removeTask(task: Task) {
        _tasks.remove(task)
    }

    fun toggleTaskCompletion(task: Task) {
        task.isCompleted = !task.isCompleted
    }

    fun updateTask(
        task: Task,
        newTitle: String,
        newDescription: String = task.description,
        newPriority: Priority,
        newTargetDateTime: LocalDateTime
    ) {
        if (newTitle.isNotBlank()) {
            task.title = newTitle
            task.description = newDescription
            task.priority = newPriority
            task.targetDateTime = newTargetDateTime
        }
    }

    fun clearAllTasks() {
        _tasks.clear()
    }

    fun resetSettings() {
        sortOption = SortOption.DEFAULT
        themeOption = ThemeOption.SYSTEM
    }
}
