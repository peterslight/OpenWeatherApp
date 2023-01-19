package com.peterstev.domain.repository

import com.peterstev.domain.model.geolocation.GeoLocation
import com.peterstev.domain.model.weather.WeatherData
import com.peterstev.domain.model.weather.WeatherItem
import io.reactivex.rxjava3.core.Observable

interface SearchRepository {

    fun searchCity(city: String): Observable<MutableList<GeoLocation>>

    fun getWeather(latitude: Double, longitude: Double): Observable<WeatherData>

    fun getTomorrowWeather(latitude: Double, longitude: Double): Observable<MutableList<WeatherItem>>
}
