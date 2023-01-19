package com.peterstev.data.models.tomorrow

data class JsonMainItem(
    val dt: Int,
    val main: JsonMain,
    val weather: List<JsonWeather>
)
