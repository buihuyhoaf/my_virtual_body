package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule

interface WorkoutScheduleRepository {
    suspend fun saveWorkoutSchedule(schedule: WorkoutSchedule)
}
