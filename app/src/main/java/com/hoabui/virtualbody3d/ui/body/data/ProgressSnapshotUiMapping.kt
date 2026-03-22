package com.hoabui.virtualbody3d.ui.body.data

import com.hoabui.virtualbody3d.domain.model.body.ProgressSnapshot
import java.time.format.DateTimeFormatter
import java.util.Locale

private val timelineDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)

fun ProgressSnapshot.toUiModel(): ProgressSnapshotUiModel = ProgressSnapshotUiModel(
    date = recordedOn.format(timelineDateFormatter),
    imageUrl = imageUrl,
    weight = weightKg,
    bodyFat = bodyFatPercent,
    muscleMass = muscleMassKg,
)

fun List<ProgressSnapshot>.toUiModels(): List<ProgressSnapshotUiModel> = map { it.toUiModel() }
