package com.peterstev.database

import com.peterstev.database.dao.WeatherDao
import com.peterstev.database.entity.LocationEntity
import com.peterstev.database.entity.WeatherEntity
import com.peterstev.database.inversion.LocalRepositoryImpl
import com.peterstev.database.transform.LocationEntityMapper
import com.peterstev.database.transform.WeatherEntityMapper
import com.peterstev.domain.mock.MockData
import com.peterstev.domain.repository.LocalRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class LocalRepositoryTest : Spek({

    describe("LocalRepository") {
        val dao: WeatherDao by memoized { mockk() }
        val weatherMapper: WeatherEntityMapper by memoized { mockk() }
        val locationMapper: LocationEntityMapper by memoized { mockk() }
        lateinit var repository: LocalRepository

        beforeEachTest {
            repository = LocalRepositoryImpl(dao, weatherMapper, locationMapper)
        }

        val entity = mockk<LocationEntity>()
        val weatherEntity = mockk<WeatherEntity>()
        describe("updating Location") {
            beforeEachTest {
                every { locationMapper.transform(MockData.geolocation) } returns entity
                every { dao.updateLocation(entity) } returns Single.just(1L)
            }

            describe("updateLocation") {
                it("should return correct values") {
                    repository
                        .updateLocation(MockData.geolocation)
                        .blockingGet()
                        .let {
                            assertThat(it).isEqualTo(1L)
                        }
                }
            }
        }

        describe("getting last location") {
            beforeEachTest {
                every { dao.getLastLocationUpdate() } returns Flowable.just(entity)
                every { locationMapper.transform(entity) } returns MockData.geolocation
            }

            describe("getLastLocationUpdate") {
                it("should return correct values") {
                    repository
                        .getLastLocationUpdate()
                        .blockingFirst()
                        .run {
                            assertThat(longitude).isEqualTo(3.8)
                            assertThat(country).isEqualTo("Nigeria")
                            assertThat(state).isEqualTo("Lagos")
                            assertThat(latitude).isEqualTo(6.5)
                            assertThat(city).isEqualTo("ikeja")
                        }
                }
            }
        }

        describe("saving location weather data") {
            beforeEachTest {
                every { dao.saveLastLocationWeatherData(weatherEntity) } returns Single.just(1L)
                every { weatherMapper.transform(MockData.weatherData) } returns weatherEntity
            }

            describe("saveLastLocationWeatherData") {
                it("should return correct values") {
                    repository
                        .saveLastLocationWeatherData(MockData.weatherData)
                        .blockingGet()
                        .let {
                            assertThat(it).isEqualTo(1L)
                        }
                }
            }
        }

        describe("getting last saved location") {
            beforeEachTest {
                every { dao.getLastLocationWeatherData() } returns Flowable.just(weatherEntity)
                every { weatherMapper.transform(weatherEntity) } returns MockData.weatherData
            }

            describe("getLastLocationWeatherData") {
                it("should return correct data") {
                    repository
                        .getLastLocationWeatherData()
                        .blockingFirst()
                        .run {
                            assertThat(timezoneOffset).isEqualTo(3600)
                            assertThat(latitude).isEqualTo(6.5)
                            assertThat(longitude).isEqualTo(3.8)
                            assertThat(daily).hasSize(5)
                            assertThat(hourly).hasSize(5)
                        }
                }
            }
        }
    }
})
