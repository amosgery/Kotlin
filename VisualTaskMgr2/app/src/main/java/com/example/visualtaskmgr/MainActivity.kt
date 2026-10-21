package com.example.visualtaskmgr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.visualtaskmgr.model.ThemeOption
import com.example.visualtaskmgr.view.SettingsScreen
import com.example.visualtaskmgr.view.TaskScreen
import com.example.visualtaskmgr.view.theme.VisualTaskMgrTheme
import com.example.visualtaskmgr.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Obtain a shared instance of TaskViewModel scoped to this Activity.
            val taskViewModel: TaskViewModel = viewModel()

            // Dynamic theme selection based on user preference in TaskViewModel
            val isDarkTheme = when (taskViewModel.themeOption) {
                ThemeOption.SYSTEM -> isSystemInDarkTheme()
                ThemeOption.LIGHT -> false
                ThemeOption.DARK -> true
            }

            // NAVIGATION WITH NAVCONTROLLER
            // Initialize NavController to manage navigation between screens (Tasks & Settings)
            val navController = rememberNavController()

            // Observe current route from NavController's backstack state
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: "tasks"

            VisualTaskMgrTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // Bottom Navigation Bar using NavController
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute == "tasks",
                                onClick = {
                                    if (currentRoute != "tasks") {
                                        navController.navigate("tasks") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "משימות") },
                                label = { Text("משימות") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "settings",
                                onClick = {
                                    if (currentRoute != "settings") {
                                        navController.navigate("settings") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "הגדרות") },
                                label = { Text("הגדרות") }
                            )
                        }
                    }
                ) { innerPadding ->
                    // NavHost defines navigation destinations managed by NavController
                    NavHost(
                        navController = navController,
                        startDestination = "tasks",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Main Tasks Screen destination (uses Dialog internally for editing task details)
                        composable("tasks") {
                            TaskScreen(viewModel = taskViewModel)
                        }

                        // Settings Screen destination
                        composable("settings") {
                            SettingsScreen(viewModel = taskViewModel)
                        }
                    }
                }
            }
        }
    }
}
