package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.WorkoutFeedLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutFeedItem
import com.hoabui.virtualbody3d.domain.repository.WorkoutFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutFeedRepositoryImpl @Inject constructor(
    private val localDataSource: WorkoutFeedLocalDataSource
) : WorkoutFeedRepository {

    override fun getWorkoutFeed(): Flow<List<WorkoutFeedItem>> =
        localDataSource.getWorkoutFeed().map { dtos -> dtos.map { it.toDomain() } }
}

