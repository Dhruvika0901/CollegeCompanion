package com.example.collegecompanion.ui.screens.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegecompanion.data.preferences.SettingsDataStore
import com.example.collegecompanion.notification.NotificationScheduler
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userEmail: String? = null,
    val daysBefore: Int = 1,
    val hourOfDay: Int = 8,
    val minuteOfDay: Int = 0,
    val locationEnabled: Boolean = false,
    val currentLocation: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val settingsDataStore: SettingsDataStore,
    private val scheduler: NotificationScheduler,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _signOutEvent = Channel<Unit>()
    val signOutEvent = _signOutEvent.receiveAsFlow()

    private val _locationText = MutableStateFlow<String?>(null)
    private val _locationEnabled = MutableStateFlow(false)

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsDataStore.notificationSettings,
        _locationText,
        _locationEnabled
    ) { settings, location, locEnabled ->
        SettingsUiState(
            userEmail = auth.currentUser?.email,
            daysBefore = settings.daysBefore,
            hourOfDay = settings.hourOfDay,
            minuteOfDay = settings.minuteOfDay,
            locationEnabled = locEnabled,
            currentLocation = location
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

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        val cts = CancellationTokenSource()
        _locationEnabled.value = true

        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener { loc ->
                _locationText.value = if (loc != null)
                    "%.4f, %.4f".format(loc.latitude, loc.longitude)
                else "Location unavailable"
            }
            .addOnFailureListener {
                _locationText.value = "Failed: ${it.message}"
            }
    }

    fun clearLocation() {
        _locationEnabled.value = false
        _locationText.value = null
    }
}