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
import com.example.visualtaskmgr.view.LoginScreen
import com.example.visualtaskmgr.view.SettingsScreen
import com.example.visualtaskmgr.view.TaskScreen
import com.example.visualtaskmgr.view.theme.VisualTaskMgrTheme
import com.example.visualtaskmgr.viewmodel.AuthViewModel
import com.example.visualtaskmgr.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val taskViewModel: TaskViewModel = viewModel()

            // Dynamic theme selection based on user preference in TaskViewModel
            val isDarkTheme = when (taskViewModel.themeOption) {
                ThemeOption.SYSTEM -> isSystemInDarkTheme()
                ThemeOption.LIGHT -> false
                ThemeOption.DARK -> true
            }

            VisualTaskMgrTheme(darkTheme = isDarkTheme) {
                val currentUser = authViewModel.currentUser

                // AUTH CHECK: If not logged in, show Login/Register Screen
                if (currentUser == null) {
                    LoginScreen(authViewModel = authViewModel)
                } else {
                    // MAIN APP CONTENT: Logged-in user flow
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route ?: "tasks"

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
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
                        NavHost(
                            navController = navController,
                            startDestination = "tasks",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("tasks") {
                                TaskScreen(viewModel = taskViewModel)
                            }

                            composable("settings") {
                                SettingsScreen(
                                    viewModel = taskViewModel,
                                    userEmail = currentUser.email,
                                    onSignOut = {
                                        authViewModel.signOut()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
