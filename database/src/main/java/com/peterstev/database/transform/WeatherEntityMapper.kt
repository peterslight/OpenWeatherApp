package com.peterstev.database.transform

import com.peterstev.database.entity.WeatherEntity
import com.peterstev.database.entity.WeatherItemEntity
import com.peterstev.database.entity.WeatherXEntity
import com.peterstev.domain.model.weather.Weather
import com.peterstev.domain.model.weather.WeatherData
import com.peterstev.domain.model.weather.WeatherItem
import javax.inject.Inject

class WeatherEntityMapper @Inject constructor() {

    fun transform(entity: WeatherEntity): WeatherData {
        return transformEntityToWeather(entity)
    }

    fun transform(data: WeatherData): WeatherEntity {
        return transformWeatherToEntity(data)
    }

    private fun transformEntityToWeather(entity: WeatherEntity): WeatherData {
        return entity.run {
            WeatherData(
                current = transformEntityToWeatherItem(current),
                daily = daily.map { transformEntityToWeatherItem(it) }.toMutableList(),
                hourly = hourly.map { transformEntityToWeatherItem(it) }.toMutableList(),
                tomorrow = tomorrow.map { transformEntityToWeatherItem(it) }.toMutableList(),
                latitude = latitude,
                longitude = longitude,
                timezoneOffset = timezone_offset
            )
        }
    }

    private fun transformWeatherToEntity(data: WeatherData): WeatherEntity {
        return data.run {
            WeatherEntity(
                current = transformWeatherItemToEntity(current),
                daily = daily.map { transformWeatherItemToEntity(it) },
                hourly = hourly.map { transformWeatherItemToEntity(it) },
                tomorrow = tomorrow.map { transformWeatherItemToEntity(it) },
                latitude = latitude,
                longitude = longitude,
                timezone_offset = timezoneOffset
            )
        }
    }

    private fun transformEntityToWeatherItem(entity: WeatherItemEntity): WeatherItem {
        return entity.run {
            WeatherItem(date_time, feels_like, humidity, sunrise, sunset, temp, weather.map { it.run { Weather(description, icon) } })
        }
    }

    private fun transformWeatherItemToEntity(item: WeatherItem): WeatherItemEntity {
        return item.run {
            WeatherItemEntity(dateTime,
                feelsLike,
                humidity,
                sunrise,
                sunset,
                temp,
                weather.map { it.run { WeatherXEntity(description, icon) } })
        }
    }
}
