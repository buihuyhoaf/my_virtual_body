package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.local.db.WorkoutSessionDao
import com.hoabui.virtualbody3d.data.mapper.toDto
import com.hoabui.virtualbody3d.data.mapper.toEntity
import com.hoabui.virtualbody3d.data.model.WorkoutSessionDto
import com.hoabui.virtualbody3d.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutSessionLocalDataSource @Inject constructor(
    private val workoutSessionDao: WorkoutSessionDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    fun observeSessionDtos(): Flow<List<WorkoutSessionDto>> =
        workoutSessionDao.observeAllSessions()
            .map { entities -> entities.map { it.toDto() } }
            .flowOn(ioDispatcher)

    suspend fun getAllDtos(): List<WorkoutSessionDto> = withContext(ioDispatcher) {
        workoutSessionDao.getAllSessions().map { it.toDto() }
    }

    suspend fun insertSession(dto: WorkoutSessionDto) {
        withContext(ioDispatcher) {
            workoutSessionDao.insertSession(dto.toEntity())
        }
    }
}
