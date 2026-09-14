package com.example.visualtaskmgr.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.visualtaskmgr.model.Task

class TaskViewModel : ViewModel() {
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks

    fun addTask(title: String) {
        if (title.isNotBlank()) {
            _tasks.add(Task(title = title))
        }
    }

    fun removeTask(task: Task) {
        _tasks.remove(task)
    }

    fun toggleTaskCompletion(task: Task) {
        val index = _tasks.indexOf(task)
        if (index != -1) {
            _tasks[index] = task.copy(isCompleted = !task.isCompleted)
        }
    }
}
