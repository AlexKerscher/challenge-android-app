package com.tiptapp.tiptappandroidchallenge

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tiptapp.tiptappandroidchallenge.ads.data.AdsRepository
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.Ad
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.AdLocation
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsViewModel
import com.tiptapp.tiptappandroidchallenge.viewmodel.LocationViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AdsViewModelInstrumentationTest {

    private lateinit var viewModel: AdsViewModel
    private val repository: AdsRepository = mockk()
    private val locationViewModel: LocationViewModel = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun updateLocationTracking_startsService_forRecentSelectedAd() = runTest {
        // Arrange
        val recentAd = Ad("1", "Recent", System.currentTimeMillis(), "t", 1, "c", 1, AdLocation(listOf(10.0, 10.0)))
        every { repository.getAdsAsFlow() } returns flowOf(Result.success(listOf(recentAd)))
        every { locationViewModel.currentLocation } returns MutableStateFlow(Pair(1.0, 1.0))
        every { locationViewModel.isTrackingLocation } returns MutableStateFlow(false)
        every { locationViewModel.startLocationTracking() } returns Unit // Important for relaxed=false mocks

        // Act
        viewModel = AdsViewModel(repository, locationViewModel)
        viewModel.toggleAdSelection("1")
        testDispatcher.scheduler.advanceUntilIdle() // Let the combine operator run
        viewModel.updateLocationTracking()

        // Assert
        verify(exactly = 1) { locationViewModel.startLocationTracking() }
    }

    @Test
    fun updateLocationTracking_stopsService_whenRecentAdIsDeselected() = runTest {
        // Arrange
        val recentAd = Ad("1", "Recent", System.currentTimeMillis(), "t", 1, "c", 1, AdLocation(listOf(10.0, 10.0)))
        every { repository.getAdsAsFlow() } returns flowOf(Result.success(listOf(recentAd)))
        every { locationViewModel.currentLocation } returns MutableStateFlow(Pair(1.0, 1.0))
        every { locationViewModel.isTrackingLocation } returns MutableStateFlow(true)
        every { locationViewModel.stopLocationTracking() } returns Unit // Important
        viewModel = AdsViewModel(repository, locationViewModel)
        viewModel.toggleAdSelection("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.toggleAdSelection("1") // Deselect
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateLocationTracking()

        // Assert
        verify(exactly = 1) { locationViewModel.stopLocationTracking() }
    }
}