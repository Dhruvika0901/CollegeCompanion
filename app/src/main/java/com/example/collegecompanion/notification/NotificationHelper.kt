// com/example/collegecompanion/notification/NotificationHelper.kt
package com.example.collegecompanion.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.collegecompanion.R

object NotificationHelper {

    private const val CHANNEL_ID   = "deadline_reminders"
    private const val CHANNEL_NAME = "Deadline Reminders"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifies you when a task deadline is approaching"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun showDeadlineNotification(context: Context, taskTitle: String, daysLeft: Int, notifId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val body = when (daysLeft) {
            0    -> "\"$taskTitle\" is due TODAY!"
            1    -> "\"$taskTitle\" is due TOMORROW"
            else -> "\"$taskTitle\" is due in $daysLeft days"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("📚 Deadline Reminder")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(notifId, notification)
    }
}