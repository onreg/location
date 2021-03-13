package com.onreg01.locationtracker.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class Log(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "time") val time: Instant,
    @ColumnInfo(name = "last_name") val text: String
)