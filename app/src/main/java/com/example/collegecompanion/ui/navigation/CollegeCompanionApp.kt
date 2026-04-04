package com.example.collegecompanion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.collegecompanion.ui.screens.dashboard.DashboardScreen
import com.example.collegecompanion.ui.screens.schedule.ScheduleScreen
import com.example.collegecompanion.ui.screens.settings.SettingsScreen
import com.example.collegecompanion.ui.tasks.AddEditTaskScreen
import com.example.collegecompanion.ui.tasks.TaskListScreen

@Composable
fun CollegeCompanionApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Bottom nav only shows on these routes
    val bottomNavRoutes = listOf(
        Screen.Dashboard.route,
        Screen.Tasks.route,
        Screen.Schedule.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Dashboard.route,
            modifier         = Modifier
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen()
            }
            composable(Screen.Tasks.route) {
                TaskListScreen(
                    onAddTask  = { navController.navigate("add_task?taskId=-1") },
                    onEditTask = { id -> navController.navigate("add_task?taskId=$id") }
                )
            }
            composable(
                route     = Screen.AddTask.route,
                arguments = Screen.AddTask.args
            ) {
                AddEditTaskScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Schedule.route) { ScheduleScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        Screen.Dashboard,
        Screen.Tasks,
        Screen.Schedule,
        Screen.Settings
    )

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick  = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon  = { Icon(screen.icon!!, contentDescription = screen.label) },
                label = { Text(screen.label) }
            )
        }
    }
}