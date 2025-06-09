package com.tiptapp.tiptappandroidchallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tiptapp.tiptappandroidchallenge.ui.theme.TiptappAndroidChallengeTheme
import com.tiptapp.tiptappandroidchallenge.utils.LocationUtils
import com.tiptapp.tiptappandroidchallenge.viewmodel.LocationViewModel

class MainActivity : ComponentActivity() {
    
    private val locationViewModel: LocationViewModel by viewModels {
        viewModelFactory {
            initializer {
                LocationViewModel(application)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TiptappAndroidChallengeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationPermissionHandler(
                        activity = this,
                        content = {
                            LocationTrackerScreen(
                                viewModel = locationViewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LocationPermissionHandler(
    activity: ComponentActivity,
    content: @Composable () -> Unit
) {
    var permissionsGranted by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    
    LocationUtils.RequestLocationPermissions(
        activity = activity,
        onPermissionsGranted = {
            permissionsGranted = true
        },
        onPermissionsDenied = {
            permissionsGranted = false
        }
    )
    
    if (permissionsGranted) {
        content()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Location permissions are required to use this feature.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun LocationTrackerScreen(
    viewModel: LocationViewModel,
    modifier: Modifier = Modifier
) {
    val isTracking by viewModel.isTrackingLocation.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Location Tracker",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isTracking) "Location tracking is active" else "Location tracking is inactive",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (currentLocation != null) {
                    Text(
                        text = "Current Location:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Latitude: ${currentLocation?.first}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Longitude: ${currentLocation?.second}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else if (isTracking) {
                    Text(
                        text = "Waiting for location updates...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { viewModel.toggleLocationTracking() }
        ) {
            Text(text = if (isTracking) "Stop Tracking" else "Start Tracking")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationTrackerPreview() {
    TiptappAndroidChallengeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Location Tracker Preview")
            Button(onClick = { }) {
                Text("Start Tracking")
            }
        }
    }
}