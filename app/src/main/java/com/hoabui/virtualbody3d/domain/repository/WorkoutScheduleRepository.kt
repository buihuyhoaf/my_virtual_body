package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutExecutionStatus
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutScheduleDeleteResult
import kotlinx.coroutines.flow.Flow

interface WorkoutScheduleRepository {
    suspend fun saveWorkoutSchedule(schedule: WorkoutSchedule)

    fun observeWorkoutSchedules(): Flow<List<WorkoutSchedule>>

    fun observeSchedulesInDayRange(startDay: Long, endDay: Long): Flow<List<WorkoutSchedule>>

    suspend fun getAllSchedules(): List<WorkoutSchedule>

    /** Returns a single schedule row by Room primary key, or null if missing. */
    suspend fun getWorkoutScheduleByRowId(rowId: Long): WorkoutSchedule?

    suspend fun updateExecutionStatus(rowId: Long, status: WorkoutExecutionStatus)

    /**
     * Updates an existing schedule row from cart-style fields. Returns false if the row is missing or [exerciseId] does not match.
     */
    suspend fun updateWorkoutScheduleRow(
        rowId: Long,
        exerciseId: String,
        measurementMode: ExerciseMeasurementMode,
        sets: Int,
        reps: Int,
        weightKg: Double,
        durationSeconds: Int?,
    ): Boolean

    /**
     * Deletes the row by primary key. Returns payload for undo (schedule + optional removed session), or null if absent.
     */
    suspend fun deleteWorkoutScheduleByRowId(rowId: Long): WorkoutScheduleDeleteResult?

    /**
     * Restores a delete performed via [deleteWorkoutScheduleByRowId] (re-inserts session when needed, then upserts schedule).
     */
    suspend fun restoreWorkoutScheduleDelete(result: WorkoutScheduleDeleteResult)
}
