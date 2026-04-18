package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.local.db.WorkoutSessionEntity
import com.hoabui.virtualbody3d.data.model.WorkoutSessionDto
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import java.time.Clock
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

internal fun WorkoutSessionEntity.toDto(): WorkoutSessionDto = WorkoutSessionDto(
    id = id,
    startEpochMillis = startEpochMillis,
    endEpochMillis = endEpochMillis,
    locationId = locationId,
)

internal fun WorkoutSessionDto.toEntity(): WorkoutSessionEntity {
    val dayKey = Instant.ofEpochMilli(startEpochMillis)
        .atZone(Clock.systemDefaultZone().zone)
        .toLocalDate()
        .toEpochDay()
    return WorkoutSessionEntity(
        id = id,
        locationId = locationId,
        startEpochMillis = startEpochMillis,
        endEpochMillis = endEpochMillis,
        dayKey = dayKey,
    )
}

fun WorkoutSessionEntity.toDomain(): WorkoutSession = WorkoutSession(
    id = id,
    startInstant = Instant.ofEpochMilli(startEpochMillis),
    endInstant = Instant.ofEpochMilli(endEpochMillis),
    locationId = locationId,
)
