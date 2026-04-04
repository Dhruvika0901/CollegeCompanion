package com.example.collegecompanion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val icon: ImageVector? = null,
    val label: String = ""
) {
    object Dashboard : Screen("dashboard", Icons.Default.Home,         "Dashboard")
    object Tasks     : Screen("tasks",     Icons.Default.CheckCircle,  "Tasks")
    object Schedule  : Screen("schedule",  Icons.Default.DateRange,    "Schedule")
    object Settings  : Screen("settings",  Icons.Default.Settings,     "Settings")

    object AddTask : Screen("add_task?taskId={taskId}") {
        val args = listOf(
            navArgument("taskId") {
                type         = NavType.LongType
                defaultValue = -1L
            }
        )
    }
}