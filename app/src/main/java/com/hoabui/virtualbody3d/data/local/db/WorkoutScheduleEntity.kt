package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_schedules",
    indices = [
        Index("dayKey"),
        Index(value = ["clientId"], unique = true),
    ],
)
data class WorkoutScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val clientId: String,
    val dayKey: Long,
    val exerciseId: String,
    val sessionId: String?,
    val scheduledAtEpochMillis: Long,
    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val restSeconds: Int,
    val notes: String?,
    val measurementMode: String,
    val durationSeconds: Int?,
    val locationId: String,
    /** Stored as string; see [com.hoabui.virtualbody3d.domain.model.exercise.WorkoutExecutionStatus]. */
    val executionStatus: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
