package com.example.visualtaskmgr.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.visualtaskmgr.model.Priority
import com.example.visualtaskmgr.model.Task
import com.example.visualtaskmgr.view.theme.VisualTaskMgrTheme
import com.example.visualtaskmgr.viewmodel.TaskViewModel

@Composable
fun TaskScreen(viewModel: TaskViewModel = viewModel()) {
    TaskScreenContent(
        tasks = viewModel.tasks,
        onAddTask = { title, priority -> viewModel.addTask(title, priority) },
        onToggleTask = { viewModel.toggleTaskCompletion(it) },
        onDeleteTask = { viewModel.removeTask(it) },
        onEditTask = { task, newTitle, newPriority -> viewModel.updateTask(task, newTitle, newPriority) }
    )
}

@Composable
fun TaskScreenContent(
    tasks: List<Task>,
    onAddTask: (String, Priority) -> Unit = { _, _ -> },
    onToggleTask: (Task) -> Unit = {},
    onDeleteTask: (Task) -> Unit = {},
    onEditTask: (Task, String, Priority) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    var taskTitle by remember { mutableStateOf("") }
    var newTaskPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    val task = taskToEdit
    if (task != null) {
        var editedTitle by remember(task) { mutableStateOf(task.title) }
        var editedPriority by remember(task) { mutableStateOf(task.priority) }

        AlertDialog(
            onDismissRequest = { taskToEdit = null },
            title = { Text(text = "Edit Task") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        label = { Text("Task Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(text = "Priority", style = MaterialTheme.typography.labelLarge)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Priority.entries.forEach { priority ->
                            FilterChip(
                                selected = editedPriority == priority,
                                onClick = { editedPriority = priority },
                                label = { Text(priority.name.lowercase().replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editedTitle.isNotBlank()) {
                            onEditTask(task, editedTitle.trim(), editedPriority)
                            Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show()
                            taskToEdit = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text(text = "Delete Task") },
            text = { Text(text = "Are you sure you want to delete \"${task.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTask(task)
                        Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show()
                        taskToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    Toast.makeText(context, "Delete cancelled", Toast.LENGTH_SHORT).show()
                    taskToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Task Manager",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = taskTitle,
            onValueChange = { taskTitle = it },
            label = { Text("New Task") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Priority.entries.forEach { priority ->
                    FilterChip(
                        selected = newTaskPriority == priority,
                        onClick = { newTaskPriority = priority },
                        label = { Text(priority.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            Button(onClick = {
                if (taskTitle.isNotBlank()) {
                    onAddTask(taskTitle.trim(), newTaskPriority)
                    taskTitle = ""
                    newTaskPriority = Priority.MEDIUM
                }
            }) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                TaskItem(
                    task = task,
                    onToggle = { onToggleTask(task) },
                    onDelete = { taskToDelete = task },
                    onClick = { taskToEdit = task }
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Text(
                text = task.title,
                modifier = Modifier.weight(1f),
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
            PriorityBadge(priority = task.priority)
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Task")
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: Priority) {
    val (bgColor, contentColor) = when (priority) {
        Priority.HIGH -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        Priority.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        Priority.LOW -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }

    Surface(
        color = bgColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = priority.name.lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    val sampleTasks = listOf(
        Task(title = "Buy groceries", isCompleted = false, priority = Priority.HIGH),
        Task(title = "Finish project", isCompleted = true, priority = Priority.MEDIUM),
        Task(title = "Go for a run", isCompleted = false, priority = Priority.LOW)
    )
    VisualTaskMgrTheme {
        Surface {
            TaskScreenContent(
                tasks = sampleTasks,
                onAddTask = { _, _ -> },
                onToggleTask = {},
                onDeleteTask = {}
            )
        }
    }
}
