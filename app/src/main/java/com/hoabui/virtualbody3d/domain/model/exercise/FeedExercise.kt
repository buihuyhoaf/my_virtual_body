package com.hoabui.virtualbody3d.domain.model.exercise

/**
 * Compact exercise row: id, name, image, optional remote URL, sets/reps.
 * Used for workout feed entries and favorite exercises.
 */
data class FeedExercise(
    val id: String,
    val name: String,
    val imageResId: Int,
    val imageResUrl: String?,
    val sets: Int,
    val reps: Int
)
