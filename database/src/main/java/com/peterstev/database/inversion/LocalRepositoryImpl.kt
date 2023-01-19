package com.peterstev.database.inversion

import com.peterstev.database.dao.WeatherDao
import com.peterstev.database.transform.LocationEntityMapper
import com.peterstev.database.transform.WeatherEntityMapper
import com.peterstev.domain.model.geolocation.GeoLocation
import com.peterstev.domain.model.weather.WeatherData
import com.peterstev.domain.repository.LocalRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val weatherDao: WeatherDao,
    private val weatherMapper: WeatherEntityMapper,
    private val locationMapper: LocationEntityMapper,
) : LocalRepository {

    override fun updateLocation(location: GeoLocation): Single<Long> =
        weatherDao.updateLocation(locationMapper.transform(location))

    override fun getLastLocationUpdate(): Flowable<GeoLocation> =
        weatherDao.getLastLocationUpdate()
            .map { locationMapper.transform(it) }

    override fun saveLastLocationWeatherData(weatherData: WeatherData): Single<Long> =
        weatherDao.saveLastLocationWeatherData(weatherMapper.transform(weatherData))

    override fun getLastLocationWeatherData(): Flowable<WeatherData> =
        weatherDao.getLastLocationWeatherData()
            .map { weatherMapper.transform(it) }
}
