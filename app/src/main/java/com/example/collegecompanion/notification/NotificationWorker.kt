// com/example/collegecompanion/notification/NotificationWorker.kt
package com.example.collegecompanion.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.collegecompanion.data.preferences.SettingsDataStore
import com.example.collegecompanion.data.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneId

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val taskRepository: TaskRepository,
    private val settingsDataStore: SettingsDataStore
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val settings   = settingsDataStore.notificationSettings.first()
        val daysBefore = settings.daysBefore
        val today      = LocalDate.now()

        // Get all incomplete tasks with a due date
        val tasks = taskRepository.getAllTasks().first()
            .filter { !it.isCompleted && it.dueDate != null }

        tasks.forEachIndexed { index, task ->
            val dueDate = task.dueDate!! // safe — filtered above
            val dueDateLocal = java.time.Instant
                .ofEpochMilli(dueDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            val daysUntilDue = today.until(dueDateLocal, java.time.temporal.ChronoUnit.DAYS)

            // Notify if due date falls within the user's chosen window
            // e.g. daysBefore=2 → notify when 2 days left AND 1 day left AND 0 days (today)
            if (daysUntilDue in 0..daysBefore.toLong()) {
                NotificationHelper.showDeadlineNotification(
                    context   = applicationContext,
                    taskTitle = task.title,
                    daysLeft  = daysUntilDue.toInt(),
                    notifId   = task.id  // unique per task so notifications don't overwrite
                )
            }
        }

        return Result.success()
    }
}