package com.tiptapp.tiptappandroidchallenge.ads.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tiptapp.tiptappandroidchallenge.ads.data.AdsRepository
import com.tiptapp.tiptappandroidchallenge.location.viewmodel.LocationTrackerViewModel

class AdsViewModelFactory(
    private val adsRepository: AdsRepository,
    private val locationTrackerViewModel: LocationTrackerViewModel,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdsViewModel::class.java)) {
            return AdsViewModel(adsRepository, locationTrackerViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}