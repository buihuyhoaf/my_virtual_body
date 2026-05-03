package com.hoabui.virtualbody3d.domain.calendar

import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLine
import com.hoabui.virtualbody3d.domain.model.calendar.resolveWorkoutCalendarLineImage
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import java.time.Clock
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.roundToInt

internal fun formatCaloriesLabel(kcal: Float): String = "${kcal.roundToInt()} kcal"

internal fun buildSetBreakdownLabel(schedule: WorkoutSchedule): String {
    return when (schedule.measurementMode) {
        ExerciseMeasurementMode.Strength -> {
            val sets = schedule.sets.coerceAtLeast(0)
            val reps = schedule.reps.coerceAtLeast(0)
            if (sets <= 0 || reps <= 0) {
                ""
            } else {
                val weightKg = schedule.weightKg.coerceAtLeast(0.0)
                if (weightKg > 0.0) "$sets x $reps x ${formatBreakdownWeight(weightKg)}kg" else "$sets x $reps"
            }
        }

        ExerciseMeasurementMode.Duration -> {
            val durationSeconds = schedule.durationSeconds?.coerceAtLeast(0) ?: 0
            if (durationSeconds <= 0) "" else "$durationSeconds s"
        }
    }
}

private fun formatBreakdownWeight(weightKg: Double): String {
    val rounded = String.format(Locale.ROOT, "%.1f", weightKg)
    return rounded.removeSuffix(".0")
}

/**
 * Calendar-style exercise lines derived from persisted schedules plus catalog lookups.
 */
fun buildWorkoutCalendarExerciseLines(
    schedules: List<WorkoutSchedule>,
    exerciseById: Map<String, Exercise>,
): List<WorkoutCalendarExerciseLine> {
    val systemZone = Clock.systemDefaultZone().zone
    return schedules
        .sortedBy { it.scheduledAt }
        .mapNotNull { schedule ->
            val rowId = schedule.rowId ?: return@mapNotNull null
            val startInstant = schedule.scheduledAt.atZone(systemZone).toInstant()
            val calories = estimateScheduleCalories(schedule)
            val catalog = exerciseById[schedule.exerciseId]
            WorkoutCalendarExerciseLine(
                rowId = rowId,
                exerciseId = schedule.exerciseId,
                exerciseDisplayName = catalog?.name ?: schedule.exerciseId,
                setBreakdownLabel = buildSetBreakdownLabel(schedule),
                caloriesLabel = formatCaloriesLabel(calories),
                caloriesKcal = calories,
                sets = schedule.sets,
                reps = schedule.reps,
                durationSeconds = schedule.durationSeconds,
                measurementMode = schedule.measurementMode,
                sessionId = schedule.sessionId,
                image = resolveWorkoutCalendarLineImage(
                    exerciseLocalImageName = schedule.exerciseLocalImageName,
                    exerciseImageResUrl = schedule.exerciseImageResUrl,
                    catalogExercise = catalog,
                ),
                startInstant = startInstant,
            )
        }
}
