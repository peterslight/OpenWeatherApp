package com.peterstev.database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.peterstev.database.dao.WeatherDao
import com.peterstev.database.entity.LocationEntity
import com.peterstev.database.entity.WeatherEntity
import com.peterstev.database.transform.WeatherItemConverter

@Database(
    entities = [WeatherEntity::class, LocationEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(WeatherItemConverter::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {
        private const val databaseName = "weather_database"

        @Volatile
        private var database: WeatherDatabase? = null

        fun getInstance(context: Context) =
            database ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    WeatherDatabase::class.java,
                    databaseName
                ).build()
                database = instance
                instance
            }
    }
}
