package com.tiptapp.tiptappandroidchallenge

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tiptapp.tiptappandroidchallenge.ads.data.AdsRepository
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.Ad
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.AdLocation
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsViewModel
import com.tiptapp.tiptappandroidchallenge.location.viewmodel.LocationTrackerViewModel
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
    private val locationTrackerViewModel: LocationTrackerViewModel = mockk(relaxed = true)

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
        every { locationTrackerViewModel.currentLocation } returns MutableStateFlow(Pair(1.0, 1.0))
        every { locationTrackerViewModel.isTrackingLocation } returns MutableStateFlow(false)
        every { locationTrackerViewModel.startLocationTracking() } returns Unit // Important for relaxed=false mocks

        // Act
        viewModel = AdsViewModel(repository, locationTrackerViewModel)
        viewModel.toggleAdSelection("1")
        testDispatcher.scheduler.advanceUntilIdle() // Let the combine operator run
        viewModel.updateLocationTracking()

        // Assert
        verify(exactly = 1) { locationTrackerViewModel.startLocationTracking() }
    }

    @Test
    fun updateLocationTracking_stopsService_whenRecentAdIsDeselected() = runTest {
        // Arrange
        val recentAd = Ad("1", "Recent", System.currentTimeMillis(), "t", 1, "c", 1, AdLocation(listOf(10.0, 10.0)))
        every { repository.getAdsAsFlow() } returns flowOf(Result.success(listOf(recentAd)))
        every { locationTrackerViewModel.currentLocation } returns MutableStateFlow(Pair(1.0, 1.0))
        every { locationTrackerViewModel.isTrackingLocation } returns MutableStateFlow(true)
        every { locationTrackerViewModel.stopLocationTracking() } returns Unit // Important
        viewModel = AdsViewModel(repository, locationTrackerViewModel)
        viewModel.toggleAdSelection("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.toggleAdSelection("1") // Deselect
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateLocationTracking()

        // Assert
        verify(exactly = 1) { locationTrackerViewModel.stopLocationTracking() }
    }
}