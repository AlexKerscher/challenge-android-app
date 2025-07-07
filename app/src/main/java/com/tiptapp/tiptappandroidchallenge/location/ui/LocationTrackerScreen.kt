package com.tiptapp.tiptappandroidchallenge.location.ui

import androidx.activity.ComponentActivity
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
import com.tiptapp.tiptappandroidchallenge.ui.theme.TiptappAndroidChallengeTheme
import com.tiptapp.tiptappandroidchallenge.utils.LocationUtils
import com.tiptapp.tiptappandroidchallenge.location.viewmodel.LocationTrackerViewModel

@Composable
fun LocationTrackerScreen(
    viewModel: LocationTrackerViewModel,
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