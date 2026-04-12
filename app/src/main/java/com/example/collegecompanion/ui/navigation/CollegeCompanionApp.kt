package com.example.collegecompanion.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.collegecompanion.ui.screens.dashboard.DashboardScreen
import com.example.collegecompanion.ui.screens.login.LoginScreen
import com.example.collegecompanion.ui.screens.schedule.ScheduleScreen
import com.example.collegecompanion.ui.screens.settings.SettingsScreen
import com.example.collegecompanion.ui.screens.splash.SplashScreen
import com.example.collegecompanion.ui.tasks.AddEditTaskScreen
import com.example.collegecompanion.ui.tasks.TaskListScreen

@Composable
fun CollegeCompanionApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
            startDestination = Screen.Splash.route,  // ← changed from Dashboard
            modifier         = Modifier.padding(innerPadding)
        ) {
            // ── Auth flow ──────────────────────────────────────────────
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToDashboard = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // ── Main app ───────────────────────────────────────────────
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToTasks      = { navController.navigate(Screen.Tasks.route) },
                    onNavigateToAttendance = { /* wire up later */ }
                )
            }
            composable(Screen.Tasks.route) {
                TaskListScreen(navController = navController)
            }
            composable(
                route     = Screen.AddTask.routeWithArgs,  // ← uses routeWithArgs now
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