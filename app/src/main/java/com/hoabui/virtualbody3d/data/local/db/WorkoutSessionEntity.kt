package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_sessions",
    indices = [
        Index("dayKey"),
        Index(value = ["locationId", "dayKey"]),
        Index(
            value = ["locationId", "dayKey", "startEpochMillis", "endEpochMillis"],
            name = "index_workout_sessions_location_day_start_end",
        ),
    ],
)
data class WorkoutSessionEntity(
    @PrimaryKey
    val id: String,
    val locationId: String,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    /** Same convention as [WorkoutScheduleEntity.dayKey]: local date epoch day for session start in plan zone. */
    val dayKey: Long,
)
