package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.WorkoutSchedule

interface WorkoutScheduleRepository {
    suspend fun saveWorkoutSchedule(schedule: WorkoutSchedule)
}
