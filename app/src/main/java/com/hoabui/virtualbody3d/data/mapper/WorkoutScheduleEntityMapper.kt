package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.local.db.WorkoutScheduleEntity
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutExecutionStatus
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime

internal fun WorkoutScheduleEntity.toDomain(): WorkoutSchedule = WorkoutSchedule(
    id = clientId,
    rowId = id,
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
    measurementMode = measurementMode.toMeasurementMode(),
    durationSeconds = durationSeconds,
    sessionId = sessionId,
    locationId = locationId,
    executionStatus = executionStatus.toExecutionStatus(),
    exerciseImageResUrl = exerciseImageResUrl,
    exerciseLocalImageName = exerciseLocalImageName,
)

internal fun WorkoutSchedule.toEntity(nowMillis: Long): WorkoutScheduleEntity {
    val systemZone = Clock.systemDefaultZone().zone
    val dayKey = scheduledAt.atZone(systemZone).toLocalDate().toEpochDay()
    val created = nowMillis
    return WorkoutScheduleEntity(
        id = rowId ?: 0L,
        clientId = id,
        dayKey = dayKey,
        exerciseId = exerciseId,
        sessionId = sessionId,
        scheduledAtEpochMillis = scheduledAt.atZone(systemZone).toInstant().toEpochMilli(),
        sets = sets,
        reps = reps,
        weightKg = weightKg,
        restSeconds = restSeconds,
        notes = notes,
        measurementMode = measurementMode.toEntityValue(),
        durationSeconds = durationSeconds,
        locationId = locationId,
        executionStatus = executionStatus.toEntityValue(),
        createdAtEpochMillis = created,
        updatedAtEpochMillis = nowMillis,
        exerciseImageResUrl = exerciseImageResUrl,
        exerciseLocalImageName = exerciseLocalImageName,
    )
}

/** For legacy import: entity row is always new. */
internal fun WorkoutSchedule.toEntityForInsert(nowMillis: Long): WorkoutScheduleEntity =
    copy(rowId = null).toEntity(nowMillis).copy(id = 0L)

fun WorkoutExecutionStatus.toStorageString(): String = toEntityValue()

private fun WorkoutExecutionStatus.toEntityValue(): String = when (this) {
    WorkoutExecutionStatus.Scheduled -> "scheduled"
    WorkoutExecutionStatus.Completed -> "completed"
    WorkoutExecutionStatus.Missed -> "missed"
    WorkoutExecutionStatus.Skipped -> "skipped"
}

private fun String.toExecutionStatus(): WorkoutExecutionStatus = when (lowercase()) {
    "completed" -> WorkoutExecutionStatus.Completed
    "missed" -> WorkoutExecutionStatus.Missed
    "skipped" -> WorkoutExecutionStatus.Skipped
    else -> WorkoutExecutionStatus.Scheduled
}

private fun ExerciseMeasurementMode.toEntityValue(): String = when (this) {
    ExerciseMeasurementMode.Strength -> "strength"
    ExerciseMeasurementMode.Duration -> "duration"
}

private fun String.toMeasurementMode(): ExerciseMeasurementMode = when (lowercase()) {
    "duration" -> ExerciseMeasurementMode.Duration
    else -> ExerciseMeasurementMode.Strength
}
