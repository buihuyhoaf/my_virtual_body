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

const val WORKOUT_INTENSITY_LIGHT_UPPER_BOUND_KCAL = 100f
const val WORKOUT_INTENSITY_MODERATE_UPPER_BOUND_KCAL = 250f

enum class WorkoutCaloriesVisualLevel {
    Low,
    Medium,
    High,
}

const val WORKOUT_CALORIES_VISUAL_LOW_UPPER_BOUND_KCAL = 15f
const val WORKOUT_CALORIES_VISUAL_MEDIUM_UPPER_BOUND_KCAL = 40f

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
            caloriesKcal < WORKOUT_INTENSITY_LIGHT_UPPER_BOUND_KCAL -> WorkoutIntensityLevel.Light
            caloriesKcal <= WORKOUT_INTENSITY_MODERATE_UPPER_BOUND_KCAL -> WorkoutIntensityLevel.Moderate
            else -> WorkoutIntensityLevel.High
        }

    val caloriesVisualLevel: WorkoutCaloriesVisualLevel
        get() = when {
            caloriesKcal < WORKOUT_CALORIES_VISUAL_LOW_UPPER_BOUND_KCAL -> WorkoutCaloriesVisualLevel.Low
            caloriesKcal <= WORKOUT_CALORIES_VISUAL_MEDIUM_UPPER_BOUND_KCAL -> WorkoutCaloriesVisualLevel.Medium
            else -> WorkoutCaloriesVisualLevel.High
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
            totalCaloriesKcal < WORKOUT_INTENSITY_LIGHT_UPPER_BOUND_KCAL -> WorkoutIntensityLevel.Light
            totalCaloriesKcal <= WORKOUT_INTENSITY_MODERATE_UPPER_BOUND_KCAL -> WorkoutIntensityLevel.Moderate
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
    val caloriesVisualLevel: WorkoutCaloriesVisualLevel,
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
    caloriesVisualLevel = caloriesVisualLevel,
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
