package com.example.visualtaskmgr.repository

import com.example.visualtaskmgr.model.Priority
import com.example.visualtaskmgr.model.Task
import com.google.firebase.firestore.DocumentSnapshot
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
                convertToTask(doc)
            }

            onSuccess(tasksList)
        }
    }

    fun convertToTask(doc: DocumentSnapshot) : Task?
    {
        try {
            val idString = doc.id
            val title = doc.getString("title") ?: return null
            val description = doc.getString("description") ?: ""
            val isCompleted = doc.getBoolean("isCompleted") ?: false
            val priorityString = doc.getString("priority") ?: Priority.MEDIUM.name
            val priority = try {
                Priority.valueOf(priorityString)
            } catch (e: Exception) {
                Priority.MEDIUM
            }
            val targetDateTimeString = doc.getString("targetDateTime")
            val targetDateTime = if (!targetDateTimeString.isNullOrEmpty()) {
                try {
                    LocalDateTime.parse(targetDateTimeString)
                } catch (e: Exception) {
                    LocalDateTime.now().plusDays(1)
                }
            } else {
                LocalDateTime.now().plusDays(1)
            }

            return Task(
                id = idString,
                title = title,
                description = description,
                isCompleted = isCompleted,
                priority = priority,
                targetDateTime = targetDateTime
            )
        } catch (e: Exception) {
            return null
        }
    }
    /**
     * One-time fetch of tasks from Firestore.
     */
    fun fetchTasks(onSuccess: (List<Task>) -> Unit, onFailure: ((Exception) -> Unit)? = null) {
        tasksCollection.get()
            .addOnSuccessListener { snapshot ->
                val tasksList = snapshot.documents.mapNotNull { doc ->
                    convertToTask(doc)
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
        val taskData = mapOf(
            "title" to title,
            "description" to description,
            "isCompleted" to false,
            "priority" to priority.name,
            "targetDateTime" to targetDateTime.toString()
        )

        tasksCollection.add(taskData)
    }

    /**
     * Saves or updates an existing task in Firestore by document ID.
     */
    fun saveTask(task: Task) {
        val taskData = mapOf(
            "title" to task.title,
            "description" to task.description,
            "isCompleted" to task.isCompleted,
            "priority" to task.priority.name,
            "targetDateTime" to task.targetDateTime.toString()
        )

        tasksCollection.document(task.id).set(taskData)
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
