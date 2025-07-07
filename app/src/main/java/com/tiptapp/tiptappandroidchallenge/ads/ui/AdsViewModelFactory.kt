package com.tiptapp.tiptappandroidchallenge.ads.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tiptapp.tiptappandroidchallenge.ads.data.AdsRepository

class AdsViewModelFactory(
    private val adsRepository: AdsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdsViewModel::class.java)) {
            return AdsViewModel(adsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}