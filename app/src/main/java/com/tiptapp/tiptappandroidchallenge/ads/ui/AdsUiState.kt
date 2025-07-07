package com.tiptapp.tiptappandroidchallenge.ads.ui

import com.tiptapp.tiptappandroidchallenge.ads.data.remote.Ad

sealed interface AdsUiState {
    object Loading : AdsUiState
    data class Success(val ads: List<Ad>) : AdsUiState
    data class Error(val message: String) : AdsUiState
}