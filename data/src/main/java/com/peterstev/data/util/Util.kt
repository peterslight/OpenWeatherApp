package com.peterstev.data.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun dateTimeConverter(dateTime: Long, weatherType: WeatherType): String {
    val pattern = when (weatherType) {
        WeatherType.TODAY -> "hh:mm a"
        WeatherType.FUTURE -> "EEE dd-M-yyyy-hh:mm a"
    }

    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(dateTime * 1000))
}

fun round(number: Double): Double {
    val df = DecimalFormat("#.##")
    return df.format(number).toDouble()
}
