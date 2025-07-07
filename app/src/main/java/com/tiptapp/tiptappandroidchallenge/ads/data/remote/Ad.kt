package com.tiptapp.tiptappandroidchallenge.ads.data.remote

import com.squareup.moshi.Json

/**
 * This class matches the root of the JSON response from the API,
 * specifically targeting the list of ad items.
 */
data class AdResponse(
    val items: List<Ad>
)

/**
 * Only contains the fields required by the coding challenge.
 */
data class Ad(
    @Json(name = "_id")
    val id: String,
    val title: String,
    val created: Long,
    val thumbnail: String,
    val pay: Long,
    val ccy: String,
    val type: Long,
    val from: AdLocation
)

data class AdLocation(
    val loc: List<Double>
)