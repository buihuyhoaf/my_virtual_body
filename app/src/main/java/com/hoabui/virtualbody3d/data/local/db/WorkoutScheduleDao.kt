package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutScheduleDao {

    @Query(
        """
        SELECT * FROM workout_schedules 
        WHERE dayKey BETWEEN :startDay AND :endDay 
        ORDER BY dayKey ASC, sessionId ASC, id ASC
        """,
    )
    fun observeSchedulesInRange(startDay: Long, endDay: Long): Flow<List<WorkoutScheduleEntity>>

    @Query("SELECT * FROM workout_schedules ORDER BY dayKey ASC, id ASC")
    fun observeAllSchedules(): Flow<List<WorkoutScheduleEntity>>

    @Query("SELECT * FROM workout_schedules ORDER BY dayKey ASC, id ASC")
    suspend fun getAllSchedules(): List<WorkoutScheduleEntity>

    @Query("SELECT * FROM workout_schedules WHERE clientId = :clientId LIMIT 1")
    suspend fun findByClientId(clientId: String): WorkoutScheduleEntity?

    @Query("SELECT * FROM workout_schedules WHERE id = :rowId LIMIT 1")
    suspend fun getByRowId(rowId: Long): WorkoutScheduleEntity?

    @Query("DELETE FROM workout_schedules WHERE id = :rowId")
    suspend fun deleteByRowId(rowId: Long): Int

    @Query("SELECT COUNT(*) FROM workout_schedules WHERE sessionId = :sessionId")
    suspend fun countSchedulesForSessionId(sessionId: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: WorkoutScheduleEntity): Long

    @Update
    suspend fun update(entity: WorkoutScheduleEntity)

    @Query(
        """
        UPDATE workout_schedules 
        SET executionStatus = :status, updatedAtEpochMillis = :now 
        WHERE id = :id
        """,
    )
    suspend fun updateStatus(id: Long, status: String, now: Long): Int

    @Transaction
    suspend fun upsert(entity: WorkoutScheduleEntity) {
        val existing = findByClientId(entity.clientId)
        if (existing == null) {
            insert(entity)
        } else {
            update(
                entity.copy(
                    id = existing.id,
                    createdAtEpochMillis = existing.createdAtEpochMillis,
                ),
            )
        }
    }
}
