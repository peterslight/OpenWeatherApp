package com.peterstev.data.network

import com.peterstev.data.models.geolocation.JsonGeoLocation
import com.peterstev.data.models.tomorrow.JsonTomorrow
import com.peterstev.data.models.weather.JsonWeatherData
import com.peterstev.data.util.MAX_LOCATION
import com.peterstev.data.util.MAX_TIMESTAMPS
import com.peterstev.data.util.UNIT_METRIC
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface BaseApiService {

    @GET("geo/1.0/direct")
    fun searchCity(
        @Query("q") city: String,
        @Query("limit") limit: Int = MAX_LOCATION,
        @Query("appid") appId: String,
    ): Observable<List<JsonGeoLocation>>

    @GET("data/2.5/onecall")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String,
        @Query("units") unit: String = UNIT_METRIC,
    ): Observable<JsonWeatherData>

    @GET("data/2.5/forecast")
    fun getTomorrowWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String,
        @Query("units") unit: String = UNIT_METRIC,
        @Query("cnt") count: Int = MAX_TIMESTAMPS,
    ): Observable<JsonTomorrow>
}
