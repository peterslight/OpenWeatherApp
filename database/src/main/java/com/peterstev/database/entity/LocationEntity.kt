package com.peterstev.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "location_table")
data class LocationEntity constructor(
    @PrimaryKey val id: Int,
    var country: String,
    var latitude: Double,
    var longitude: Double,
    var city: String,
    var state: String,
) : Serializable
