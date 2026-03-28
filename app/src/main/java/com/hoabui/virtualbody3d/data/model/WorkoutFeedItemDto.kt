package com.hoabui.virtualbody3d.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO for a single day's workout in the feed (data layer only).
 * Uses dateString (ISO) to avoid java.time in data layer if needed.
 */
data class WorkoutFeedItemDto(
    @SerializedName("label")
    val label: String,
    @SerializedName("date")
    val dateString: String,
    @SerializedName("workout_name")
    val workoutName: String,
    @SerializedName("exercises")
    val exercises: List<ExerciseDto>,
    @SerializedName("duration_minutes")
    val durationMinutes: Int,
    @SerializedName("estimated_calories")
    val estimatedCalories: Int,
    @SerializedName("muscle_groups")
    val muscleGroups: List<String>
)
