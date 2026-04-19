package com.hoabui.virtualbody3d.data.local

import androidx.room.withTransaction
import com.hoabui.virtualbody3d.data.local.db.VirtualBodyDatabase
import com.hoabui.virtualbody3d.data.local.db.WorkoutScheduleEntity
import com.hoabui.virtualbody3d.data.local.db.WorkoutSessionEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

/**
 * Local persistence for workout schedules. Wraps Room so repositories
 * depend on this type rather than DAOs directly.
 */
@Singleton
class WorkoutScheduleLocalDataSource @Inject constructor(
    private val db: VirtualBodyDatabase,
) {
    private val scheduleDao get() = db.workoutScheduleDao()
    private val sessionDao get() = db.workoutSessionDao()

    fun observeAllSchedules(): Flow<List<WorkoutScheduleEntity>> = scheduleDao.observeAllSchedules()

    fun observeSchedulesInRange(startDay: Long, endDay: Long): Flow<List<WorkoutScheduleEntity>> =
        scheduleDao.observeSchedulesInRange(startDay, endDay)

    suspend fun getAllSchedules(): List<WorkoutScheduleEntity> = scheduleDao.getAllSchedules()

    suspend fun getScheduleByRowId(rowId: Long): WorkoutScheduleEntity? = scheduleDao.getByRowId(rowId)

    suspend fun updateScheduleEntity(entity: WorkoutScheduleEntity) = scheduleDao.update(entity)

    suspend fun upsert(entity: WorkoutScheduleEntity) = scheduleDao.upsert(entity)

    suspend fun listDistinctExerciseIdsForSession(sessionId: String): List<String> =
        scheduleDao.listDistinctExerciseIdsForSession(sessionId)

    suspend fun updateStatus(id: Long, status: String, now: Long): Int =
        scheduleDao.updateStatus(id, status, now)

    /**
     * Deletes the schedule row and removes the session row when no schedule still references it.
     * Returns the deleted schedule and an optional session snapshot removed for undo, or null if the row was missing.
     */
    suspend fun deleteScheduleByRowIdWithSessionCleanup(rowId: Long): Pair<WorkoutScheduleEntity, WorkoutSessionEntity?>? =
        db.withTransaction {
            val entity = scheduleDao.getByRowId(rowId) ?: return@withTransaction null
            val sid = entity.sessionId
            val sessionSnapshot =
                if (sid != null && scheduleDao.countSchedulesForSessionId(sid) == 1) {
                    sessionDao.getById(sid)
                } else {
                    null
                }
            scheduleDao.deleteByRowId(rowId)
            if (sid != null && scheduleDao.countSchedulesForSessionId(sid) == 0) {
                sessionDao.deleteById(sid)
            }
            Pair(entity, sessionSnapshot)
        }
}
