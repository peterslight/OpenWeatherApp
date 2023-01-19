package com.peterstev.data.mapper

import com.peterstev.data.models.geolocation.JsonGeoLocation
import com.peterstev.domain.model.geolocation.GeoLocation
import java.util.*
import javax.inject.Inject

class CityMapper @Inject constructor() {

    fun transform(data: List<JsonGeoLocation>): MutableList<GeoLocation> {
        return data.map {
            it.run {
                GeoLocation(
                    getCountryName(country),
                    latitude = lat,
                    longitude = lon,
                    city = name,
                    state ?: ""
                )
            }
        }.toMutableList()
    }

    private fun getCountryName(code: String): String {
        return Locale("en", code).displayCountry
    }
}
