package com.onreg01.locationtracker.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {

    @Query("SELECT * FROM log")
    fun getAllLogs(): Flow<List<Log>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: Log)

    @Query("DELETE FROM log")
    suspend fun clearAll()
}