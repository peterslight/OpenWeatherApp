package com.peterstev.data.mapper

import com.peterstev.data.models.tomorrow.JsonMainItem
import com.peterstev.data.models.tomorrow.JsonTomorrow
import com.peterstev.data.util.WeatherType
import com.peterstev.data.util.dateTimeConverter
import com.peterstev.domain.model.weather.Weather
import com.peterstev.domain.model.weather.WeatherItem
import java.time.*
import javax.inject.Inject

class TomorrowMapper @Inject constructor() {

    fun transform(data: JsonTomorrow): MutableList<WeatherItem> {
        return filterTomorrowDates(data.list).map { item ->
            item.run {
                WeatherItem(
                    dateTime = dateTimeConverter(dt.toLong(), WeatherType.FUTURE),
                    feelsLike = main.feels_like,
                    humidity = main.humidity,
                    sunrise = dateTimeConverter(data.city.sunrise.toLong(), WeatherType.FUTURE),
                    sunset = dateTimeConverter(data.city.sunset.toLong(), WeatherType.FUTURE),
                    temp = main.temp,
                    weather = weather.map { Weather(description = it.description, icon = it.icon) }
                )
            }
        }.toMutableList()
    }

    private fun filterTomorrowDates(items: List<JsonMainItem>): List<JsonMainItem> {
        val currentDate = LocalDate.now()
        val tomorrow = currentDate.plusDays(1)
        val startOfTomorrow = LocalDateTime.of(tomorrow, LocalTime.MIN)
        val endOfTomorrow = LocalDateTime.of(tomorrow, LocalTime.MAX)
        return items.filter {
            val datetime = LocalDateTime.ofInstant(Instant.ofEpochSecond(it.dt.toLong()), ZoneOffset.UTC)
            datetime.isEqual(startOfTomorrow) || (datetime.isAfter(startOfTomorrow) && datetime.isBefore(endOfTomorrow))
        }
    }

}
