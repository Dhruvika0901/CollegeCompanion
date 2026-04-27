package com.example.collegecompanion.data.geofence

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "campus_location")

data class CampusLocation(
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float
)

@Singleton
class CampusLocationStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_LAT    = doublePreferencesKey("campus_lat")
        private val KEY_LNG    = doublePreferencesKey("campus_lng")
        private val KEY_RADIUS = floatPreferencesKey("campus_radius")
    }

    val campusLocation: Flow<CampusLocation?> = context.dataStore.data.map { prefs ->
        val lat    = prefs[KEY_LAT]    ?: return@map null
        val lng    = prefs[KEY_LNG]    ?: return@map null
        val radius = prefs[KEY_RADIUS] ?: 200f
        CampusLocation(lat, lng, radius)
    }

    suspend fun save(location: CampusLocation) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAT]    = location.latitude
            prefs[KEY_LNG]    = location.longitude
            prefs[KEY_RADIUS] = location.radiusMeters
        }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_LAT)
            prefs.remove(KEY_LNG)
            prefs.remove(KEY_RADIUS)
        }
    }
}