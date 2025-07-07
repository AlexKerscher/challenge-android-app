package com.tiptapp.tiptappandroidchallenge.ads.data

import com.tiptapp.tiptappandroidchallenge.ads.data.remote.Ad

/**
 * Interface for the Ads data layer.
 */
interface AdsRepository {
    /**
     * Fetches the list of ads from the data source.
     */
    suspend fun getAds(): Result<List<Ad>>
}