package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionSummaryDao {
    @Query("SELECT * FROM nutrition_summary WHERE id = 1")
    fun observeActive(): Flow<NutritionSummaryEntity?>
}
