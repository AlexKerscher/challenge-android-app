package com.tiptapp.tiptappandroidchallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tiptapp.tiptappandroidchallenge.ads.data.AdsRepositoryImpl
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.RetrofitInstance
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsScreen
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsViewModel
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsViewModelFactory
import com.tiptapp.tiptappandroidchallenge.location.ui.LocationPermissionHandler
import com.tiptapp.tiptappandroidchallenge.location.ui.LocationTrackerScreen
import com.tiptapp.tiptappandroidchallenge.ui.theme.TiptappAndroidChallengeTheme
import com.tiptapp.tiptappandroidchallenge.viewmodel.LocationViewModel

class MainActivity : ComponentActivity() {

    // Existing LocationViewModel
    private val locationViewModel: LocationViewModel by viewModels {
        viewModelFactory { initializer { LocationViewModel(application) } }
    }

    // Our new AdsViewModel and its dependencies
    private val adsRepository by lazy { AdsRepositoryImpl(RetrofitInstance.api) }
    private val adsViewModel: AdsViewModel by viewModels { AdsViewModelFactory(adsRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TiptappAndroidChallengeTheme {
                // This is the controller for our navigation graph
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationPermissionHandler(
                        activity = this,
                        content = {
                            // NavHost is where our screens will be swapped
                            NavHost(
                                navController = navController,
                                startDestination = "ads_screen", // Our new screen is the starting point
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("ads_screen") {
                                    AdsScreen(
                                        viewModel = adsViewModel,
                                        onNavigateToTracker = {
                                            navController.navigate("tracker_screen")
                                        }
                                    )
                                }
                                composable("tracker_screen") {
                                    LocationTrackerScreen(viewModel = locationViewModel)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}



