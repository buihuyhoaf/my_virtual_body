package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_log_exercises",
    indices = [
        Index("sessionId"),
        Index("exerciseId"),
    ],
)
data class WorkoutLogExerciseEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val exerciseId: String,
    val displayNameSnapshot: String,
    val measurementMode: String,
    val startTimeMillis: Long,
    val orderIndex: Int,
)
