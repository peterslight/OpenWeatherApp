package com.peterstev.data.models.weather

import com.peterstev.data.util.round

data class JsonFeelsLike(
    val day: Double?,
    val eve: Double?,
    val morn: Double?,
    val night: Double?
) {

    fun getTempFeeling() : Double {
        val items = listOfNotNull(day, eve, morn, night).filter { it != 0.0 }
        return round(items.sum() / items.size)
    }
}
