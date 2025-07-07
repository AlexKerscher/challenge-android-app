package com.tiptapp.tiptappandroidchallenge

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.Ad
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.AdLocation
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsScreen
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsUiState
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsViewModel
import com.tiptapp.tiptappandroidchallenge.ads.ui.DisplayAd
import com.tiptapp.tiptappandroidchallenge.ui.theme.TiptappAndroidChallengeTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class AdsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel: AdsViewModel = mockk()

    @Test
    fun whenStateIsSuccess_displaysAdList() {
        // Arrange
        val ad = Ad("1", "Ad 1 Title", 1L, "thumb", 100, "SEK", 1, AdLocation(listOf(0.0, 0.0)))
        val displayAds = listOf(DisplayAd(ad = ad, distanceInKm = 5.5f))
        val successState = AdsUiState.Success(displayAds)

        every { viewModel.uiState } returns MutableStateFlow(successState)
        every { viewModel.selectedAdIds } returns MutableStateFlow(emptySet())

        // Act
        composeTestRule.setContent {
            TiptappAndroidChallengeTheme {
                AdsScreen(viewModel = viewModel, onNavigateToTracker = {})
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Ad 1 Title").assertIsDisplayed()
    }

    @Test
    fun whenAdIsClicked_viewModelToggleIsCalled() {
        // Arrange
        val ad = Ad("1", "Clickable Ad", 1L, "thumb", 100, "SEK", 1, AdLocation(listOf(0.0, 0.0)))
        val displayAd = DisplayAd(ad = ad, distanceInKm = null)
        val successState = AdsUiState.Success(listOf(displayAd))

        every { viewModel.uiState } returns MutableStateFlow(successState)
        every { viewModel.selectedAdIds } returns MutableStateFlow(emptySet())
        every { viewModel.toggleAdSelection(any()) } returns Unit

        // Act
        composeTestRule.setContent {
            TiptappAndroidChallengeTheme {
                AdsScreen(viewModel = viewModel, onNavigateToTracker = {})
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Clickable Ad").performClick()
        verify { viewModel.toggleAdSelection(ad.id) }
    }
}