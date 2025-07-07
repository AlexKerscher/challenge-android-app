package com.tiptapp.tiptappandroidchallenge.location.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.tiptapp.tiptappandroidchallenge.location.service.LocationTrackerService
import com.tiptapp.tiptappandroidchallenge.utils.LocationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationTrackerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _isTrackingLocation = MutableStateFlow(false)
    val isTrackingLocation: StateFlow<Boolean> = _isTrackingLocation.asStateFlow()
    
    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val currentLocation: StateFlow<Pair<Double, Double>?> = _currentLocation.asStateFlow()

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == LocationTrackerService.ACTION_LOCATION_UPDATED) {
                val latitude = intent.getDoubleExtra(LocationTrackerService.EXTRA_LATITUDE, 0.0)
                val longitude = intent.getDoubleExtra(LocationTrackerService.EXTRA_LONGITUDE, 0.0)
                _currentLocation.value = latitude to longitude
            }
        }
    }

    init {
        registerLocationReceiver()
    }
    
    private fun registerLocationReceiver() {
        val filter = IntentFilter(LocationTrackerService.ACTION_LOCATION_UPDATED)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            getApplication<Application>().registerReceiver(
                locationReceiver, 
                filter, 
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            ContextCompat.registerReceiver(
                getApplication(),
                locationReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
    }

    fun requestInitialLocation() {
        // This requires location permission, which our UI already handles.
        // The try/catch is for the SecurityException if permissions are somehow lost.
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        _currentLocation.value = location.latitude to location.longitude
                    }
                }
        } catch (e: SecurityException) {
            // Permissions error
        }
    }
    
    fun toggleLocationTracking() {
        viewModelScope.launch {
            if (_isTrackingLocation.value) {
                stopLocationTracking()
            } else {
                startLocationTracking()
            }
        }
    }
    
    fun startLocationTracking() {
        LocationUtils.startLocationService(getApplication())
        _isTrackingLocation.value = true
    }
    
    fun stopLocationTracking() {
        LocationUtils.stopLocationService(getApplication())
        _isTrackingLocation.value = false
    }
    
    override fun onCleared() {
        super.onCleared()
        try {
            getApplication<Application>().unregisterReceiver(locationReceiver)
        } catch (e: Exception) {
            // Receiver might not be registered
        }
        
        if (_isTrackingLocation.value) {
            stopLocationTracking()
        }
    }
}
