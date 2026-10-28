package com.example.visualtaskmgr.repository

import com.example.visualtaskmgr.model.Priority
import com.example.visualtaskmgr.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.time.LocalDateTime

class TaskRepository {
    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")

    /**
     * Attaches a real-time listener to the Firestore "tasks" collection.
     */
    fun listenToTasks(
        onSuccess: (List<Task>) -> Unit,
        onFailure: ((Exception) -> Unit)? = null
    ): ListenerRegistration {
        return tasksCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onFailure?.invoke(error)
                return@addSnapshotListener
            }
            if (snapshot == null) return@addSnapshotListener

            val tasksList = snapshot.documents.mapNotNull { doc ->
                if (doc.contains("title")) Task(doc) else null
            }

            onSuccess(tasksList)
        }
    }

    /**
     * One-time fetch of tasks from Firestore.
     */
    fun fetchTasks(onSuccess: (List<Task>) -> Unit, onFailure: ((Exception) -> Unit)? = null) {
        tasksCollection.get()
            .addOnSuccessListener { snapshot ->
                val tasksList = snapshot.documents.mapNotNull { doc ->
                    if (doc.contains("title")) Task(doc) else null
                }
                onSuccess(tasksList)
            }
            .addOnFailureListener { exception ->
                onFailure?.invoke(exception)
            }
    }

    /**
     * Adds a new task to Firestore using add() so Firestore generates the ID automatically.
     */
    fun addTask(
        title: String,
        description: String = "",
        priority: Priority = Priority.MEDIUM,
        targetDateTime: LocalDateTime = LocalDateTime.now().plusDays(1)
    ) {
        val newTask = Task(
            title = title,
            description = description,
            priority = priority,
            targetDateTime = targetDateTime
        )
        tasksCollection.add(newTask.toMap())
    }

    /**
     * Saves or updates an existing task in Firestore by document ID.
     */
    fun saveTask(task: Task) {
        tasksCollection.document(task.id).set(task.toMap())
    }

    /**
     * Deletes a task from Firestore by ID.
     */
    fun deleteTask(taskId: String) {
        tasksCollection.document(taskId).delete()
    }

    /**
     * Deletes all tasks from Firestore.
     */
    fun clearAllTasks() {
        tasksCollection.get().addOnSuccessListener { snapshot ->
            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit()
        }
    }
}
