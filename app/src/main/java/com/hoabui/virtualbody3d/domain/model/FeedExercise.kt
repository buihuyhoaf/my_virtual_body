package com.hoabui.virtualbody3d.domain.model

/**
 * Domain model for an exercise entry in the workout feed (name + sets/reps).
 * Used across app for feed display; distinct from full [Exercise] used in library.
 */
data class FeedExercise(
    val id: String,
    val name: String,
    val imageResId: Int,
    val imageResUrl: String?,
    val sets: Int,
    val reps: Int
)
