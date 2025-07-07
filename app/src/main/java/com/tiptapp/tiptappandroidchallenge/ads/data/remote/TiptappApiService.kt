package com.tiptapp.tiptappandroidchallenge.ads.data.remote

import retrofit2.http.GET

interface TiptappApiService {

    @GET("v1/ads")
    suspend fun getAds(): AdResponse
}