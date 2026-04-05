package com.hoabui.virtualbody3d.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_summary")
data class NutritionSummaryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 1,
    @ColumnInfo(name = "intake")
    val intake: Int,
    @ColumnInfo(name = "burned")
    val burned: Int,
    @ColumnInfo(name = "goal")
    val goal: Int,
)
