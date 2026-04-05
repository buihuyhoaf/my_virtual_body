package com.hoabui.virtualbody3d.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "progress_snapshots",
    indices = [
        Index(value = ["date_iso"], unique = true),
    ],
)
data class ProgressSnapshotEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "row_id")
    val rowId: Long = 0L,
    @ColumnInfo(name = "date_iso")
    val dateIso: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    @ColumnInfo(name = "weight_kg")
    val weightKg: Float?,
    @ColumnInfo(name = "body_fat_percent")
    val bodyFatPercent: Float?,
    @ColumnInfo(name = "muscle_mass_kg")
    val muscleMassKg: Float?,
)
