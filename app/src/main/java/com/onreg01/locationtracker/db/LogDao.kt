package com.onreg01.locationtracker.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Instant

@Dao
interface LogDao {

    @Query("SELECT * FROM log ORDER BY time ASC")
    fun getAllLogs(): Flow<List<Log>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: Log)

    @Query("DELETE FROM log")
    suspend fun clearAll()
}

fun log(text: String) {
    GlobalScope.launch {
        DatabaseProvider.db
            .logDao()
            .insertLog(Log(0, Instant.now(), text))
    }
}