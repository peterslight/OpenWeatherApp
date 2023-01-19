package com.peterstev.database.entity

import androidx.room.Entity
import androidx.room.TypeConverters
import com.peterstev.database.transform.WeatherItemConverter
import java.io.Serializable

@Entity
data class WeatherItemEntity constructor(
    var date_time: String,
    var feels_like: Double,
    var humidity: Int,
    var sunrise: String,
    var sunset: String,
    var temp: Double,
    @TypeConverters(WeatherItemConverter::class) var weather: List<WeatherXEntity>,
) : Serializable
