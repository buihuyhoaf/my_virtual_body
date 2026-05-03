package com.hoabui.virtualbody3d.domain.calendar

import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLine
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarSessionBlock
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import java.time.Clock
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun groupLinesIntoWorkoutSessionBlocks(
    lines: List<WorkoutCalendarExerciseLine>,
    workoutSessionsById: Map<String, WorkoutSession>,
): List<WorkoutCalendarSessionBlock> {
    if (lines.isEmpty()) return emptyList()

    val locale = Locale.getDefault()
    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
    val systemZone = Clock.systemDefaultZone().zone

    val groupedBySession = lines.groupBy { it.sessionId }
    return groupedBySession.map { (sessionId, exercises) ->
        val sortedExercises = exercises.sortedBy { it.startInstant }
        val fallbackStartInstant = sortedExercises.first().startInstant
        val startInstant = resolveSessionStartInstant(
            sessionId = sessionId,
            workoutSessionsById = workoutSessionsById,
            fallbackStartInstant = fallbackStartInstant,
        )
        val endInstant = resolveSessionEndInstant(
            sessionId = sessionId,
            workoutSessionsById = workoutSessionsById,
            startInstant = startInstant,
            sortedExercises = sortedExercises,
        )
        val startTime = startInstant.atZone(systemZone).toLocalTime().format(timeFormatter)
        val endTime = endInstant.atZone(systemZone).toLocalTime().format(timeFormatter)

        WorkoutCalendarSessionBlock(
            sessionId = sessionId,
            sessionTimeLabel = "$startTime - $endTime",
            startInstant = startInstant,
            endInstant = endInstant,
            exercises = sortedExercises,
            totalCaloriesKcal = sortedExercises.sumOf { it.caloriesKcal.toDouble() }.toFloat(),
        )
    }.sortedBy { it.startInstant }
}

private fun resolveSessionStartInstant(
    sessionId: String?,
    workoutSessionsById: Map<String, WorkoutSession>,
    fallbackStartInstant: Instant,
): Instant {
    if (sessionId.isNullOrBlank()) return fallbackStartInstant
    return workoutSessionsById[sessionId]?.startInstant ?: fallbackStartInstant
}

private fun resolveSessionEndInstant(
    sessionId: String?,
    workoutSessionsById: Map<String, WorkoutSession>,
    startInstant: Instant,
    sortedExercises: List<WorkoutCalendarExerciseLine>,
): Instant {
    if (!sessionId.isNullOrBlank()) {
        workoutSessionsById[sessionId]?.endInstant?.let { return it }
    }
    val lastExercise = sortedExercises.lastOrNull() ?: return startInstant
    val lastDurationSeconds = lastExercise.durationSeconds?.coerceAtLeast(0) ?: 0
    return if (lastDurationSeconds > 0) {
        lastExercise.startInstant.plusSeconds(lastDurationSeconds.toLong())
    } else {
        lastExercise.startInstant
    }
}
