package com.hoabui.virtualbody3d.ui.body.data

import com.hoabui.virtualbody3d.domain.model.exercise.FeedExercise


data class UpcomingWorkoutUiItem(
    val id: String,
    val name: String,
    val imageResId: Int,
    val imageResUrl: String? = null,
    val sets: Int,
    val reps: Int,
)

fun FeedExercise.toUpcomingItem(): UpcomingWorkoutUiItem =
    UpcomingWorkoutUiItem(
        id = this.id,
        name = this.name,
        imageResId = this.imageResId,
        imageResUrl = this.imageResUrl,
        sets = this.sets,
        reps = this.reps,
    )