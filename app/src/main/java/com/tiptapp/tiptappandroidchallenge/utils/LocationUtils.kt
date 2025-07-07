package com.tiptapp.tiptappandroidchallenge.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.tiptapp.tiptappandroidchallenge.location.service.LocationTrackerService

object LocationUtils {

    private val NOTIFICATION_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyArray()
    }

    private fun hasLocationPermissions(context: Context): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val backgroundLocation =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        return fineLocation && coarseLocation && backgroundLocation
    }
    
    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    fun startLocationService(context: Context) {
        val intent = Intent(context, LocationTrackerService::class.java).apply {
            action = LocationTrackerService.ACTION_START_SERVICE
        }
        context.startForegroundService(intent)
    }
    
    fun stopLocationService(context: Context) {
        val intent = Intent(context, LocationTrackerService::class.java).apply {
            action = LocationTrackerService.ACTION_STOP_SERVICE
        }
        context.stopService(intent)
    }
    
    @Composable
    fun RequestLocationPermissions(
        activity: Activity,
        onPermissionsGranted: () -> Unit = {},
        onPermissionsDenied: () -> Unit = {}
    ) {
        var permissionsGranted by remember { mutableStateOf(false) }
        
        val backgroundPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                permissionsGranted = true
                onPermissionsGranted()
            } else {
                permissionsGranted = false
                onPermissionsDenied()
            }
        }
        
        val locationPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            if (allGranted) {
                if (ContextCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                    backgroundPermissionLauncher.launch(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                } else {
                    permissionsGranted = true
                    onPermissionsGranted()
                }
            } else {
                permissionsGranted = false
                onPermissionsDenied()
            }
        }

        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.entries.all { it.value }) {
                // After notification permission, request location
                locationPermissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            } else {
                permissionsGranted = false
                onPermissionsDenied()
            }
        }
        
        // Launch permission request on first composition
        LaunchedEffect(Unit) {
            if (!hasNotificationPermission(activity) && NOTIFICATION_PERMISSION.isNotEmpty()) {
                notificationPermissionLauncher.launch(NOTIFICATION_PERMISSION)
            } else if (!hasLocationPermissions(activity)) {
                locationPermissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            } else {
                permissionsGranted = true
                onPermissionsGranted()
            }
        }
        
        DisposableEffect(Unit) {
            onDispose { }
        }
    }
}
