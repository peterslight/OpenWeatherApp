package com.peterstev.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.peterstev.database.entity.LocationEntity
import com.peterstev.database.entity.WeatherEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateLocation(entity: LocationEntity): Single<Long>

    @Query("SELECT * FROM location_table WHERE id = 1")
    fun getLastLocationUpdate(): Flowable<LocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLastLocationWeatherData(entity: WeatherEntity): Single<Long>

    @Query("SELECT * FROM weather_table WHERE id = 1")
    fun getLastLocationWeatherData(): Flowable<WeatherEntity>
}
