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
import com.peterstev.viewmodel.TomorrowViewModel.TomorrowState.Error
import com.peterstev.viewmodel.TomorrowViewModel.TomorrowState.RemoteData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class TomorrowViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val localUseCase: LocalUseCase,
    private val schedulers: RxSchedulers,
) : ViewModel() {

    private val mutableWeatherLiveData = MutableLiveData<TomorrowState>()
    val weatherLiveData: LiveData<TomorrowState> = mutableWeatherLiveData

    private val mutableGeoLocationLiveData = MutableLiveData<GeoLocation>()
    val geoLocationLiveData: LiveData<GeoLocation> = mutableGeoLocationLiveData

    private lateinit var weatherData: WeatherData

    sealed class TomorrowState {
        class RemoteData(val data: MutableList<WeatherItem>) : TomorrowState()
        class LocalData(val data: MutableList<WeatherItem>) : TomorrowState()
        class Error(val throwable: Throwable) : TomorrowState()
    }

    private val disposable by lazy { CompositeDisposable() }

    fun initialize() {
        getLiveGeoLocation()
        val sub = localUseCase
            .getLastLocationWeatherData()
            .thread(schedulers)
            .map {
                weatherData = it
                TomorrowState.LocalData(it.tomorrow.toMutableList())
            }
            .subscribe(this::updateWeatherState) {
                updateWeatherState(Error(it))
            }
        disposable.add(sub)
    }

    fun getTomorrowWeather(latitude: Double, longitude: Double) {
        val subscribe = searchUseCase
            .getTomorrowWeather(latitude, longitude)
            .thread(schedulers)
            .map { RemoteData(it) }
            .subscribe(this::updateWeatherState) {
                updateWeatherState(Error(it))
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

    fun saveTomorrowData(item: MutableList<WeatherItem>) {
        val data = weatherData.copy(tomorrow = item)
        val subscribe = localUseCase
            .saveLastLocationWeatherData(data)
            .thread(schedulers)
            .map { it }
            .subscribe()
        disposable.add(subscribe)
    }

    private fun updateWeatherState(state: TomorrowState) {
        mutableWeatherLiveData.value = state
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
