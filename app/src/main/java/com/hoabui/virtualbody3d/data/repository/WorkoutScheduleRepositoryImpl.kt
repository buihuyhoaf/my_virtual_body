package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.WorkoutScheduleLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.data.mapper.toDto
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutScheduleRepositoryImpl @Inject constructor(
    private val localDataSource: WorkoutScheduleLocalDataSource,
) : WorkoutScheduleRepository {

    override suspend fun saveWorkoutSchedule(schedule: WorkoutSchedule) {
        localDataSource.save(schedule.toDto())
    }

    override fun observeWorkoutSchedules(): Flow<List<WorkoutSchedule>> =
        localDataSource.schedules.map { list -> list.map { it.toDomain() } }
}
