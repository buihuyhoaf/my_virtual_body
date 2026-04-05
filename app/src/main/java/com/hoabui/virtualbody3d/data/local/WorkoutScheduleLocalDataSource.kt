package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.local.db.WorkoutScheduleDao
import com.hoabui.virtualbody3d.data.local.db.WorkoutScheduleEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

/**
 * Local persistence for workout schedules. Wraps [WorkoutScheduleDao] so repositories
 * depend on this type rather than Room DAOs directly.
 */
@Singleton
class WorkoutScheduleLocalDataSource @Inject constructor(
    private val dao: WorkoutScheduleDao,
) {
    fun observeAllSchedules(): Flow<List<WorkoutScheduleEntity>> = dao.observeAllSchedules()

    fun observeSchedulesInRange(startDay: Long, endDay: Long): Flow<List<WorkoutScheduleEntity>> =
        dao.observeSchedulesInRange(startDay, endDay)

    suspend fun getAllSchedules(): List<WorkoutScheduleEntity> = dao.getAllSchedules()

    suspend fun upsert(entity: WorkoutScheduleEntity) = dao.upsert(entity)

    suspend fun updateStatus(id: Long, status: String, now: Long): Int =
        dao.updateStatus(id, status, now)
}
