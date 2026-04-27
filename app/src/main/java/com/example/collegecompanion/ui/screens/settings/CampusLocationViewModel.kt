//package com.example.collegecompanion.ui.screens.settings
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.location.Location
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.collegecompanion.data.geofence.CampusGeofence
//import com.example.collegecompanion.data.geofence.CampusLocation
//import com.example.collegecompanion.data.geofence.CampusLocationStore
//import com.example.collegecompanion.data.geofence.GeofenceHelper
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.tasks.Task
//import dagger.hilt.android.lifecycle.HiltViewModel
//import dagger.hilt.android.qualifiers.ApplicationContext
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//data class CampusLocationUiState(
//    val saved: CampusLocation? = null,
//    val latInput: String       = "",
//    val lngInput: String       = "",
//    val radiusInput: String    = "200",
//    val isFetchingGps: Boolean = false,
//    val saveSuccess: Boolean   = false,
//    val error: String?         = null
//)
//
//@HiltViewModel
//class CampusLocationViewModel @Inject constructor(
//    @ApplicationContext private val context: Context,
//    private val store: CampusLocationStore,
//    private val geofenceHelper: GeofenceHelper
//) : ViewModel() {
//
//    private val _ui = MutableStateFlow(CampusLocationUiState())
//    val uiState: StateFlow<CampusLocationUiState> = _ui.asStateFlow()
//
//    // FusedLocationProviderClient — only constructed when needed
//    private val fusedClient: FusedLocationProviderClient by lazy {
//        LocationServices.getFusedLocationProviderClient(context)
//    }
//
//    init {
//        viewModelScope.launch {
//            store.campusLocation.collect { saved ->
//                _ui.update { s ->
//                    s.copy(
//                        saved       = saved,
//                        latInput    = saved?.latitude?.toString()          ?: "",
//                        lngInput    = saved?.longitude?.toString()         ?: "",
//                        radiusInput = saved?.radiusMeters?.toInt()?.toString() ?: "200"
//                    )
//                }
//            }
//        }
//    }
//
//    fun onLatChange(v: String)    { _ui.update { it.copy(latInput    = v, error = null) } }
//    fun onLngChange(v: String)    { _ui.update { it.copy(lngInput    = v, error = null) } }
//    fun onRadiusChange(v: String) { _ui.update { it.copy(radiusInput = v, error = null) } }
//
//    fun saveManual() {
//        val lat    = _ui.value.latInput.toDoubleOrNull()
//        val lng    = _ui.value.lngInput.toDoubleOrNull()
//        val radius = _ui.value.radiusInput.toFloatOrNull()
//
//        if (lat == null || lng == null) {
//            _ui.update { it.copy(error = "Please enter valid coordinates") }
//            return
//        }
//        if (lat !in -90.0..90.0 || lng !in -180.0..180.0) {
//            _ui.update { it.copy(error = "Coordinates out of range") }
//            return
//        }
//        val campusLocation = CampusLocation(lat, lng, radius ?: CampusGeofence.DEFAULT_RADIUS)
//        viewModelScope.launch {
//            store.save(campusLocation)
//            geofenceHelper.unregister()
//            geofenceHelper.registerFromStore()
//            _ui.update { it.copy(saveSuccess = true, error = null) }
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    fun useCurrentLocation() {
//        _ui.update { it.copy(isFetchingGps = true, error = null) }
//
//        val locationTask: Task<Location> = fusedClient.lastLocation
//
//        locationTask.addOnSuccessListener { loc: Location? ->
//            if (loc == null) {
//                _ui.update {
//                    it.copy(
//                        isFetchingGps = false,
//                        error = "GPS unavailable — step outside and try again."
//                    )
//                }
//            } else {
//                _ui.update { s ->
//                    s.copy(
//                        isFetchingGps = false,
//                        latInput      = "%.6f".format(loc.latitude),
//                        lngInput      = "%.6f".format(loc.longitude)
//                    )
//                }
//            }
//        }
//
//        locationTask.addOnFailureListener { ex: Exception ->
//            _ui.update {
//                it.copy(
//                    isFetchingGps = false,
//                    error         = ex.message ?: "Failed to get location"
//                )
//            }
//        }
//    }
//
//    fun clearLocation() {
//        viewModelScope.launch {
//            store.clear()
//            geofenceHelper.unregister()
//            _ui.update {
//                it.copy(
//                    saved       = null,
//                    latInput    = "",
//                    lngInput    = "",
//                    radiusInput = "200"
//                )
//            }
//        }
//    }
//
//    fun dismissSuccess() { _ui.update { it.copy(saveSuccess = false) } }
//}