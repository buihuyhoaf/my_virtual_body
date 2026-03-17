package com.hoabui.virtualbody3d.data.model

/**
 * DTO for a single day's workout in the feed (data layer only).
 * Uses dateString (ISO) to avoid java.time in data layer if needed.
 */
data class WorkoutFeedItemDto(
    val label: String,
    val dateString: String,
    val workoutName: String,
    val exercises: List<ExerciseDto>,
    val feeling: String
)
