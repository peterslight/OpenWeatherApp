package com.peterstev.data.mapper

import com.peterstev.data.models.weather.JsonCurrent
import com.peterstev.data.models.weather.JsonDaily
import com.peterstev.data.models.weather.JsonHourly
import com.peterstev.data.models.weather.JsonWeatherData
import com.peterstev.data.util.WeatherType
import com.peterstev.data.util.dateTimeConverter
import com.peterstev.domain.model.weather.Weather
import com.peterstev.domain.model.weather.WeatherData
import com.peterstev.domain.model.weather.WeatherItem
import java.util.*
import javax.inject.Inject

class WeatherMapper @Inject constructor() {

    fun transform(data: JsonWeatherData): WeatherData {
        return data.run {
            WeatherData(
                current = unpackCurrent(current),
                hourly = unpackHourlyWeather(hourly, current).toMutableList(),
                daily = unpackDailyWeather(daily).toMutableList(),
                latitude = lat,
                longitude = lon,
                timezoneOffset = timezone_offset
            )
        }
    }

    private fun filterTodaysDates(datetimes: List<JsonHourly>): List<JsonHourly> {
        val currentCalendar = Calendar.getInstance()
        val tomorrowCalendar = currentCalendar.clone() as Calendar
        tomorrowCalendar.add(Calendar.DAY_OF_MONTH, 0)
        tomorrowCalendar.set(Calendar.HOUR_OF_DAY, 0)
        tomorrowCalendar.set(Calendar.MINUTE, 0)
        tomorrowCalendar.set(Calendar.SECOND, 0)
        tomorrowCalendar.set(Calendar.MILLISECOND, 0)
        val endOfTomorrowCalendar = tomorrowCalendar.clone() as Calendar
        endOfTomorrowCalendar.set(Calendar.HOUR_OF_DAY, 23)
        endOfTomorrowCalendar.set(Calendar.MINUTE, 59)
        endOfTomorrowCalendar.set(Calendar.SECOND, 59)
        endOfTomorrowCalendar.set(Calendar.MILLISECOND, 999)
        return datetimes.filter {
            val datetime = Calendar.getInstance().apply { timeInMillis = (it.dt.toLong() * 1000) }
            datetime.after(tomorrowCalendar) && datetime.before(endOfTomorrowCalendar)
        }
    }

    private fun unpackDailyWeather(items: List<JsonDaily>): List<WeatherItem> {
        return items.map {
            it.run {
                WeatherItem(
                    dateTime = dateTimeConverter(dt.toLong(), WeatherType.FUTURE),
                    feelsLike = feels_like.getTempFeeling(),
                    humidity = humidity,
                    sunrise = dateTimeConverter(sunrise.toLong(), WeatherType.FUTURE),
                    sunset = dateTimeConverter(sunset.toLong(), WeatherType.FUTURE),
                    temp = temp.getAverageTemp(),
                    weather = weather.map { Weather(description = it.description, icon = it.icon) }
                )
            }
        }
    }

    private fun unpackHourlyWeather(items: List<JsonHourly>, current: JsonCurrent): List<WeatherItem> {
        return filterTodaysDates(items).map {
            it.run {
                WeatherItem(
                    dateTime = dateTimeConverter(dt.toLong(), WeatherType.TODAY),
                    feelsLike = feels_like,
                    humidity = humidity,
                    sunrise = dateTimeConverter(current.sunrise.toLong(), WeatherType.TODAY),
                    sunset = dateTimeConverter(current.sunset.toLong(), WeatherType.TODAY),
                    temp = temp,
                    weather = weather.map { Weather(description = it.description, icon = it.icon) }
                )
            }
        }
    }

    private fun unpackCurrent(current: JsonCurrent): WeatherItem {
        return current.run {
            WeatherItem(
                dateTime = dateTimeConverter(dt.toLong(), WeatherType.TODAY),
                feelsLike = feels_like,
                humidity = humidity,
                sunrise = dateTimeConverter(sunrise.toLong(), WeatherType.TODAY),
                sunset = dateTimeConverter(sunset.toLong(), WeatherType.TODAY),
                temp = temp,
                weather = weather.map { Weather(description = it.description, icon = it.icon) }
            )
        }
    }
}
