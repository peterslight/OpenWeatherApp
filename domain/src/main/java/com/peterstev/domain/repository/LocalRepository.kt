package com.peterstev.domain.repository

import com.peterstev.domain.model.geolocation.GeoLocation
import com.peterstev.domain.model.weather.WeatherData
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface LocalRepository {

    fun updateLocation(location: GeoLocation): Single<Long>

    fun getLastLocationUpdate(): Flowable<GeoLocation>

    fun saveLastLocationWeatherData(weatherData: WeatherData): Single<Long>

    fun getLastLocationWeatherData(): Flowable<WeatherData>
}
