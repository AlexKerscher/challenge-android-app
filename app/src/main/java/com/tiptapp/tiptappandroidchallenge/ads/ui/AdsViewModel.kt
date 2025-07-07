package com.tiptapp.tiptappandroidchallenge.ads.ui

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiptapp.tiptappandroidchallenge.ads.data.AdsRepository
import com.tiptapp.tiptappandroidchallenge.viewmodel.LocationViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class AdsViewModel(
    adsRepository: AdsRepository,
    private val locationViewModel: LocationViewModel,
) : ViewModel() {

    val uiState: StateFlow<AdsUiState> = combine(
        adsRepository.getAdsAsFlow(),
        locationViewModel.currentLocation // Use the location flow here
    ) { adsResult, location ->
        adsResult.fold(
            onSuccess = { ads ->
                val displayAds = ads.map { ad ->
                    DisplayAd(
                        ad = ad,
                        distanceInKm = calculateDistance(location, ad.from.loc)
                    )
                }
                AdsUiState.Success(displayAds)
            },
            onFailure = { throwable ->
                AdsUiState.Error(throwable.message ?: "An unknown error occurred")
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdsUiState.Loading
    )

    private val _selectedAdIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedAdIds: StateFlow<Set<String>> = _selectedAdIds.asStateFlow()

    fun toggleAdSelection(adId: String) {
        _selectedAdIds.update { currentSelection ->
            val newSelection = currentSelection.toMutableSet()
            if (adId in newSelection) {
                newSelection.remove(adId)
            } else {
                newSelection.add(adId)
            }
            newSelection
        }
    }

    private fun calculateDistance(userLocation: Pair<Double, Double>?, adLocation: List<Double>): Float? {
        if (userLocation == null || adLocation.size < 2) return null
        val results = FloatArray(1)
        Location.distanceBetween(
            userLocation.first, userLocation.second,
            adLocation[1], adLocation[0],
            results
        )
        return results[0] / 1000 // convert meters to km
    }

    fun updateLocationTracking() {
        val uiStateValue = uiState.value
        val selectedIds = selectedAdIds.value
        val isTracking = locationViewModel.isTrackingLocation.value

        if (uiStateValue is AdsUiState.Success) {
            val selectedAds = uiStateValue.ads
                .map { it.ad } // Get the original Ad objects
                .filter { it.id in selectedIds }

            val tenMinutesInMillis = 10 * 60 * 1000
            val shouldBeTracking = selectedAds.any {
                (System.currentTimeMillis() - it.created) < tenMinutesInMillis
            }

            if (shouldBeTracking && !isTracking) {
                locationViewModel.startLocationTracking()
            } else if (!shouldBeTracking && isTracking) {
                locationViewModel.stopLocationTracking()
            }
        }
    }
}