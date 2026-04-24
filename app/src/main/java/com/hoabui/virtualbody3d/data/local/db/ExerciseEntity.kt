package com.hoabui.virtualbody3d.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "local_image_name")
    val localImageName: String?,
    @ColumnInfo(name = "image_res_url")
    val imageResUrl: String?,
    @ColumnInfo(name = "body_region")
    val bodyRegion: String?,
    @ColumnInfo(name = "region_group")
    val regionGroup: String?,
    @ColumnInfo(name = "focus_muscles")
    val focusMuscles: List<String>,
    @ColumnInfo(name = "category")
    val category: String?,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "equipment")
    val equipment: String?,
    @ColumnInfo(name = "safety_notes")
    val safetyNotes: String?,
    @ColumnInfo(name = "last_weight_kg")
    val lastWeightKg: Double?,
    @ColumnInfo(name = "sets")
    val sets: Int?,
    @ColumnInfo(name = "reps")
    val reps: Int?,
    @ColumnInfo(name = "measurement_mode")
    val measurementMode: String?,
)
