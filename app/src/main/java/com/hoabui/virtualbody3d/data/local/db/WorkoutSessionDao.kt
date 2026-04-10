package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {

    @Query("SELECT * FROM workout_sessions ORDER BY startEpochMillis ASC")
    fun observeAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions ORDER BY startEpochMillis ASC")
    suspend fun getAllSessions(): List<WorkoutSessionEntity>

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getById(sessionId: String): WorkoutSessionEntity?

    @Query(
        """
        SELECT * FROM workout_sessions
        WHERE locationId = :locationId
          AND dayKey = :dayKey
          AND startEpochMillis = :startEpochMillis
          AND endEpochMillis = :endEpochMillis
        LIMIT 1
        """,
    )
    suspend fun findByLocationDayAndInterval(
        locationId: String,
        dayKey: Long,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): WorkoutSessionEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSession(entity: WorkoutSessionEntity)

    @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
    suspend fun deleteById(sessionId: String): Int
}
