package com.tiptapp.tiptappandroidchallenge
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    private val locationViewModel: LocationViewModel by viewModels {
        viewModelFactory { initializer { LocationViewModel(application) } }
    }

    private val adsRepository by lazy { AdsRepositoryImpl(RetrofitInstance.api) }
    private val adsViewModel: AdsViewModel by viewModels {
        AdsViewModelFactory(adsRepository, locationViewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TiptappAndroidChallengeTheme {
                val navController = rememberNavController()

                val adUiState by adsViewModel.uiState.collectAsStateWithLifecycle()
                val selectedAdIds by adsViewModel.selectedAdIds.collectAsStateWithLifecycle()

                LaunchedEffect(adUiState, selectedAdIds) {
                    adsViewModel.updateLocationTracking(adUiState, selectedAdIds)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationPermissionHandler(
                        activity = this,
                        content = {
                            NavHost(
                                navController = navController,
                                startDestination = "ads_screen",
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



