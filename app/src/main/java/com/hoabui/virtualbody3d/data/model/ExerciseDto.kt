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
    @SerializedName("region_group")
    val regionGroup: String? = null,
    @SerializedName("focus_muscles")
    val focusMuscles: List<String>? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("equipment")
    val equipment: String? = null,
    @SerializedName("safety_notes")
    val safetyNotes: String? = null,
    @SerializedName("last_weight_kg")
    val lastWeightKg: Double? = null,
    @SerializedName("sets")
    val sets: Int? = null,
    @SerializedName("reps")
    val reps: Int? = null,
    @SerializedName("measurement_mode")
    val measurementMode: String? = null,
)
