package com.peterstev.domain.mock

import com.peterstev.domain.model.geolocation.GeoLocation
import com.peterstev.domain.model.weather.Weather
import com.peterstev.domain.model.weather.WeatherData
import com.peterstev.domain.model.weather.WeatherItem

class MockData {

    companion object {
        val geolocation = GeoLocation(
            country = "Nigeria",
            latitude = 6.5,
            longitude = 3.8,
            city = "ikeja",
            state = "Lagos"
        )

        val weather = Weather(
            description = "cloudy", "10n"
        )

        val item = WeatherItem(
            dateTime = "24-01-2023",
            feelsLike = 23.5,
            humidity = 60,
            sunrise = "06:40 AM",
            sunset = "06:40 AM",
            temp = 30.0,
            weather = listOf(weather)
        )

        val weatherData = WeatherData(
            current = item,
            daily = mutableListOf(item, item, item, item, item),
            hourly = mutableListOf(item, item, item, item, item),
            tomorrow = mutableListOf(item, item, item, item, item),
            latitude = geolocation.latitude,
            longitude = geolocation.longitude,
            timezoneOffset = 3600
        )
    }
}
