package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.WorkoutScheduleLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDto
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutScheduleRepositoryImpl @Inject constructor(
    private val localDataSource: WorkoutScheduleLocalDataSource
) : WorkoutScheduleRepository {

    override suspend fun saveWorkoutSchedule(schedule: WorkoutSchedule) {
        localDataSource.save(schedule.toDto())
    }
}
