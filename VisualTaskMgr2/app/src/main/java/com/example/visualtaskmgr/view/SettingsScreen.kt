package com.example.visualtaskmgr.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.visualtaskmgr.model.SortOption
import com.example.visualtaskmgr.model.ThemeOption
import com.example.visualtaskmgr.view.theme.VisualTaskMgrTheme
import com.example.visualtaskmgr.viewmodel.TaskViewModel

@Composable
fun SettingsScreen(viewModel: TaskViewModel = viewModel()) {
    SettingsScreenContent(
        themeOption = viewModel.themeOption,
        sortOption = viewModel.sortOption,
        onThemeOptionSelected = { viewModel.themeOption = it },
        onSortOptionSelected = { viewModel.sortOption = it },
        onClearAllTasks = { viewModel.clearAllTasks() },
        onResetSettings = { viewModel.resetSettings() }
    )
}

@Composable
fun SettingsScreenContent(
    themeOption: ThemeOption,
    sortOption: SortOption,
    onThemeOptionSelected: (ThemeOption) -> Unit = {},
    onSortOptionSelected: (SortOption) -> Unit = {},
    onClearAllTasks: () -> Unit = {},
    onResetSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    var showClearTasksDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    if (showClearTasksDialog) {
        AlertDialog(
            onDismissRequest = { showClearTasksDialog = false },
            title = { Text("מחיקת כל המשימות") },
            text = { Text("האם אתה בטוח שברצונך למחוק את כל המשימות? פעולה זו אינה ניתנת לבטול.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllTasks()
                        Toast.makeText(context, "כל המשימות שנמחקו", Toast.LENGTH_SHORT).show()
                        showClearTasksDialog = false
                    }
                ) {
                    Text("מחק הכל", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearTasksDialog = false }) {
                    Text("ביטול")
                }
            }
        )
    }

    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text("איפוס הגדרות") },
            text = { Text("האם להחזיר את הגדרות התצוגה והמיון למצב ברירת המחדל?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetSettings()
                        Toast.makeText(context, "ההגדרות אופסו", Toast.LENGTH_SHORT).show()
                        showResetSettingsDialog = false
                    }
                ) {
                    Text("אפס הגדרות")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text("ביטול")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "הגדרות",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Section 1: Display / Theme
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "תצוגה (Theme)",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ThemeOption.entries.forEach { option ->
                        FilterChip(
                            selected = themeOption == option,
                            onClick = { onThemeOptionSelected(option) },
                            label = { Text(option.displayName) }
                        )
                    }
                }
            }
        }

        // Section 2: Sorting
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "מיון משימות",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SortOption.entries.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSortOptionSelected(option) }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = sortOption == option,
                            onClick = { onSortOptionSelected(option) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        // Section 3: Reset Actions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "איפוס ופעולות",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedButton(
                    onClick = { showResetSettingsDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("איפוס הגדרות תצוגה ומיון")
                }

                Button(
                    onClick = { showClearTasksDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("מחיקת כל המשימות")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    VisualTaskMgrTheme {
        Surface {
            SettingsScreenContent(
                themeOption = ThemeOption.SYSTEM,
                sortOption = SortOption.DEFAULT
            )
        }
    }
}
