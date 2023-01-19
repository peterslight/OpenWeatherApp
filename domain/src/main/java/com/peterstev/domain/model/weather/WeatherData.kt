package com.peterstev.domain.model.weather

import java.io.Serializable

data class WeatherData(
    val current: WeatherItem,
    val daily: MutableList<WeatherItem>,
    val hourly: MutableList<WeatherItem>,
    var tomorrow: List<WeatherItem> = listOf(),
    val latitude: Double,
    val longitude: Double,
    val timezoneOffset: Int,
) : Serializable
