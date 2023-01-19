package com.peterstev.data.models.weather

data class JsonWeatherData(
    val current: JsonCurrent,
    val daily: List<JsonDaily>,
    val hourly: List<JsonHourly>,
    val lat: Double,
    val lon: Double,
    val timezone_offset: Int
)
