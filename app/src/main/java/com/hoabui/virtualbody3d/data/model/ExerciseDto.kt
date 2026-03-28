package com.hoabui.virtualbody3d.data.model

import com.google.gson.annotations.SerializedName

data class ExerciseDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("image_res_id")
    val imageResId: Int? = null,
    @SerializedName("image_url")
    val imageResUrl: String? = null,
    @SerializedName("local_image_name")
    val localImageName: String? = null,
    @SerializedName("body_region")
    val bodyRegion: String? = null,
    @SerializedName("difficulty")
    val difficulty: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("primary_muscles")
    val primaryMuscles: List<String>? = null,
    @SerializedName("secondary_muscles")
    val secondaryMuscles: List<String>? = null,
    @SerializedName("equipment")
    val equipment: String? = null,
    @SerializedName("safety_notes")
    val safetyNotes: String? = null,
    @SerializedName("last_weight_kg")
    val lastWeightKg: Double? = null,
    @SerializedName("sets")
    val sets: Int? = null,
    @SerializedName("reps")
    val reps: Int? = null
)
