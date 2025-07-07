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
    @Json(name = "_id") // Maps the JSON "_id" field to our "id" property
    val id: String,
    val title: String,
    val thumbnail: String,
    val created: Long // The API sends this as a Long (Unix timestamp)
)