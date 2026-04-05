package com.hoabui.virtualbody3d.ui.body.data

import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode

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
    val measurementMode: ExerciseMeasurementMode = ExerciseMeasurementMode.Strength,
    val durationSeconds: Int? = null,
    val highlight: UpcomingExerciseHighlight = UpcomingExerciseHighlight.None,
)

/** Placeholder sets/reps until dashboard rows are schedule-backed. */
private const val DEFAULT_STRENGTH_SETS = 4
private const val DEFAULT_STRENGTH_REPS = 12
private const val DEFAULT_DURATION_SECONDS = 300

fun Exercise.toUpcomingItem(): UpcomingWorkoutUiItem = when (measurementMode) {
    ExerciseMeasurementMode.Duration -> UpcomingWorkoutUiItem(
        id = id,
        name = name,
        image = image,
        sets = 0,
        reps = 0,
        measurementMode = measurementMode,
        durationSeconds = DEFAULT_DURATION_SECONDS,
    )
    ExerciseMeasurementMode.Strength -> UpcomingWorkoutUiItem(
        id = id,
        name = name,
        image = image,
        sets = DEFAULT_STRENGTH_SETS,
        reps = DEFAULT_STRENGTH_REPS,
        measurementMode = measurementMode,
        durationSeconds = null,
    )
}
