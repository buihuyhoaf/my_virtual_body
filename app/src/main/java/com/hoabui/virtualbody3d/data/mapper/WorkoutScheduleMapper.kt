package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.WorkoutScheduleDto
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun WorkoutSchedule.toDto(): WorkoutScheduleDto = WorkoutScheduleDto(
    id = id,
    exerciseId = exerciseId,
    scheduledAtEpochMillis = scheduledAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    restSeconds = restSeconds,
    notes = notes
)

fun WorkoutScheduleDto.toDomain(): WorkoutSchedule = WorkoutSchedule(
    id = id,
    exerciseId = exerciseId,
    scheduledAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(scheduledAtEpochMillis), ZoneId.systemDefault()),
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    restSeconds = restSeconds,
    notes = notes
)
