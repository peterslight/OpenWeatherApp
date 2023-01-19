package com.peterstev.database.transform

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.peterstev.database.entity.WeatherItemEntity
import com.peterstev.database.entity.WeatherXEntity
import java.lang.reflect.Type

class WeatherItemConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromWeatherItemToString(item: List<WeatherItemEntity>): String {
        return gson.toJson(item)
    }

    @TypeConverter
    fun fromStringToWeatherItem(item: String?): List<WeatherItemEntity> {
        if (item == null) return emptyList()
        val listType: Type = object : TypeToken<List<WeatherItemEntity>?>() {}.type
        return gson.fromJson(item, listType)
    }

    @TypeConverter
    fun fromWeatherXEntityToString(item: List<WeatherXEntity>): String {
        return gson.toJson(item)
    }

    @TypeConverter
    fun fromStringToWeatherXEntity(item: String?): List<WeatherXEntity> {
        if (item == null) return emptyList()
        val listType: Type = object : TypeToken<List<WeatherXEntity>?>() {}.type
        return gson.fromJson(item, listType)
    }
}
