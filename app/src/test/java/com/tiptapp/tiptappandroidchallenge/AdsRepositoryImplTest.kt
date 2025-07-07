package com.tiptapp.tiptappandroidchallenge

import com.google.common.truth.Truth.assertThat
import com.tiptapp.tiptappandroidchallenge.ads.data.AdsRepositoryImpl
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.Ad
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.AdResponse
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.TiptappApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

class AdsRepositoryImplTest {

    private lateinit var repository: AdsRepositoryImpl
    private val apiService: TiptappApiService = mockk()

    // This method runs before each test
    @Before
    fun setUp() {
        repository = AdsRepositoryImpl(apiService)
    }

    @Test
    fun `getAds returns success when api call is successful`() = runTest {
        // Arrange: Define what the mock service should return
        val expectedAds = listOf(Ad("1", "Test Ad", 12345L))
        val adResponse = AdResponse(items = expectedAds)
        coEvery { apiService.getAds() } returns adResponse

        // Act: Call the method we are testing
        val result = repository.getAds()

        // Assert: Check if the outcome is what we expected
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedAds)
    }

    @Test
    fun `getAds returns failure when api call throws exception`() = runTest {
        // Arrange: Define that the mock service should throw an error
        val expectedException = IOException("Network error")
        coEvery { apiService.getAds() } throws expectedException

        // Act: Call the method
        val result = repository.getAds()

        // Assert: Check that the result is a failure and contains the correct exception
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(expectedException)
    }
}