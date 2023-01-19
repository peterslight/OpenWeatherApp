package com.peterstev.database.entity

import androidx.room.Entity
import java.io.Serializable

@Entity
data class WeatherXEntity constructor(
    var description: String,
    var icon: String,
) : Serializable
