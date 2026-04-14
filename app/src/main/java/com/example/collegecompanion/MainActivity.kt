// com/example/collegecompanion/MainActivity.kt
package com.example.collegecompanion

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.collegecompanion.notification.NotificationHelper
import com.example.collegecompanion.notification.NotificationScheduler
import com.example.collegecompanion.ui.navigation.CollegeCompanionApp
import com.example.collegecompanion.ui.theme.CollegeCompanionTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var notificationScheduler: NotificationScheduler

    // Permission launcher for Android 13+
    private val notifPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // Permission just granted — start the default schedule (8:00 AM, 1 day before)
            notificationScheduler.schedule(hourOfDay = 8, minuteOfDay = 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Create notification channel (safe to call multiple times)
        NotificationHelper.createChannel(this)

        // 2. Request POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Below Android 13, permission not needed — schedule directly
            notificationScheduler.schedule(hourOfDay = 8, minuteOfDay = 0)
        }

        setContent {
            CollegeCompanionTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CollegeCompanionApp()
                }
            }
        }
    }
}