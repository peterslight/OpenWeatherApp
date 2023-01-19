package com.peterstev.domain.model.weather

import java.io.Serializable

data class WeatherItem(
    val dateTime: String,
    val feelsLike: Double,
    val humidity: Int,
    val sunrise: String,
    val sunset: String,
    val temp: Double,
    val weather: List<Weather>,
) : Serializable
