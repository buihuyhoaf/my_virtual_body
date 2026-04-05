package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutExecutionStatus
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow

interface WorkoutScheduleRepository {
    suspend fun saveWorkoutSchedule(schedule: WorkoutSchedule, planZoneId: ZoneId)

    fun observeWorkoutSchedules(): Flow<List<WorkoutSchedule>>

    fun observeSchedulesInDayRange(startDay: Long, endDay: Long): Flow<List<WorkoutSchedule>>

    suspend fun getAllSchedules(): List<WorkoutSchedule>

    suspend fun updateExecutionStatus(rowId: Long, status: WorkoutExecutionStatus)
}
