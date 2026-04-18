package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.WorkoutScheduleDto
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutExecutionStatus
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime

fun WorkoutSchedule.toDto(): WorkoutScheduleDto = WorkoutScheduleDto(
    id = id,
    exerciseId = exerciseId,
    scheduledAtEpochMillis = scheduledAt
        .atZone(Clock.systemDefaultZone().zone)
        .toInstant()
        .toEpochMilli(),
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    restSeconds = restSeconds,
    notes = notes,
    measurementMode = measurementMode.toDtoValue(),
    durationSeconds = durationSeconds,
    sessionId = sessionId,
    locationId = locationId,
    exerciseImageResUrl = exerciseImageResUrl,
    exerciseLocalImageName = exerciseLocalImageName,
)

fun WorkoutScheduleDto.toDomain(): WorkoutSchedule = WorkoutSchedule(
    id = id,
    rowId = null,
    exerciseId = exerciseId,
    scheduledAt = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(scheduledAtEpochMillis),
        Clock.systemDefaultZone().zone,
    ),
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    restSeconds = restSeconds,
    notes = notes,
    measurementMode = measurementMode.toScheduleMeasurementMode(),
    durationSeconds = durationSeconds,
    sessionId = sessionId,
    locationId = locationId,
    executionStatus = WorkoutExecutionStatus.Scheduled,
    exerciseImageResUrl = exerciseImageResUrl,
    exerciseLocalImageName = exerciseLocalImageName,
)

private fun ExerciseMeasurementMode.toDtoValue(): String = when (this) {
    ExerciseMeasurementMode.Strength -> "strength"
    ExerciseMeasurementMode.Duration -> "duration"
}

private fun String.toScheduleMeasurementMode(): ExerciseMeasurementMode = when (lowercase()) {
    "duration" -> ExerciseMeasurementMode.Duration
    else -> ExerciseMeasurementMode.Strength
}
