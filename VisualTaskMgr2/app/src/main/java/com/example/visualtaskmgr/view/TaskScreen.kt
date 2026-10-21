package com.example.visualtaskmgr.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.visualtaskmgr.model.Priority
import com.example.visualtaskmgr.model.Task
import com.example.visualtaskmgr.view.theme.VisualTaskMgrTheme
import com.example.visualtaskmgr.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TaskScreen(viewModel: TaskViewModel = viewModel()) {
    TaskScreenContent(
        tasks = viewModel.sortedTasks,
        onAddTask = { title, priority, targetDateTime -> viewModel.addTask(title, priority = priority, targetDateTime = targetDateTime) },
        onToggleTask = { viewModel.toggleTaskCompletion(it) },
        onDeleteTask = { viewModel.removeTask(it) },
        onEditTask = { task, newTitle, newDescription, newPriority, newTargetDateTime ->
            viewModel.updateTask(task, newTitle, newDescription, newPriority, newTargetDateTime)
        }
    )
}

@Composable
fun TaskScreenContent(
    tasks: List<Task>,
    onAddTask: (String, Priority, LocalDateTime) -> Unit = { _, _, _ -> },
    onToggleTask: (Task) -> Unit = {},
    onDeleteTask: (Task) -> Unit = {},
    onEditTask: (Task, String, String, Priority, LocalDateTime) -> Unit = { _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    val dateTimeFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm") }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    var taskTitle by remember { mutableStateOf("") }
    var newTaskPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var newTaskTargetDateTime by remember { mutableStateOf(LocalDateTime.now().plusDays(1)) }

    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    // Edit Task Dialog
    val task = taskToEdit
    if (task != null) {
        var editedTitle by remember(task) { mutableStateOf(task.title) }
        var editedDescription by remember(task) { mutableStateOf(task.description) }
        var editedPriority by remember(task) { mutableStateOf(task.priority) }
        var editedTargetDateTime by remember(task) { mutableStateOf(task.targetDateTime) }

        Dialog(
            onDismissRequest = { taskToEdit = null }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Edit Task Details",
                        style = MaterialTheme.typography.titleLarge
                    )

                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        label = { Text("Task Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editedDescription,
                        onValueChange = { editedDescription = it },
                        label = { Text("Description / Notes") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Priority", style = MaterialTheme.typography.labelLarge)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Priority.entries.forEach { priority ->
                                FilterChip(
                                    selected = editedPriority == priority,
                                    onClick = { editedPriority = priority },
                                    label = {
                                        Text(
                                            priority.name.lowercase()
                                                .replaceFirstChar { it.uppercase() }
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Target Completion Date & Time", style = MaterialTheme.typography.labelLarge)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            val newDate = LocalDate.of(year, month + 1, dayOfMonth)
                                            editedTargetDateTime = LocalDateTime.of(newDate, editedTargetDateTime.toLocalTime())
                                        },
                                        editedTargetDateTime.year,
                                        editedTargetDateTime.monthValue - 1,
                                        editedTargetDateTime.dayOfMonth
                                    ).show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(editedTargetDateTime.format(dateFormatter))
                            }

                            OutlinedButton(
                                onClick = {
                                    TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            val newTime = LocalTime.of(hourOfDay, minute)
                                            editedTargetDateTime = LocalDateTime.of(editedTargetDateTime.toLocalDate(), newTime)
                                        },
                                        editedTargetDateTime.hour,
                                        editedTargetDateTime.minute,
                                        true
                                    ).show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(editedTargetDateTime.format(timeFormatter))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { taskToEdit = null }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (editedTitle.isNotBlank()) {
                                    onEditTask(
                                        task,
                                        editedTitle.trim(),
                                        editedDescription.trim(),
                                        editedPriority,
                                        editedTargetDateTime
                                    )
                                    Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show()
                                    taskToEdit = null
                                }
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text(text = "Delete Task") },
            text = { Text(text = "Are you sure you want to delete \"${task.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTask(task)
                        Toast.makeText(context, "Task deleted", Toast.LENGTH_LONG).show()
                        taskToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    Toast.makeText(context, "Delete cancelled", Toast.LENGTH_LONG).show()
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
            label = { Text("New Task Title") },
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
                    onAddTask(taskTitle.trim(), newTaskPriority, newTaskTargetDateTime)
                    taskTitle = ""
                    newTaskPriority = Priority.MEDIUM
                    newTaskTargetDateTime = LocalDateTime.now().plusDays(1)
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
                    dateTimeFormatter = dateTimeFormatter,
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
    dateTimeFormatter: DateTimeFormatter,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Target: ${task.targetDateTime.format(dateTimeFormatter)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
                onAddTask = { _, _, _ -> },
                onToggleTask = {},
                onDeleteTask = {}
            )
        }
    }
}
