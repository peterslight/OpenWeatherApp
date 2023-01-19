package com.peterstev.data.models.geolocation

data class JsonGeoLocation(
    val country: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val state: String? = "",
)
