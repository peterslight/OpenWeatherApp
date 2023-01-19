package com.peterstev.domain.model.geolocation

import java.io.Serializable

data class GeoLocation(
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val state: String
) : Serializable
