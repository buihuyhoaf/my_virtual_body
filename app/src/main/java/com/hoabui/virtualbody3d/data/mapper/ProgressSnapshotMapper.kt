package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.ProgressSnapshotDto
import com.hoabui.virtualbody3d.domain.model.body.ProgressSnapshot
import java.time.LocalDate

fun ProgressSnapshotDto.toDomain(): ProgressSnapshot = ProgressSnapshot(
    recordedOn = LocalDate.parse(dateIso),
    imageUrl = imageUrl,
    weightKg = weightKg,
    bodyFatPercent = bodyFatPercent,
    muscleMassKg = muscleMassKg,
)

fun List<ProgressSnapshotDto>.toDomain(): List<ProgressSnapshot> = map { it.toDomain() }
