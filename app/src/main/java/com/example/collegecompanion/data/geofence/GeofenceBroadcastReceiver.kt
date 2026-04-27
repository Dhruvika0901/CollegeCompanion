//GeofenceBroadcastReceiver.kt
package com.example.collegecompanion.data.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: AttendanceNotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return

        // hasError() is deprecated in newer play-services-location — check error code instead
        if (event.errorCode != GeofenceStatusCodes.SUCCESS) return

        if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            notificationHelper.showAttendancePrompt()
        }
    }
}