package com.peterstev.domain.model.weather

import java.io.Serializable

data class Weather(
    val description: String,
    val icon: String
) : Serializable
