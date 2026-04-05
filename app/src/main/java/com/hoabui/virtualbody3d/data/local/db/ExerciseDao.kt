package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY id ASC")
    fun observeAll(): Flow<List<ExerciseEntity>>
}
