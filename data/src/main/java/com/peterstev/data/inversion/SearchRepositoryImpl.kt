package com.peterstev.data.inversion

import com.peterstev.data.mapper.CityMapper
import com.peterstev.data.mapper.TomorrowMapper
import com.peterstev.data.mapper.WeatherMapper
import com.peterstev.data.network.BaseApiService
import com.peterstev.domain.model.geolocation.GeoLocation
import com.peterstev.domain.model.weather.WeatherData
import com.peterstev.domain.model.weather.WeatherItem
import com.peterstev.domain.repository.SearchRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val service: BaseApiService,
    private val weatherMapper: WeatherMapper,
    private val cityMapper: CityMapper,
    private val tomorrowMapper: TomorrowMapper,
) : SearchRepository {

    override fun searchCity(city: String): Observable<MutableList<GeoLocation>> {
        return service.searchCity(
            city = city,
            appId = "System.getenv(yourApiKey)"
        ).map {
            cityMapper.transform(it)
        }
    }

    override fun getWeather(latitude: Double, longitude: Double): Observable<WeatherData> {
        return service.getWeather(
            latitude = latitude,
            longitude = longitude,
            appId = "System.getenv(yourApiKey)"
        ).map { weatherMapper.transform(it) }
    }

    override fun getTomorrowWeather(latitude: Double, longitude: Double): Observable<MutableList<WeatherItem>> {
        return service.getTomorrowWeather(
            latitude = latitude,
            longitude = longitude,
            appId = "System.getenv(yourApiKey)"
        ).map { tomorrowMapper.transform(it) }
    }
}
