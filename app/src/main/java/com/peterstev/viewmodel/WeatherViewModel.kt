package com.peterstev.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peterstev.domain.model.geolocation.GeoLocation
import com.peterstev.domain.model.weather.WeatherData
import com.peterstev.domain.model.weather.WeatherItem
import com.peterstev.domain.usecase.LocalUseCase
import com.peterstev.domain.usecase.SearchUseCase
import com.peterstev.util.RxSchedulers
import com.peterstev.util.thread
import com.peterstev.viewmodel.WeatherViewModel.LocationState.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val localUseCase: LocalUseCase,
    private val schedulers: RxSchedulers,
) : ViewModel() {

    private val mutableWeatherLiveData = MutableLiveData<WeatherState>()
    val weatherLiveData: LiveData<WeatherState> = mutableWeatherLiveData

    private val mutableLocationStateLiveData = MutableLiveData<LocationState>()
    val locationStateLiveData: LiveData<LocationState> = mutableLocationStateLiveData

    private val mutableGeoLocationLiveData = MutableLiveData<GeoLocation>()
    val geoLocationLiveData: LiveData<GeoLocation> = mutableGeoLocationLiveData

    private var tomorrowList: MutableList<WeatherItem> = mutableListOf()

    private val disposable by lazy { CompositeDisposable() }

    sealed class WeatherState {
        class RemoteData(val data: WeatherData) : WeatherState()
        class LocalData(val data: WeatherData) : WeatherState()
        class Error(val throwable: Throwable) : WeatherState()
        class Loading(val isLoading: Boolean) : WeatherState()
    }

    sealed class LocationState {
        class Success(val data: MutableList<GeoLocation>) : LocationState()
        class Error(val throwable: Throwable) : LocationState()
        class Loading(val isLoading: Boolean) : LocationState()
    }

    fun initialize() {
        getLiveGeoLocation()
        val subscribe = localUseCase
            .getLastLocationWeatherData()
            .thread(schedulers)
            .map {
                tomorrowList = it.tomorrow.toMutableList()
                WeatherState.LocalData(it)
            }
            .subscribe(this::updateWeatherState) {
                updateWeatherState(WeatherState.Error(it))
            }
        disposable.add(subscribe)
    }

    private fun getLiveGeoLocation() {
        val subscribe = localUseCase
            .getLastLocationUpdate()
            .distinctUntilChanged { old, new -> old.latitude == new.latitude }
            .thread(schedulers)
            .map { mutableGeoLocationLiveData.value = it }
            .subscribe()
        disposable.add(subscribe)
    }

    fun searchCity(query: String) {
        val subscribe = searchUseCase
            .searchCity(query)
            .thread(schedulers)
            .doOnSubscribe { updateLocationState(Loading(true)) }
            .doOnComplete { updateLocationState(Loading(false)) }
            .map { Success(it) }
            .subscribe(this::updateLocationState) {
                updateLocationState(Error(it))
            }
        disposable.add(subscribe)
    }

    fun getWeather(latitude: Double, longitude: Double) {
        val subscribe = searchUseCase
            .getWeather(latitude, longitude)
            .thread(schedulers)
            .doOnSubscribe { updateWeatherState(WeatherState.Loading(true)) }
            .doOnComplete { updateWeatherState(WeatherState.Loading(false)) }
            .map { WeatherState.RemoteData(it) }
            .subscribe(this::updateWeatherState) {
                updateWeatherState(WeatherState.Error(it))
            }
        disposable.add(subscribe)
    }

    fun onGeoLocationUpdated(geoLocation: GeoLocation) {
        val subscribe = localUseCase
            .updateLocation(geoLocation)
            .thread(schedulers)
            .map { }
            .subscribe()
        disposable.add(subscribe)
    }

    fun saveWeatherData(weatherData: WeatherData) {
        val subscribe = localUseCase
            .saveLastLocationWeatherData(weatherData.copy(tomorrow = tomorrowList))
            .thread(schedulers)
            .map { }
            .subscribe()
        disposable.add(subscribe)
    }

    private fun updateLocationState(state: LocationState) {
        mutableLocationStateLiveData.value = state
    }

    private fun updateWeatherState(state: WeatherState) {
        mutableWeatherLiveData.value = state
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
