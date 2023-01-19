package com.peterstev.domain

import com.peterstev.domain.inversion.LocalUseCaseImpl
import com.peterstev.domain.mock.MockData
import com.peterstev.domain.model.geolocation.GeoLocation
import com.peterstev.domain.model.weather.WeatherData
import com.peterstev.domain.repository.LocalRepository
import com.peterstev.domain.usecase.LocalUseCase
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class LocalUseCaseTest : Spek({

    describe("LocalUseCase") {
        val repository: LocalRepository by memoized { mockk() }
        lateinit var usecase: LocalUseCase

        beforeEachTest {
            usecase = LocalUseCaseImpl(repository)
        }

        val location = MockData.geolocation
        describe("updateLocation") {
            lateinit var result: Single<Long>
            beforeEachTest {
                every { repository.updateLocation(location) } returns Single.just(1L)
                result = usecase.updateLocation(location)
            }

            it("should return a value of 1") {
                assertThat(result.blockingGet()).isEqualTo(1L)
            }
        }

        describe("getLastLocationUpdate") {
            lateinit var result: Flowable<GeoLocation>
            beforeEachTest {
                every { repository.getLastLocationUpdate() } returns Flowable.just(location)
                result = usecase.getLastLocationUpdate()
            }

            it("should return correct data") {
                result.blockingFirst().run {
                    assertThat(longitude).isEqualTo(3.8)
                    assertThat(country).isEqualTo("Nigeria")
                    assertThat(state).isEqualTo("Lagos")
                    assertThat(latitude).isEqualTo(6.5)
                    assertThat(city).isEqualTo("ikeja")
                }
            }
        }

        describe("saveLastLocationWeatherData") {
            lateinit var result: Single<Long>
            val param = MockData.weatherData.copy(daily = mutableListOf(), tomorrow = mutableListOf())
            beforeEachTest {
                every { repository.saveLastLocationWeatherData(param) } returns Single.just(1L)
                result = usecase.saveLastLocationWeatherData(param)
            }

            it("should return a valid data") {
                assertThat(result.blockingGet()).isEqualTo(1L)
            }
        }

        describe("getLastLocationWeatherData") {
            val weatherData = MockData.weatherData
            lateinit var result: Flowable<WeatherData>
            beforeEachTest {
                every { repository.getLastLocationWeatherData() } returns Flowable.just(weatherData)
                result = usecase.getLastLocationWeatherData()
            }

            it("should return correct values") {
                result.blockingFirst().run {
                    assertThat(timezoneOffset).isEqualTo(3600)
                    assertThat(latitude).isEqualTo(6.5)
                    assertThat(longitude).isEqualTo(3.8)
                    assertThat(daily).hasSize(5)
                    assertThat(hourly).hasSize(5)
                }
            }
        }
    }
})
