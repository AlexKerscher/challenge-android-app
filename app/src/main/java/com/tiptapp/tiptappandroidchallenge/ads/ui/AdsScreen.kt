package com.tiptapp.tiptappandroidchallenge.ads.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AdsScreen(
    viewModel: AdsViewModel,
    onNavigateToTracker: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedIds by viewModel.selectedAdIds.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToTracker) {
                Icon(Icons.Filled.LocationOn, contentDescription = "Go to Tracker")
            }
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is AdsUiState.Loading -> {
                LoadingIndicator()
            }
            is AdsUiState.Error -> {
                ErrorScreen(message = state.message)
            }
            is AdsUiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(innerPadding)
                ) {
                    items(state.ads, key = { it.ad.id }) { displayAd ->
                        AdItem(
                            displayAd = displayAd,
                            isSelected = displayAd.ad.id in selectedIds,
                            onSelectionChanged = { viewModel.toggleAdSelection(displayAd.ad.id) }
                        )
                    }
                }
            }
        }
    }
}