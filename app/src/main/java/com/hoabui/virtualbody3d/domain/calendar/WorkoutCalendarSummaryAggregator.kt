package com.hoabui.virtualbody3d.domain.calendar

import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarDayCellStatus
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarDaySummary
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutExecutionStatus
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.util.CaloriesCalculator
import java.time.Clock

fun groupSchedulesToDaySummaries(
    schedules: List<WorkoutSchedule>,
): Map<Long, WorkoutCalendarDaySummary> {
    if (schedules.isEmpty()) return emptyMap()
    val systemZone = Clock.systemDefaultZone().zone
    val byDay = schedules.groupBy { it.scheduledAt.atZone(systemZone).toLocalDate().toEpochDay() }
    return byDay.mapValues { (epochDay, rows) ->
        WorkoutCalendarDaySummary(
            epochDay = epochDay,
            cellStatus = rows.toCellStatus(),
            totalCaloriesKcal = rows.sumOf { schedule ->
                val durationMinutes = (schedule.durationSeconds ?: 0) / 60.0
                val totalReps = schedule.sets.coerceAtLeast(0) * schedule.reps.coerceAtLeast(0)
                CaloriesCalculator.estimateCalories(
                    exerciseId = schedule.exerciseId,
                    measurementMode = schedule.measurementMode,
                    durationMinutes = durationMinutes,
                    totalReps = totalReps,
                    averageLoadKg = schedule.weightKg.coerceAtLeast(0.0),
                    bodyWeightKg = DEFAULT_BODY_WEIGHT_KG,
                    leanBodyMassKg = null,
                ).toDouble()
            }.toFloat(),
        )
    }
}

fun List<WorkoutSchedule>.toCellStatus(): WorkoutCalendarDayCellStatus {
    if (isEmpty()) return WorkoutCalendarDayCellStatus.Empty
    val distinct = map { it.executionStatus }.distinct()
    if (distinct.size == 1) {
        return when (distinct.single()) {
            WorkoutExecutionStatus.Scheduled -> WorkoutCalendarDayCellStatus.Scheduled
            WorkoutExecutionStatus.Completed -> WorkoutCalendarDayCellStatus.Completed
            WorkoutExecutionStatus.Missed -> WorkoutCalendarDayCellStatus.Missed
            WorkoutExecutionStatus.Skipped -> WorkoutCalendarDayCellStatus.Missed
        }
    }
    return WorkoutCalendarDayCellStatus.Mixed
}

/**
 * Fallback body weight used only for schedule-based calorie estimation in month summaries,
 * where no per-user logged body-weight snapshot is available yet.
 */
private const val DEFAULT_BODY_WEIGHT_KG = 70.0
