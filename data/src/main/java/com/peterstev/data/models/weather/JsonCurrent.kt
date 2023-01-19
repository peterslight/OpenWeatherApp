package com.peterstev.data.models.weather

data class JsonCurrent(
    val dt: Int,
    val feels_like: Double,
    val humidity: Int,
    val sunrise: Int,
    val sunset: Int,
    val temp: Double,
    val weather: List<JsonWeather>
)
