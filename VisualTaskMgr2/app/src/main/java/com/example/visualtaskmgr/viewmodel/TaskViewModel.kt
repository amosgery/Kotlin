package com.example.visualtaskmgr.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.visualtaskmgr.model.Priority
import com.example.visualtaskmgr.model.Task

class TaskViewModel : ViewModel() {
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks

    fun addTask(title: String, priority: Priority = Priority.MEDIUM) {
        if (title.isNotBlank()) {
            _tasks.add(Task(title = title, priority = priority))
        }
    }

    fun removeTask(task: Task) {
        _tasks.remove(task)
    }

    fun toggleTaskCompletion(task: Task) {
        task.isCompleted = !task.isCompleted
    }

    fun updateTask(task: Task, newTitle: String, newPriority: Priority) {
        if (newTitle.isNotBlank()) {
            task.title = newTitle
            task.priority = newPriority
        }
    }
}

