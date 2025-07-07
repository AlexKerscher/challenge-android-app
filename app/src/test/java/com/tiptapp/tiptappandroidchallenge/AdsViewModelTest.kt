package com.tiptapp.tiptappandroidchallenge

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.tiptapp.tiptappandroidchallenge.ads.data.AdsRepository
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.Ad
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsUiState
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    // Rule to swap the main dispatcher with a test dispatcher
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState transitions to Success when repository returns success`() = runTest {
        // Arrange
        val ads = listOf(Ad("1", "Test Ad", 12345L))
        coEvery { repository.getAds() } returns Result.success(ads)

        // Act & Assert
        viewModel = AdsViewModel(repository) // ViewModel is initialized here
        viewModel.uiState.test {
            // The initial state is Loading
            assertThat(awaitItem()).isEqualTo(AdsUiState.Loading)
            // Then it transitions to Success
            assertThat(awaitItem()).isEqualTo(AdsUiState.Success(ads))
        }
    }

    @Test
    fun `uiState transitions to Error when repository returns failure`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        coEvery { repository.getAds() } returns Result.failure(IOException(errorMessage))

        // Act & Assert
        viewModel = AdsViewModel(repository)
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(AdsUiState.Loading)
            assertThat(awaitItem()).isEqualTo(AdsUiState.Error(errorMessage))
        }
    }

    @Test
    fun `onAdSelectionChanged correctly updates the selectedAdIds`() = runTest {
        // Arrange - Setup a successful state first
        coEvery { repository.getAds() } returns Result.success(emptyList())
        viewModel = AdsViewModel(repository)

        // Act & Assert
        viewModel.selectedAdIds.test {
            // Initial state is an empty set
            assertThat(awaitItem()).isEmpty()

            // Select an ad
            viewModel.onAdSelectionChanged("ad1", true)
            assertThat(awaitItem()).containsExactly("ad1")

            // Select another ad
            viewModel.onAdSelectionChanged("ad2", true)
            assertThat(awaitItem()).containsExactly("ad1", "ad2")

            // Deselect the first ad
            viewModel.onAdSelectionChanged("ad1", false)
            assertThat(awaitItem()).containsExactly("ad2")
        }
    }
}