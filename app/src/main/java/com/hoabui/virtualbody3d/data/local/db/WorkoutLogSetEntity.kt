package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_log_sets",
    indices = [
        Index("exerciseLogId"),
    ],
)
data class WorkoutLogSetEntity(
    @PrimaryKey
    val id: String,
    val exerciseLogId: String,
    val reps: Int,
    val weightKg: Double,
    val durationSeconds: Int?,
    val setIndex: Int,
)
