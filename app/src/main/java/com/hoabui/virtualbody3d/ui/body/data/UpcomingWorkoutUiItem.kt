package com.hoabui.virtualbody3d.ui.body.data

import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.FeedExercise

enum class UpcomingExerciseHighlight {
    None,
    New,
}

data class UpcomingWorkoutUiItem(
    val id: String,
    val name: String,
    val image: ImageSource,
    val sets: Int,
    val reps: Int,
    val highlight: UpcomingExerciseHighlight = UpcomingExerciseHighlight.None,
)

fun FeedExercise.toUpcomingItem(): UpcomingWorkoutUiItem =
    UpcomingWorkoutUiItem(
        id = this.id,
        name = this.name,
        image = this.image,
        sets = this.sets,
        reps = this.reps,
    )
