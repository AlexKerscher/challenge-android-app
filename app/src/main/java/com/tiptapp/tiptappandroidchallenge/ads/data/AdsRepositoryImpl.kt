package com.tiptapp.tiptappandroidchallenge.ads.data

import com.tiptapp.tiptappandroidchallenge.ads.data.remote.Ad
import com.tiptapp.tiptappandroidchallenge.ads.data.remote.TiptappApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * Default implementation of the AdsRepository.
 *
 * @param apiService The Retrofit service to fetch data from the network.
 */
class AdsRepositoryImpl(
    private val apiService: TiptappApiService
) : AdsRepository {

    override suspend fun getAds(): Result<List<Ad>> {
        // Ensure network call happens on the IO dispatcher
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAds()
                Result.success(response.items)
            } catch (e: Exception) {
                // Log the exception here if needed
                Result.failure(e)
            }
        }
    }

    override fun getAdsAsFlow(): Flow<Result<List<Ad>>> = flow {
        emit(getAds())
    }
}