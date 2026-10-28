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
import com.example.visualtaskmgr.repository.TaskRepository
import com.google.firebase.firestore.ListenerRegistration
import java.time.LocalDateTime

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()
    private var listenerRegistration: ListenerRegistration? = null

    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks

    var sortOption by mutableStateOf(SortOption.DEFAULT)
    var themeOption by mutableStateOf(ThemeOption.SYSTEM)

    init {
        loadTasksFromFirestore()
        startListeningToFirestore()
    }

    fun loadTasksFromFirestore() {
        repository.fetchTasks(
            onSuccess = { loadedTasks ->
                _tasks.clear()
                _tasks.addAll(loadedTasks)
            }
        )
    }

    fun startListeningToFirestore() {
        listenerRegistration?.remove()
        listenerRegistration = repository.listenToTasks(
            onSuccess = { updatedTasks ->
                _tasks.clear()
                _tasks.addAll(updatedTasks)
            }
        )
    }

    val sortedTasks: List<Task>
        get() = when (sortOption) {
            SortOption.DEFAULT -> _tasks
            SortOption.PRIORITY_HIGH_TO_LOW -> _tasks.sortedByDescending { it.priority.ordinal }
            SortOption.ALPHABETICAL -> _tasks.sortedBy { it.title.lowercase() }
            SortOption.COMPLETED_LAST -> _tasks.sortedBy { it.isCompleted }
        }

    fun getTaskById(taskId: String): Task? {
        return _tasks.find { it.id == taskId }
    }

    fun addTask(
        title: String,
        description: String = "",
        priority: Priority = Priority.MEDIUM,
        targetDateTime: LocalDateTime = LocalDateTime.now().plusDays(1)
    ) {
        if (title.isNotBlank()) {
            repository.addTask(
                title = title,
                description = description,
                priority = priority,
                targetDateTime = targetDateTime
            )
        }
    }

    fun removeTask(task: Task) {
        repository.deleteTask(task.id)
    }

    fun toggleTaskCompletion(task: Task) {
        task.isCompleted = !task.isCompleted
        repository.saveTask(task)
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
            repository.saveTask(task)
        }
    }

    fun clearAllTasks() {
        repository.clearAllTasks()
    }

    fun resetSettings() {
        sortOption = SortOption.DEFAULT
        themeOption = ThemeOption.SYSTEM
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}
