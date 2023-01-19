package com.peterstev.domain.inversion

import com.peterstev.domain.model.geolocation.GeoLocation
import com.peterstev.domain.model.weather.WeatherData
import com.peterstev.domain.model.weather.WeatherItem
import com.peterstev.domain.repository.SearchRepository
import com.peterstev.domain.usecase.SearchUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class SearchUseCaseImpl @Inject constructor(
    private val repository: SearchRepository,
) : SearchUseCase {

    override fun searchCity(
        city: String,
    ): Observable<MutableList<GeoLocation>> = repository.searchCity(city)

    override fun getWeather(
        latitude: Double,
        longitude: Double,
    ): Observable<WeatherData> = repository.getWeather(latitude, longitude)

    override fun getTomorrowWeather(
        latitude: Double,
        longitude: Double,
    ): Observable<MutableList<WeatherItem>> =
        repository.getTomorrowWeather(latitude, longitude)
}
