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
    // ── Auth flow ──────────────────────────────────────────────
    object Splash : Screen("splash")
    object Login  : Screen("login")

    // ── Bottom nav tabs ────────────────────────────────────────
    object Dashboard : Screen("dashboard", Icons.Default.Home,        "Dashboard")
    object Tasks     : Screen("tasks",     Icons.Default.CheckCircle, "Tasks")
    object Schedule  : Screen("schedule",  Icons.Default.DateRange,   "Schedule")
    object Settings  : Screen("settings",  Icons.Default.Settings,    "Settings")

    // ── Task add/edit ──────────────────────────────────────────
    object AddTask : Screen("add_task") {
        val routeWithArgs = "add_task?taskId={taskId}"
        val newTaskRoute  = "add_task?taskId=-1"        // ← add this
        val args = listOf(
            navArgument("taskId") {
                type = NavType.IntType
                defaultValue = -1
            }
        )
        fun editRoute(taskId: Int) = "add_task?taskId=$taskId"
    }
}