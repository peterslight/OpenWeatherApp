package com.peterstev.adapter

import com.peterstev.domain.model.geolocation.GeoLocation

interface LocationItemListener {
    fun onItemSelected(item: GeoLocation)
}
