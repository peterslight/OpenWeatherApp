package com.peterstev.data

import com.peterstev.data.inversion.SearchRepositoryImpl
import com.peterstev.data.mapper.CityMapper
import com.peterstev.data.mapper.TomorrowMapper
import com.peterstev.data.mapper.WeatherMapper
import com.peterstev.data.models.geolocation.JsonGeoLocation
import com.peterstev.data.models.tomorrow.JsonTomorrow
import com.peterstev.data.models.weather.JsonWeatherData
import com.peterstev.data.network.BaseApiService
import com.peterstev.domain.mock.MockData
import com.peterstev.domain.model.geolocation.GeoLocation
import com.peterstev.domain.model.weather.WeatherData
import com.peterstev.domain.model.weather.WeatherItem
import com.peterstev.domain.repository.SearchRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SearchRepositoryTest : Spek({

    describe("SearchRepository") {
        val service: BaseApiService by memoized { mockk() }
        val weatherMapper: WeatherMapper by memoized { mockk() }
        val cityMapper: CityMapper by memoized { mockk() }
        val tomorrowMapper: TomorrowMapper by memoized { mockk() }

        lateinit var repository: SearchRepository
        beforeEachTest {
            repository = SearchRepositoryImpl(service, weatherMapper, cityMapper, tomorrowMapper)
        }

        val location = MockData.geolocation
        val apikey = "System.getenv(yourApiKey)"
        describe("BaseApiService") {
            val query = "ajah"
            val jsonGeoLocations = listOf(mockk<JsonGeoLocation>())
            val locations = mutableListOf(MockData.geolocation)

            beforeEachTest {
                every { service.searchCity(city = query, appId = apikey) } returns
                        Observable.just(jsonGeoLocations)
                every { cityMapper.transform(jsonGeoLocations) } returns locations
            }

            describe("searchCity") {
                lateinit var result: Observable<MutableList<GeoLocation>>
                beforeEachTest {
                    result = repository.searchCity(query)
                }

                it("should return a non null value") {
                    assertThat(result.blockingFirst()).isNotNull
                }

                it("should return the correct values") {
                    result.blockingFirst().first().run {
                        assertThat(longitude).isEqualTo(3.8)
                        assertThat(country).isEqualTo("Nigeria")
                        assertThat(state).isEqualTo("Lagos")
                        assertThat(latitude).isEqualTo(6.5)
                        assertThat(city).isEqualTo("ikeja")
                    }
                }
            }
        }

        describe("BaseApiService") {
            val jsonWeatherData = mockk<JsonWeatherData>()

            beforeEachTest {
                every {
                    service.getWeather(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        appId = apikey
                    )
                } returns
                        Observable.just(jsonWeatherData)
                every { weatherMapper.transform(jsonWeatherData) } returns MockData.weatherData
            }

            describe("getWeather") {
                lateinit var result: Observable<WeatherData>
                beforeEachTest {
                    result = repository.getWeather(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                }

                it("should return a non null value") {
                    assertThat(result.blockingFirst()).isNotNull
                }

                it("should return the correct values") {
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

        describe("BaseApiService") {
            val jsonTomorrow = mockk<JsonTomorrow>()

            beforeEachTest {
                every {
                    service.getTomorrowWeather(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        appId = apikey
                    )
                } returns
                        Observable.just(jsonTomorrow)
                every { tomorrowMapper.transform(jsonTomorrow) } returns mutableListOf(MockData.item)
            }

            describe("getTomorrowWeather") {
                lateinit var result: Observable<MutableList<WeatherItem>>
                beforeEachTest {
                    result = repository.getTomorrowWeather(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                }

                it("should return a non null value") {
                    assertThat(result.blockingFirst()).isNotNull
                }

                it("should return the correct values") {
                    result.blockingFirst().first().run {
                        assertThat(dateTime).isEqualTo("24-01-2023")
                        assertThat(feelsLike).isEqualTo(23.5)
                        assertThat(weather).hasSize(1)
                        assertThat(weather.first().description).isEqualTo("cloudy")
                    }
                }
            }
        }
    }
})
