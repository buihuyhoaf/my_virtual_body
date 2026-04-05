package com.hoabui.virtualbody3d.core.extensions

import android.content.res.Resources
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLine
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLineUiModel
import com.hoabui.virtualbody3d.domain.model.calendar.toUiModel
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutExecutionStatus

fun String.formatMeasurement(unit: String): String {
    val trimmed = trim()
    if (trimmed.isBlank()) return ""
    return if (trimmed.endsWith(unit)) trimmed else "$trimmed $unit"
}

/**
 * Localized [WorkoutCalendarExerciseLineUiModel] for the calendar day list (uses [Resources], not Compose).
 */
fun WorkoutCalendarExerciseLine.toWorkoutCalendarLineUiModel(resources: Resources): WorkoutCalendarExerciseLineUiModel {
    val metricsLabel = when (measurementMode) {
        ExerciseMeasurementMode.Strength -> resources.getString(
            R.string.workout_calendar_metrics_strength,
            sets,
            reps,
        )
        ExerciseMeasurementMode.Duration -> {
            val minutes = (durationSeconds ?: 0) / 60
            resources.getString(
                R.string.workout_calendar_metrics_duration,
                minutes.coerceAtLeast(1),
            )
        }
    }
    val statusLabel = when (executionStatus) {
        WorkoutExecutionStatus.Scheduled -> resources.getString(R.string.workout_execution_scheduled)
        WorkoutExecutionStatus.Completed -> resources.getString(R.string.workout_execution_completed)
        WorkoutExecutionStatus.Missed -> resources.getString(R.string.workout_execution_missed)
        WorkoutExecutionStatus.Skipped -> resources.getString(R.string.workout_execution_skipped)
    }
    return toUiModel(metricsLabel = metricsLabel, statusLabel = statusLabel)
}
