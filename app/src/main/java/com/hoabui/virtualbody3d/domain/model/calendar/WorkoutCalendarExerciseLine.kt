package com.hoabui.virtualbody3d.domain.model.calendar

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import java.time.Instant

/**
 * Workout intensity level for color-coded visualization.
 */
enum class WorkoutIntensityLevel {
    Light,    // < 100 kcal
    Moderate, // 100-250 kcal
    High,     // > 250 kcal
}

/**
 * One exercise entry for the workout calendar day detail list (domain layer).
 * Time-related labels are now handled by the parent [WorkoutCalendarSessionBlock].
 */
data class WorkoutCalendarExerciseLine(
    val rowId: Long,
    val exerciseId: String,
    val exerciseDisplayName: String,
    val setBreakdownLabel: String,
    val caloriesLabel: String,
    val caloriesKcal: Float,
    val sets: Int,
    val reps: Int,
    val durationSeconds: Int?,
    val measurementMode: ExerciseMeasurementMode,
    val sessionId: String?,
    val image: ImageSource,
    val startInstant: Instant,
) {
    /**
     * Determines the intensity level based on calories for color-coded visualization.
     */
    val intensityLevel: WorkoutIntensityLevel
        get() = when {
            caloriesKcal < 100f -> WorkoutIntensityLevel.Light
            caloriesKcal <= 250f -> WorkoutIntensityLevel.Moderate
            else -> WorkoutIntensityLevel.High
        }
}

/**
 * A group of exercises that belong to the same workout session.
 * This enables the session-based UI architecture with sticky headers.
 */
@Immutable
data class WorkoutCalendarSessionBlock(
    val sessionId: String?,
    val sessionTimeLabel: String,  // e.g., "08:00 AM - 09:30 AM" or "08:00 AM" for single exercise
    val startInstant: Instant,
    val endInstant: Instant,
    val exercises: List<WorkoutCalendarExerciseLine>,
    val totalCaloriesKcal: Float,
) {
    val intensityLevel: WorkoutIntensityLevel
        get() = when {
            totalCaloriesKcal < 100f -> WorkoutIntensityLevel.Light
            totalCaloriesKcal <= 250f -> WorkoutIntensityLevel.Moderate
            else -> WorkoutIntensityLevel.High
        }
}

/**
 * UI-ready row for the calendar day exercise list (immutable list item; consumed by organisms).
 * Redundant fields (startTimeLabel, statusLabel) removed as per session-based architecture.
 */
@Immutable
data class WorkoutCalendarExerciseLineUiModel(
    val rowId: Long,
    val title: String,
    val setBreakdownLabel: String,
    val caloriesLabel: String,
    val intensityLevel: WorkoutIntensityLevel,
    val image: ImageSource,
)

/**
 * UI-ready session block for the grouped calendar day list (consumed by organisms).
 */
@Immutable
data class WorkoutCalendarSessionBlockUiModel(
    val sessionId: String?,
    val sessionTimeLabel: String,
    val exercises: List<WorkoutCalendarExerciseLineUiModel>,
    val totalCaloriesLabel: String,
    val intensityLevel: WorkoutIntensityLevel,
)

/**
 * Maps a domain line to a UI model.
 */
fun WorkoutCalendarExerciseLine.toUiModel(): WorkoutCalendarExerciseLineUiModel = WorkoutCalendarExerciseLineUiModel(
    rowId = rowId,
    title = exerciseDisplayName,
    setBreakdownLabel = setBreakdownLabel,
    caloriesLabel = caloriesLabel,
    intensityLevel = intensityLevel,
    image = image,
)

/**
 * Maps a domain session block to a UI model.
 */
fun WorkoutCalendarSessionBlock.toUiModel(): WorkoutCalendarSessionBlockUiModel = WorkoutCalendarSessionBlockUiModel(
    sessionId = sessionId,
    sessionTimeLabel = sessionTimeLabel,
    exercises = exercises.map { it.toUiModel() },
    totalCaloriesLabel = "${totalCaloriesKcal.toInt()} kcal",
    intensityLevel = intensityLevel,
)
