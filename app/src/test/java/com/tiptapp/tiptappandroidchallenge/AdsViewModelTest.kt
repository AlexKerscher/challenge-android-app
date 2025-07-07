package com.tiptapp.tiptappandroidchallenge

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.tiptapp.tiptappandroidchallenge.ads.data.AdsRepository
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.Ad
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsUiState
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsViewModel
import com.tiptapp.tiptappandroidchallenge.viewmodel.LocationViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AdsViewModelTest {

    private lateinit var viewModel: AdsViewModel
    private val repository: AdsRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val locationViewModel: LocationViewModel = mockk(relaxed = true)

    // Swap the main dispatcher with a test dispatcher
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getAds() } returns Result.success(emptyList())
        viewModel = AdsViewModel(repository, locationViewModel)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState transitions from Loading to Success when repository returns success`() = runTest {
        // Arrange
        val ads = listOf(Ad("1", "Test Ad", 12345L))
        coEvery { repository.getAds() } returns Result.success(ads)
        // Act
        viewModel = AdsViewModel(repository, locationViewModel)
        // Assert
        viewModel.uiState.test {
            // 1. First, expect the initial Loading state.
            assertThat(awaitItem()).isInstanceOf(AdsUiState.Loading::class.java)
            // 2. Then, expect the final Success state.
            assertThat(awaitItem()).isEqualTo(AdsUiState.Success(ads))
            // 3. Ensure no other states are emitted.
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState transitions from Loading to Error when repository returns failure`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        coEvery { repository.getAds() } returns Result.failure(IOException(errorMessage))
        // Act
        viewModel = AdsViewModel(repository, locationViewModel)
        // Assert
        viewModel.uiState.test {
            // 1. First, expect the initial Loading state.
            assertThat(awaitItem()).isInstanceOf(AdsUiState.Loading::class.java)
            // 2. Then, expect the final Error state.
            assertThat(awaitItem()).isEqualTo(AdsUiState.Error(errorMessage))
            // 3. Ensure no other states are emitted.
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAdSelectionChanged correctly updates the selectedAdIds`() = runTest {
        // The default setup in @Before is enough for this test.
        viewModel.selectedAdIds.test {
            assertThat(awaitItem()).isEmpty()

            viewModel.onAdSelectionChanged("ad1", true)
            assertThat(awaitItem()).containsExactly("ad1")
            viewModel.onAdSelectionChanged("ad2", true)
            assertThat(awaitItem()).containsExactly("ad1", "ad2")
            viewModel.onAdSelectionChanged("ad1", false)
            assertThat(awaitItem()).containsExactly("ad2")
        }
    }

    @Test
    fun `updateLocationTracking starts service for recent selected ad`() {
        // Arrange
        val recentAd = Ad("1", "Recent", System.currentTimeMillis())
        val successState = AdsUiState.Success(listOf(recentAd))
        val selectedIds = setOf("1")
        // Pretend tracking is currently off
        every { locationViewModel.isTrackingLocation } returns MutableStateFlow(false)
        // Act
        viewModel.updateLocationTracking(successState, selectedIds)
        // Assert
        verify { locationViewModel.startLocationTracking() }
    }

    @Test
    fun `updateLocationTracking stops service when recent ad is deselected`() = runTest {
        // Arrange
        val recentAd = Ad("1", "Recent", System.currentTimeMillis())
        val successState = AdsUiState.Success(listOf(recentAd))
        val selectedIds = emptySet<String>()
        every { locationViewModel.isTrackingLocation } returns MutableStateFlow(true)
        // Act
        viewModel.updateLocationTracking(successState, selectedIds)
        // Assert
        verify { locationViewModel.stopLocationTracking() }
    }
}