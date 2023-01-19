package com.peterstev

import com.peterstev.domain.mock.MockData
import com.peterstev.domain.usecase.LocalUseCase
import com.peterstev.domain.usecase.SearchUseCase
import com.peterstev.util.RxSchedulers
import com.peterstev.viewmodel.TomorrowViewModel
import com.peterstev.viewmodel.TomorrowViewModel.TomorrowState.*
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
class TomorrowViewModelTest : Spek({
    include(LiveDataSpek())

    describe("TomorrowViewModel") {
        val searchUseCase: SearchUseCase by memoized { mockk() }
        val localUseCase: LocalUseCase by memoized { mockk() }
        val schedulers: RxSchedulers by memoized {
            mockk {
                every { IO } returns Schedulers.trampoline()
                every { MAIN } returns Schedulers.trampoline()
            }
        }
        lateinit var viewModel: TomorrowViewModel
        beforeEachTest {
            viewModel = TomorrowViewModel(searchUseCase, localUseCase, schedulers)
        }

        describe("tomorrow weather state") {
            val state: MutableList<TomorrowViewModel.TomorrowState> = mutableListOf()
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
                            assertThat(state[0]).isInstanceOf(LocalData::class.java)
                        }

                        it("should contain the correct values") {
                            assertThat((state[0] as LocalData).data[0].humidity).isEqualTo(60)
                            assertThat((state[0] as LocalData).data).hasSize(5)
                            assertThat((state[0] as LocalData).data[0].temp).isEqualTo(30.0)
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
                            assertThat(state[0]).isInstanceOf(Error::class.java)
                        }

                        it("should contain the correct error message") {
                            assertThat((state[0] as Error).throwable.message)
                                .isEqualTo("unable to find data")
                        }
                    }
                }
            }

            context("getting tomorrow weather") {
                val location = MockData.geolocation
                describe("a successful weather data") {
                    val result = mutableListOf(MockData.item)

                    beforeEachTest {
                        every { searchUseCase.getTomorrowWeather(location.latitude, location.longitude) } returns Observable.just(result)
                    }

                    describe("getTomorrowWeather") {
                        beforeEachTest { viewModel.getTomorrowWeather(location.latitude, location.longitude) }

                        it("should contain the correct state") {
                            assertThat(state[0]).isInstanceOf(RemoteData::class.java)
                        }

                        it("should contain the correct values") {
                            assertThat((state[0] as RemoteData).data[0].humidity).isEqualTo(60)
                            assertThat((state[0] as RemoteData).data).hasSize(1)
                            assertThat((state[0] as RemoteData).data[0].weather.firstOrNull()?.description).isEqualTo("cloudy")
                        }
                    }
                }

                describe("a failed weather data") {
                    val errorMessage = "unable to find weather"
                    beforeEachTest {
                        every { searchUseCase.getTomorrowWeather(location.latitude, location.longitude) } returns
                                Observable.error(Exception(errorMessage))
                    }

                    describe("getTomorrowWeather") {
                        beforeEachTest { viewModel.getTomorrowWeather(location.latitude, location.longitude) }

                        it("should contain the correct state") {
                            assertThat(state[0]).isInstanceOf(Error::class.java)
                        }

                        it("should contain the correct error message") {
                            assertThat((state[0] as Error).throwable.message)
                                .isEqualTo("unable to find weather")
                        }
                    }
                }
            }
        }
    }
})
