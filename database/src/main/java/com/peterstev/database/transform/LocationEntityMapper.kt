package com.peterstev.database.transform

import com.peterstev.database.entity.LocationEntity
import com.peterstev.domain.model.geolocation.GeoLocation
import javax.inject.Inject

class LocationEntityMapper @Inject constructor() {

    fun transform(entity: LocationEntity): GeoLocation {
        return entity.run {
            GeoLocation(country, latitude, longitude, city, state)
        }
    }

    fun transform(location: GeoLocation): LocationEntity {
        return location.run {
            LocationEntity(id = 1, country, latitude, longitude, city, state)
        }
    }
}
