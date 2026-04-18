package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_log_sessions",
    indices = [
        Index("dayKey"),
    ],
)
data class WorkoutLogSessionEntity(
    @PrimaryKey
    val id: String,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val dayKey: String,
)
