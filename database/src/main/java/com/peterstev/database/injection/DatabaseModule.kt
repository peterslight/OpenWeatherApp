package com.peterstev.database.injection

import android.app.Application
import com.peterstev.database.dao.WeatherDao
import com.peterstev.database.database.WeatherDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesWeatherDatabase(application: Application): WeatherDatabase {
        return WeatherDatabase.getInstance(application.applicationContext)
    }

    @Singleton
    @Provides
    internal fun providesWeatherDao(database: WeatherDatabase): WeatherDao {
        return database.weatherDao()
    }

}
