package com.tiptapp.tiptappandroidchallenge.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tiptapp.tiptappandroidchallenge.service.LocationService
import com.tiptapp.tiptappandroidchallenge.utils.LocationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _isTrackingLocation = MutableStateFlow(false)
    val isTrackingLocation: StateFlow<Boolean> = _isTrackingLocation.asStateFlow()
    
    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val currentLocation: StateFlow<Pair<Double, Double>?> = _currentLocation.asStateFlow()
    
    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == LocationService.ACTION_LOCATION_UPDATED) {
                val latitude = intent.getDoubleExtra(LocationService.EXTRA_LATITUDE, 0.0)
                val longitude = intent.getDoubleExtra(LocationService.EXTRA_LONGITUDE, 0.0)
                _currentLocation.value = latitude to longitude
            }
        }
    }
    
    init {
        registerLocationReceiver()
    }
    
    private fun registerLocationReceiver() {
        val filter = IntentFilter(LocationService.ACTION_LOCATION_UPDATED)
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
    
    fun toggleLocationTracking() {
        viewModelScope.launch {
            if (_isTrackingLocation.value) {
                stopLocationTracking()
            } else {
                startLocationTracking()
            }
        }
    }
    
    private fun startLocationTracking() {
        LocationUtils.startLocationService(getApplication())
        _isTrackingLocation.value = true
    }
    
    private fun stopLocationTracking() {
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
