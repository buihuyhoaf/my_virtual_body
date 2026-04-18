package com.hoabui.virtualbody3d.domain.model.calendar

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutExecutionStatus

/**
 * One scheduled line for the workout calendar day detail list (domain layer).
 */
data class WorkoutCalendarExerciseLine(
    val rowId: Long,
    val exerciseId: String,
    val exerciseDisplayName: String,
    val startTimeLabel: String,
    val setBreakdownLabel: String,
    val caloriesLabel: String,
    val sets: Int,
    val reps: Int,
    val durationSeconds: Int?,
    val measurementMode: ExerciseMeasurementMode,
    val executionStatus: WorkoutExecutionStatus,
    val sessionId: String?,
    val image: ImageSource,
)

/**
 * UI-ready row for the calendar day exercise list (immutable list item; consumed by organisms).
 */
@Immutable
data class WorkoutCalendarExerciseLineUiModel(
    val rowId: Long,
    val title: String,
    val startTimeLabel: String,
    val setBreakdownLabel: String,
    val caloriesLabel: String,
    val statusLabel: String,
    val image: ImageSource,
)

/**
 * Maps a domain line to a UI model using **already localized** labels from the presentation layer.
 */
fun WorkoutCalendarExerciseLine.toUiModel(
    statusLabel: String,
): WorkoutCalendarExerciseLineUiModel = WorkoutCalendarExerciseLineUiModel(
    rowId = rowId,
    title = exerciseDisplayName,
    startTimeLabel = startTimeLabel,
    setBreakdownLabel = setBreakdownLabel,
    caloriesLabel = caloriesLabel,
    statusLabel = statusLabel,
    image = image,
)
