package com.tiptapp.tiptappandroidchallenge

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.Ad
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsScreen
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsUiState
import com.tiptapp.tiptappandroidchallenge.ads.ui.AdsViewModel
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
        // Arrange: Create stable StateFlows for the test
        val ads = listOf(Ad("1", "Ad 1 Title", 1L), Ad("2", "Ad 2 Title", 2L))
        val uiStateFlow = MutableStateFlow<AdsUiState>(AdsUiState.Success(ads))
        val selectedIdsFlow = MutableStateFlow<Set<String>>(emptySet())

        // Use `every` to return the stable StateFlow instance
        every { viewModel.uiState } returns uiStateFlow
        every { viewModel.selectedAdIds } returns selectedIdsFlow

        // Act
        composeTestRule.setContent {
            TiptappAndroidChallengeTheme {
                AdsScreen(viewModel = viewModel, onNavigateToTracker = {})
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Ad 1 Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ad 2 Title").assertIsDisplayed()
    }

    @Test
    fun whenAdIsClicked_viewModelIsNotified() {
        // Arrange
        val ad = Ad("1", "Clickable Ad", 1L)
        val uiStateFlow = MutableStateFlow<AdsUiState>(AdsUiState.Success(listOf(ad)))
        val selectedIdsFlow = MutableStateFlow<Set<String>>(emptySet())

        every { viewModel.uiState } returns uiStateFlow
        every { viewModel.selectedAdIds } returns selectedIdsFlow
        // We need to tell MockK what to do when this function is called
        every { viewModel.toggleAdSelection(any()) } returns Unit

        composeTestRule.setContent {
            TiptappAndroidChallengeTheme {
                AdsScreen(viewModel = viewModel, onNavigateToTracker = {})
            }
        }

        // Act
        composeTestRule.onNodeWithText("Clickable Ad").performClick()

        // Assert
        verify { viewModel.toggleAdSelection(ad.id) }
    }
}