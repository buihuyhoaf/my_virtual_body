package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutExecutionStatus
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutScheduleDeleteResult
import kotlinx.coroutines.flow.Flow

interface WorkoutScheduleRepository {
    suspend fun saveWorkoutSchedule(schedule: WorkoutSchedule)

    fun observeWorkoutSchedules(): Flow<List<WorkoutSchedule>>

    fun observeSchedulesInDayRange(startDay: Long, endDay: Long): Flow<List<WorkoutSchedule>>

    suspend fun getAllSchedules(): List<WorkoutSchedule>

    suspend fun updateExecutionStatus(rowId: Long, status: WorkoutExecutionStatus)

    /**
     * Deletes the row by primary key. Returns payload for undo (schedule + optional removed session), or null if absent.
     */
    suspend fun deleteWorkoutScheduleByRowId(rowId: Long): WorkoutScheduleDeleteResult?

    /**
     * Restores a delete performed via [deleteWorkoutScheduleByRowId] (re-inserts session when needed, then upserts schedule).
     */
    suspend fun restoreWorkoutScheduleDelete(result: WorkoutScheduleDeleteResult)
}
