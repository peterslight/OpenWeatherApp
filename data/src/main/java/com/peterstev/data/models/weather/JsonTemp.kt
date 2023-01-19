package com.peterstev.data.models.weather

import com.peterstev.data.util.round
import java.text.DecimalFormat

data class JsonTemp(
    val day: Double?,
    val eve: Double?,
    val max: Double?,
    val min: Double?,
    val morn: Double?,
    val night: Double?,
) {

    fun getAverageTemp(): Double {
        val items = listOfNotNull(day, eve, morn, night).filter { it != 0.0 }
        return round((items.sum() / items.size))
    }

}
