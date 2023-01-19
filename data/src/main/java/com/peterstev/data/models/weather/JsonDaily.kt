package com.peterstev.data.models.weather

data class JsonDaily(
    val dt: Int,
    val feels_like: JsonFeelsLike,
    val humidity: Int,
    val sunrise: Int,
    val sunset: Int,
    val temp: JsonTemp,
    val weather: List<JsonWeather>,
)
