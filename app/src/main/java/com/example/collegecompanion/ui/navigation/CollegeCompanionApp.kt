package com.example.collegecompanion.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.collegecompanion.ui.screens.dashboard.DashboardScreen
import com.example.collegecompanion.ui.screens.login.LoginScreen
import com.example.collegecompanion.ui.screens.schedule.ScheduleScreen
import com.example.collegecompanion.ui.screens.settings.SettingsScreen
import com.example.collegecompanion.ui.screens.splash.SplashScreen
import com.example.collegecompanion.ui.tasks.AddEditTaskScreen
import com.example.collegecompanion.ui.tasks.TaskListScreen
import com.example.collegecompanion.ui.screens.attendance.AttendanceScreen

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
            startDestination = Screen.Splash.route,
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
                            // Wipes Login from stack so Back button exits app
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // ── Main app ───────────────────────────────────────────────
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToTasks      = { navController.navigate(Screen.Tasks.route) },
                    onNavigateToAttendance = { navController.navigate(Screen.Attendance.route) },
                    onNavigateToSchedule   = { navController.navigate(Screen.Schedule.route) }
                )
            }

            composable(Screen.Tasks.route) {
                TaskListScreen(navController = navController)
            }

            composable(
                route     = Screen.AddTask.routeWithArgs,
                arguments = Screen.AddTask.args
            ) {
                AddEditTaskScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Schedule.route) { ScheduleScreen() }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onSignOut = {
                        navController.navigate(Screen.Login.route) {
                            // Clears entire app graph so user must re-auth
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Attendance.route) {
                AttendanceScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
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
                        // Standard BottomNav behavior: pop to root to avoid stack buildup
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