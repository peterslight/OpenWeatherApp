package com.peterstev.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.peterstev.database.transform.WeatherItemConverter
import java.io.Serializable

@Entity(tableName = "weather_table")
data class WeatherEntity constructor(
    @PrimaryKey val id: Int = 1,
    @Embedded(prefix = "current") var current: WeatherItemEntity,
    @TypeConverters(WeatherItemConverter::class) var daily: List<WeatherItemEntity> = listOf(),
    @TypeConverters(WeatherItemConverter::class) var hourly: List<WeatherItemEntity> = listOf(),
    @TypeConverters(WeatherItemConverter::class) var tomorrow: List<WeatherItemEntity> = listOf(),
    var latitude: Double,
    var longitude: Double,
    var timezone_offset: Int,
) : Serializable
