// com/example/collegecompanion/data/preferences/SettingsDataStore.kt
package com.example.collegecompanion.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

data class NotificationSettings(
    val daysBefore: Int,   // 1, 2, 3, or 7
    val hourOfDay: Int,    // 0–23
    val minuteOfDay: Int   // 0 or 30
)

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_DAYS_BEFORE  = intPreferencesKey("days_before")
        val KEY_HOUR         = intPreferencesKey("notif_hour")
        val KEY_MINUTE       = intPreferencesKey("notif_minute")

        // Defaults
        const val DEFAULT_DAYS_BEFORE = 1
        const val DEFAULT_HOUR        = 8
        const val DEFAULT_MINUTE      = 0
    }

    val notificationSettings: Flow<NotificationSettings> = context.dataStore.data
        .map { prefs ->
            NotificationSettings(
                daysBefore  = prefs[KEY_DAYS_BEFORE] ?: DEFAULT_DAYS_BEFORE,
                hourOfDay   = prefs[KEY_HOUR]        ?: DEFAULT_HOUR,
                minuteOfDay = prefs[KEY_MINUTE]      ?: DEFAULT_MINUTE
            )
        }

    suspend fun saveDaysBefore(days: Int) {
        context.dataStore.edit { it[KEY_DAYS_BEFORE] = days }
    }

    suspend fun saveNotificationTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[KEY_HOUR]   = hour
            it[KEY_MINUTE] = minute
        }
    }
}