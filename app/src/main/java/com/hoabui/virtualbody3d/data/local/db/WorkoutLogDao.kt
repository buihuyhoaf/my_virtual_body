package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutLogDao {

    @Transaction
    @Query(
        """
        SELECT * FROM workout_log_sessions
        WHERE dayKey = :dayKey
        ORDER BY startEpochMillis ASC
        """,
    )
    fun observeSessionsByDay(dayKey: String): Flow<List<WorkoutLogSessionWithExercises>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(entity: WorkoutLogSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(entities: List<WorkoutLogExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(entities: List<WorkoutLogSetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnergy(entities: List<WorkoutLogEnergyEntity>)

    @Query("DELETE FROM workout_log_sets WHERE exerciseLogId = :exerciseLogId")
    suspend fun deleteSetsForExercise(exerciseLogId: String): Int

    @Query("DELETE FROM workout_log_energy WHERE exerciseLogId = :exerciseLogId")
    suspend fun deleteEnergyForExercise(exerciseLogId: String): Int

    @Query("DELETE FROM workout_log_exercises WHERE id = :exerciseLogId")
    suspend fun deleteExercise(exerciseLogId: String): Int

    @Query("DELETE FROM workout_log_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String): Int

    @Transaction
    suspend fun insertFullSession(
        session: WorkoutLogSessionEntity,
        exercises: List<WorkoutLogExerciseEntity>,
        sets: List<WorkoutLogSetEntity>,
        energy: List<WorkoutLogEnergyEntity>,
    ) {
        insertSession(session)
        if (exercises.isNotEmpty()) insertExercises(exercises)
        if (sets.isNotEmpty()) insertSets(sets)
        if (energy.isNotEmpty()) insertEnergy(energy)
    }
}
