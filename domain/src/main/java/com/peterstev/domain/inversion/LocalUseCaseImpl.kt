package com.peterstev.domain.inversion

import com.peterstev.domain.model.geolocation.GeoLocation
import com.peterstev.domain.model.weather.WeatherData
import com.peterstev.domain.repository.LocalRepository
import com.peterstev.domain.usecase.LocalUseCase
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LocalUseCaseImpl @Inject constructor(
    private val repository: LocalRepository,
) : LocalUseCase {

    override fun updateLocation(location: GeoLocation): Single<Long> =
        repository.updateLocation(location)

    override fun getLastLocationUpdate(): Flowable<GeoLocation> =
        repository.getLastLocationUpdate()

    override fun saveLastLocationWeatherData(weatherData: WeatherData): Single<Long> =
        repository.saveLastLocationWeatherData(weatherData)

    override fun getLastLocationWeatherData(): Flowable<WeatherData> =
        repository.getLastLocationWeatherData()
}
