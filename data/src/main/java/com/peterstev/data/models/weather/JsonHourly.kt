package com.peterstev.data.models.weather

data class JsonHourly(
    val dt: Int,
    val feels_like: Double,
    val humidity: Int,
    val temp: Double,
    val weather: List<JsonWeather>
)
