package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.workoutlog.WorkoutLogSessionDetail
import com.hoabui.virtualbody3d.domain.model.workoutlog.WorkoutLogSessionInput
import kotlinx.coroutines.flow.Flow

interface WorkoutLogRepository {
    fun observeWorkoutLogsByDay(dayKey: String): Flow<List<WorkoutLogSessionDetail>>
    suspend fun saveWorkoutLogSession(session: WorkoutLogSessionInput)
}
