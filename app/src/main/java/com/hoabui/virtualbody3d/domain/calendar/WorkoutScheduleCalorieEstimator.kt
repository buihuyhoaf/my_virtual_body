package com.hoabui.virtualbody3d.domain.calendar

import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.util.CaloriesCalculator

internal const val DEFAULT_BODY_WEIGHT_FOR_CALORIES_KG = 70.0

/**
 * Calories estimate aligned with workout calendar/day detail pipelines.
 */
fun estimateScheduleCalories(schedule: WorkoutSchedule): Float {
    val durationMinutes = (schedule.durationSeconds ?: 0) / 60.0
    val totalReps = schedule.sets.coerceAtLeast(0) * schedule.reps.coerceAtLeast(0)
    return CaloriesCalculator.estimateCalories(
        exerciseId = schedule.exerciseId,
        measurementMode = schedule.measurementMode,
        durationMinutes = durationMinutes,
        totalReps = totalReps,
        averageLoadKg = schedule.weightKg.coerceAtLeast(0.0),
        bodyWeightKg = DEFAULT_BODY_WEIGHT_FOR_CALORIES_KG,
        leanBodyMassKg = null,
    )
}
