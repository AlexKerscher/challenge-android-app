package com.tiptapp.tiptappandroidchallenge.ads.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiptapp.tiptappandroidchallenge.ads.data.AdsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdsViewModel(private val adsRepository: AdsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AdsUiState>(AdsUiState.Loading)
    val uiState: StateFlow<AdsUiState> = _uiState.asStateFlow()

    private val _selectedAdIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedAdIds: StateFlow<Set<String>> = _selectedAdIds.asStateFlow()

    init {
        fetchAds()
    }

    private fun fetchAds() {
        viewModelScope.launch {
            _uiState.value = AdsUiState.Loading
            adsRepository.getAds()
                .onSuccess { ads ->
                    _uiState.value = AdsUiState.Success(ads)
                }
                .onFailure { throwable ->
                    _uiState.value = AdsUiState.Error(throwable.message ?: "An unknown error occurred")
                }
        }
    }

    fun onAdSelectionChanged(adId: String, isSelected: Boolean) {
        _selectedAdIds.update { currentSelection ->
            val newSelection = currentSelection.toMutableSet()
            if (isSelected) {
                newSelection.add(adId)
            } else {
                newSelection.remove(adId)
            }
            newSelection
        }
    }
}