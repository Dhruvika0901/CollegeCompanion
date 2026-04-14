// com/example/collegecompanion/notification/NotificationScheduler.kt
package com.example.collegecompanion.notification

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val WORK_TAG = "deadline_reminder_work"
    }

    fun schedule(hourOfDay: Int, minuteOfDay: Int) {
        // Calculate delay until next occurrence of the chosen time
        val now    = LocalDateTime.now()
        var target = now.withHour(hourOfDay).withMinute(minuteOfDay).withSecond(0)

        // If that time has already passed today, schedule for tomorrow
        if (!target.isAfter(now)) target = target.plusDays(1)

        val delayMinutes = now.until(target, ChronoUnit.MINUTES)

        val request = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .addTag(WORK_TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        // CANCEL_AND_REENQUEUE replaces any existing schedule with new time
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_TAG,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }

    fun cancel() {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)
    }
}