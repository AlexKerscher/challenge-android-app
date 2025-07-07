package com.tiptapp.tiptappandroidchallenge.ads.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tiptapp.tiptappandroidchallenge.ads.data.AdsRepository
import com.tiptapp.tiptappandroidchallenge.viewmodel.LocationViewModel
import kotlinx.coroutines.flow.StateFlow

class AdsViewModelFactory(
    private val adsRepository: AdsRepository,
    private val locationViewModel: LocationViewModel,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdsViewModel::class.java)) {
            return AdsViewModel(adsRepository, locationViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}