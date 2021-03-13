package com.onreg01.locationtracker.db

import androidx.room.*
import com.onreg01.locationtracker.App
import java.time.Instant

@Database(entities = [Log::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
}

class Converters {

    @TypeConverter
    fun toInstant(epochMilli: Long): Instant {
        return Instant.ofEpochMilli(epochMilli)
    }

    @TypeConverter
    fun fromInstant(instant: Instant): Long {
        return instant.toEpochMilli()
    }
}


object DatabaseProvider {
    val db = Room.databaseBuilder(
        App.CONTEXT,
        AppDatabase::class.java, "log-db"
    ).build()
}