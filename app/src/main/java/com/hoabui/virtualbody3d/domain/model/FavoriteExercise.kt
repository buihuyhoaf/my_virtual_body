package com.hoabui.virtualbody3d.domain.model

/**
 * Domain model for a favorite exercise (name, last lifted weight, drawable resource for image).
 */
data class FavoriteExercise(
    val id: String,
    val name: String,
    val exerciseVolume: String,
    val imageResId: Int
)
