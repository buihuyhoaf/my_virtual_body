package com.hoabui.virtualbody3d.domain.model.exercise

import com.hoabui.virtualbody3d.domain.model.common.ImageSource

/**
 * Compact exercise row: id, name, image, sets/reps or duration for display.
 * Used for workout feed entries and favorite exercises.
 */
data class FeedExercise(
    val id: String,
    val name: String,
    val image: ImageSource,
    val sets: Int,
    val reps: Int,
    val measurementMode: ExerciseMeasurementMode = ExerciseMeasurementMode.Strength,
    val durationSeconds: Int? = null,
)
