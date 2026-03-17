package com.hoabui.virtualbody3d.domain.model

import java.time.LocalDate

/**
 * Domain model for a single day's workout in the feed.
 * Used across app for workout feed screen and any future consumers.
 */
data class WorkoutFeedItem(
    val label: String,
    val date: LocalDate,
    val workoutName: String,
    val exercises: List<FeedExercise>,
    val feeling: String
)
