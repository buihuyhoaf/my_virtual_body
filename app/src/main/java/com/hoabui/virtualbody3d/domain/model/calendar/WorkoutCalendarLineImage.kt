package com.hoabui.virtualbody3d.domain.model.calendar

import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise

/** Matches [com.hoabui.virtualbody3d.data.mapper.FALLBACK_IMAGE_NAME] in ExerciseMapper. */
const val WORKOUT_CALENDAR_FALLBACK_DRAWABLE_NAME = "body_unsplash"

/**
 * Resolves list thumbnail: schedule snapshot → catalog exercise image → [WORKOUT_CALENDAR_FALLBACK_DRAWABLE_NAME].
 */
fun resolveWorkoutCalendarLineImage(
    exerciseLocalImageName: String?,
    exerciseImageResUrl: String?,
    catalogExercise: Exercise?,
): ImageSource {
    exerciseLocalImageName?.takeIf { it.isNotBlank() }?.let {
        return ImageSource.LocalResource(it)
    }
    exerciseImageResUrl?.takeIf { it.isNotBlank() }?.let { raw ->
        return when {
            raw.startsWith("content:") || raw.startsWith("file:") ->
                ImageSource.ContentUri(raw)
            else -> ImageSource.Network(raw)
        }
    }
    catalogExercise?.image?.let { return it }
    return ImageSource.LocalResource(WORKOUT_CALENDAR_FALLBACK_DRAWABLE_NAME)
}
