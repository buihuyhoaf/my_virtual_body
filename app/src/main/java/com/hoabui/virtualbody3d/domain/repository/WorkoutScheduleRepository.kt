package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import kotlinx.coroutines.flow.Flow

interface WorkoutScheduleRepository {
    suspend fun saveWorkoutSchedule(schedule: WorkoutSchedule)

    fun observeWorkoutSchedules(): Flow<List<WorkoutSchedule>>
}
