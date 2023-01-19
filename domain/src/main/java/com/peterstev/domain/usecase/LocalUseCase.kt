package com.peterstev.domain.usecase

import com.peterstev.domain.model.geolocation.GeoLocation
import com.peterstev.domain.model.weather.WeatherData
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface LocalUseCase {

    fun updateLocation(location: GeoLocation): Single<Long>

    fun getLastLocationUpdate(): Flowable<GeoLocation>

    fun saveLastLocationWeatherData(weatherData: WeatherData): Single<Long>

    fun getLastLocationWeatherData(): Flowable<WeatherData>
}
