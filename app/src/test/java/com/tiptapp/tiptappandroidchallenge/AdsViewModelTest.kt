package com.tiptapp.tiptappandroidchallenge

import android.location.Location
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.tiptapp.tiptappandroidchallenge.ads.data.AdsRepository
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.Ad
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.AdLocation
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsUiState
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsViewModel
import com.tiptapp.tiptappandroidchallenge.location.viewmodel.LocationTrackerViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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
    private val locationTrackerViewModel: LocationTrackerViewModel = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Location::class)
        every { Location.distanceBetween(any(), any(), any(), any(), any()) } answers {
            (it.invocation.args[4] as FloatArray)[0] = 12345f // Return a fake distance
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Location::class) // Clean up the static mock
    }

    @Test
    fun `uiState emits Loading then Success`() = runTest {
        // Arrange
        val ads = listOf(Ad("1", "Test Ad", 1L, "thumb", 100, "SEK", 1, AdLocation(listOf(0.0, 0.0))))
        every { repository.getAdsAsFlow() } returns flowOf(Result.success(ads))
        every { locationTrackerViewModel.currentLocation } returns MutableStateFlow(null)
        // Act
        viewModel = AdsViewModel(repository, locationTrackerViewModel)
        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(AdsUiState.Loading::class.java)
            assertThat(awaitItem()).isInstanceOf(AdsUiState.Success::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `uiState emits Loading then Error`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        every { repository.getAdsAsFlow() } returns flowOf(Result.failure(IOException(errorMessage)))
        every { locationTrackerViewModel.currentLocation } returns MutableStateFlow(null)
        // Act
        viewModel = AdsViewModel(repository, locationTrackerViewModel)
        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(AdsUiState.Loading::class.java)
            assertThat(awaitItem()).isInstanceOf(AdsUiState.Error::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleAdSelection correctly updates selectedAdIds`() = runTest {
        // Arrange
        every { repository.getAdsAsFlow() } returns flowOf(Result.success(emptyList()))
        every { locationTrackerViewModel.currentLocation } returns MutableStateFlow(null)
        viewModel = AdsViewModel(repository, locationTrackerViewModel)

        // Act & Assert
        viewModel.selectedAdIds.test {
            assertThat(awaitItem()).isEmpty()
            viewModel.toggleAdSelection("ad1")
            assertThat(awaitItem()).containsExactly("ad1")
            viewModel.toggleAdSelection("ad2")
            assertThat(awaitItem()).containsExactly("ad1", "ad2")
            viewModel.toggleAdSelection("ad1")
            assertThat(awaitItem()).containsExactly("ad2")
        }
    }
}