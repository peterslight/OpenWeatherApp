package com.peterstev.domain

import com.peterstev.domain.inversion.SearchUseCaseImpl
import com.peterstev.domain.mock.MockData
import com.peterstev.domain.model.geolocation.GeoLocation
import com.peterstev.domain.model.weather.WeatherData
import com.peterstev.domain.model.weather.WeatherItem
import com.peterstev.domain.repository.SearchRepository
import com.peterstev.domain.usecase.SearchUseCase
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SearchUseCaseTest : Spek({

    describe("SearchUseCase") {
        val repository: SearchRepository by memoized { mockk() }
        lateinit var usecase: SearchUseCase

        beforeEachTest {
            usecase = SearchUseCaseImpl(repository)
        }

        val location = MockData.geolocation
        describe("search city") {
            val locations = mutableListOf(location)

            lateinit var result: Observable<MutableList<GeoLocation>>
            val query = "ikeja"
            beforeEachTest {
                every { repository.searchCity(query) } returns Observable.just(locations)
                result = usecase.searchCity(query)
            }

            it("should return correct values") {
                result.blockingFirst().first().run {
                    assertThat(country).isEqualTo("Nigeria")
                    assertThat(latitude).isEqualTo(6.5)
                    assertThat(longitude).isEqualTo(3.8)
                    assertThat(city).isEqualTo("ikeja")
                    assertThat(state).isEqualTo("Lagos")
                }
            }
        }

        describe("getWeather") {
            val weather = MockData.weatherData
            lateinit var result: Observable<WeatherData>
            beforeEachTest {
                every { repository.getWeather(location.latitude, location.longitude) } returns Observable.just(weather)
                result = usecase.getWeather(location.latitude, location.longitude)
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

        describe("getTomorrowWeather") {
            val weatherItemList = mutableListOf(MockData.item)
            lateinit var result: Observable<MutableList<WeatherItem>>
            beforeEachTest {
                every { repository.getTomorrowWeather(location.latitude, location.longitude) } returns Observable.just(weatherItemList)
                result = usecase.getTomorrowWeather(location.latitude, location.longitude)
            }

            it("should return correct values") {
                result.blockingFirst().first().run {
                    assertThat(dateTime).isEqualTo("24-01-2023")
                    assertThat(feelsLike).isEqualTo(23.5)
                    assertThat(weather).hasSize(1)
                    assertThat(weather.first().description).isEqualTo("cloudy")
                }
            }
        }
    }
})











