package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_log_energy")
data class WorkoutLogEnergyEntity(
    @PrimaryKey
    val exerciseLogId: String,
    val kcal: Float,
    val bodyWeightUsed: Double,
    val metUsed: Double,
    val epocFactorUsed: Double,
)
