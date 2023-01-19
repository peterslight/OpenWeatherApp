package com.peterstev

import com.peterstev.domain.mock.MockData
import com.peterstev.domain.usecase.LocalUseCase
import com.peterstev.domain.usecase.SearchUseCase
import com.peterstev.util.RxSchedulers
import com.peterstev.viewmodel.WeatherViewModel
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


@ExperimentalCoroutinesApi
class WeatherViewModelTest : Spek({
    include(LiveDataSpek())

    describe("WeatherViewModel") {
        val searchUseCase: SearchUseCase by memoized { mockk() }
        val localUseCase: LocalUseCase by memoized { mockk() }
        val schedulers: RxSchedulers by memoized {
            mockk {
                every { IO } returns Schedulers.trampoline()
                every { MAIN } returns Schedulers.trampoline()
            }
        }
        lateinit var viewModel: WeatherViewModel

        beforeEachTest {
            viewModel = WeatherViewModel(searchUseCase, localUseCase, schedulers)
        }

        describe("weather state") {
            val state: MutableList<WeatherViewModel.WeatherState> = mutableListOf()
            beforeEachTest {
                viewModel.weatherLiveData.observeForever { state.add(it) }
            }

            afterEachTest { state.clear() }

            context("getting local data") {
                describe("a successful local fetch") {
                    val result = MockData.weatherData

                    beforeEachTest {
                        every { localUseCase.getLastLocationUpdate() } returns Flowable.just(MockData.geolocation)
                        every { localUseCase.getLastLocationWeatherData() } returns Flowable.just(result)
                    }

                    describe("initialize") {
                        beforeEachTest { viewModel.initialize() }

                        it("should contain the correct state") {
                            assertThat(state[0]).isInstanceOf(WeatherViewModel.WeatherState.LocalData::class.java)
                        }

                        it("should contain the correct values") {
                            assertThat((state[0] as WeatherViewModel.WeatherState.LocalData).data.current.humidity).isEqualTo(60)
                            assertThat((state[0] as WeatherViewModel.WeatherState.LocalData).data.daily).hasSize(5)
                            assertThat((state[0] as WeatherViewModel.WeatherState.LocalData).data.timezoneOffset).isEqualTo(3600)
                        }

                        describe("getLiveGeoLocation") {
                            it("should contain valid data") {
                                assertThat(viewModel.geoLocationLiveData.value?.city).isEqualTo("ikeja")
                                assertThat(viewModel.geoLocationLiveData.value?.state).isEqualTo("Lagos")
                            }
                        }
                    }
                }

                describe("a failed local data") {
                    val errorMessage = "unable to find data"
                    beforeEachTest {
                        every { localUseCase.getLastLocationUpdate() } returns Flowable.just(MockData.geolocation)
                        every { localUseCase.getLastLocationWeatherData() } returns
                                Flowable.error(Exception(errorMessage))
                    }

                    describe("initialize") {
                        beforeEachTest { viewModel.initialize() }

                        it("should contain the correct state") {
                            assertThat(state[0]).isInstanceOf(WeatherViewModel.WeatherState.Error::class.java)
                        }

                        it("should contain the correct error message") {
                            assertThat((state[0] as WeatherViewModel.WeatherState.Error).throwable.message)
                                .isEqualTo("unable to find data")
                        }
                    }
                }
            }

            context("getting weather") {
                val latitude = 6.5
                val longitude = 3.8
                describe("a successful weather data") {
                    val result = MockData.weatherData

                    beforeEachTest {
                        every { searchUseCase.getWeather(latitude, longitude) } returns Observable.just(result)
                    }

                    describe("getWeather") {
                        beforeEachTest { viewModel.getWeather(latitude, longitude) }

                        it("should contain the correct state") {
                            assertThat(state[1]).isInstanceOf(WeatherViewModel.WeatherState.RemoteData::class.java)
                        }

                        it("should contain the correct values") {
                            assertThat((state[1] as WeatherViewModel.WeatherState.RemoteData).data.current.humidity).isEqualTo(60)
                            assertThat((state[1] as WeatherViewModel.WeatherState.RemoteData).data.daily).hasSize(5)
                            assertThat((state[1] as WeatherViewModel.WeatherState.RemoteData).data.timezoneOffset).isEqualTo(3600)
                        }
                    }
                }

                describe("a failed weather data") {
                    val errorMessage = "unable to find weather"
                    beforeEachTest {
                        every { searchUseCase.getWeather(latitude, longitude) } returns
                                Observable.error(Exception(errorMessage))
                    }

                    describe("getWeather") {
                        beforeEachTest { viewModel.getWeather(latitude, longitude) }

                        it("should contain the correct state") {
                            assertThat(state[1]).isInstanceOf(WeatherViewModel.WeatherState.Error::class.java)
                        }

                        it("should contain the correct error message") {
                            assertThat((state[1] as WeatherViewModel.WeatherState.Error).throwable.message)
                                .isEqualTo("unable to find weather")
                        }
                    }
                }
            }
        }

        describe("location state") {
            val state: MutableList<WeatherViewModel.LocationState> = mutableListOf()
            beforeEachTest {
                viewModel.locationStateLiveData.observeForever { state.add(it) }
            }

            afterEachTest { state.clear() }

            context("searching for city") {
                val query = "ikeja"
                describe("a successful city search") {
                    val result = mutableListOf(MockData.geolocation)

                    beforeEachTest {
                        every { searchUseCase.searchCity(query) } returns Observable.just(result)
                    }

                    describe("searchCity") {
                        beforeEachTest { viewModel.searchCity(query) }

                        it("should contain the correct state") {
                            assertThat(state[0]).isInstanceOf(WeatherViewModel.LocationState.Loading::class.java)
                            assertThat(state[1]).isInstanceOf(WeatherViewModel.LocationState.Success::class.java)
                        }

                        it("should contain the correct values") {
                            assertThat((state[1] as WeatherViewModel.LocationState.Success).data[0].city).isEqualTo("ikeja")
                            assertThat((state[1] as WeatherViewModel.LocationState.Success).data).hasSize(1)
                            assertThat((state[1] as WeatherViewModel.LocationState.Success).data[0].state).isEqualTo("Lagos")
                        }
                    }
                }

                describe("a failed city search") {
                    val errorMessage = "unable to find city"
                    beforeEachTest {
                        every { searchUseCase.searchCity(query) } returns
                                Observable.error(Exception(errorMessage))
                    }

                    describe("searchCity") {
                        beforeEachTest { viewModel.searchCity(query) }

                        it("should contain the correct state") {
                            assertThat(state[0]).isInstanceOf(WeatherViewModel.LocationState.Loading::class.java)
                            assertThat(state[1]).isInstanceOf(WeatherViewModel.LocationState.Error::class.java)
                        }

                        it("should contain the correct error message") {
                            assertThat((state[1] as WeatherViewModel.LocationState.Error).throwable.message)
                                .isEqualTo("unable to find city")
                        }
                    }
                }
            }
        }
    }
})































