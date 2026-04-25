//NotificationWorker
package com.example.collegecompanion.notification

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.collegecompanion.data.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.first

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskRepository: TaskRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        android.util.Log.d("NotifWorker", "🔥 WORKER EXECUTED")

        val today = LocalDate.now()
        val tasks = taskRepository.getAllTasks().first()

        val filtered = tasks
            .filter { !it.isCompleted }
            .filter { it.dueDate != null }
            .filter { it.reminderDaysBefore != null }

        filtered.forEach { task ->

            val dueDateLocal = Instant.ofEpochMilli(task.dueDate!!)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            val daysUntilDue = ChronoUnit.DAYS.between(today, dueDateLocal).toInt()

            // ✅ THIS IS THE REAL FIX
            if (daysUntilDue == task.reminderDaysBefore) {

                android.util.Log.d("NotifWorker", "🔥 MATCH FOUND: ${task.title}")

                notificationHelper.showTaskReminder(
                    taskId = task.id,
                    title = task.title,
                    daysUntilDue = daysUntilDue
                )
            }
        }

        return Result.success()
    }
}