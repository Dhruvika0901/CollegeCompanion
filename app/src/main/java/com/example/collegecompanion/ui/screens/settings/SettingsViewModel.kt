package com.example.collegecompanion.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegecompanion.data.preferences.SettingsDataStore
import com.example.collegecompanion.notification.NotificationScheduler
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userEmail: String? = null,
    val daysBefore: Int = 1,
    val hourOfDay: Int = 8,
    val minuteOfDay: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val settingsDataStore: SettingsDataStore,
    private val scheduler: NotificationScheduler
) : ViewModel() {

    // One-time event for navigation
    private val _signOutEvent = Channel<Unit>()
    val signOutEvent = _signOutEvent.receiveAsFlow()

    val uiState: StateFlow<SettingsUiState> = settingsDataStore.notificationSettings
        .map { settings ->
            SettingsUiState(
                userEmail = auth.currentUser?.email,
                daysBefore = settings.daysBefore,
                hourOfDay = settings.hourOfDay,
                minuteOfDay = settings.minuteOfDay
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )

    fun setDaysBefore(days: Int) {
        viewModelScope.launch {
            settingsDataStore.saveDaysBefore(days)
            scheduler.schedule(uiState.value.hourOfDay, uiState.value.minuteOfDay)
        }
    }

    fun setNotificationHour(hour: Int) {
        viewModelScope.launch {
            settingsDataStore.saveNotificationTime(hour, uiState.value.minuteOfDay)
            scheduler.schedule(hour, uiState.value.minuteOfDay)
        }
    }

    fun signOut() {
        auth.signOut()
        viewModelScope.launch {
            _signOutEvent.send(Unit)
        }
    }
}