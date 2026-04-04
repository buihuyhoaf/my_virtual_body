package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.WorkoutSessionDto
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import java.time.Instant

fun WorkoutSessionDto.toDomain(): WorkoutSession = WorkoutSession(
    id = id,
    startInstant = Instant.ofEpochMilli(startEpochMillis),
    endInstant = Instant.ofEpochMilli(endEpochMillis),
    locationId = locationId,
)

fun WorkoutSession.toDto(): WorkoutSessionDto = WorkoutSessionDto(
    id = id,
    startEpochMillis = startInstant.toEpochMilli(),
    endEpochMillis = endInstant.toEpochMilli(),
    locationId = locationId,
)
