package com.tiptapp.tiptappandroidchallenge.ads.ui

import com.tiptapp.tiptappandroidchallenge.ads.data.remote.Ad

data class DisplayAd(
    val ad: Ad,
    val distanceInKm: Float?
)