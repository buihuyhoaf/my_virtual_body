package com.hoabui.virtualbody3d.data.local.db

import com.hoabui.virtualbody3d.data.model.ExerciseDto
import com.hoabui.virtualbody3d.data.model.NutritionSummaryDto
import com.hoabui.virtualbody3d.data.model.ProgressSnapshotDto

internal fun ExerciseEntity.toExerciseDto(): ExerciseDto = ExerciseDto(
    id = id,
    name = name,
    imageResId = null,
    imageResUrl = imageResUrl,
    localImageName = localImageName,
    bodyRegion = bodyRegion,
    regionGroup = regionGroup,
    focusMuscles = focusMuscles,
    category = category,
    description = description,
    equipment = equipment,
    safetyNotes = safetyNotes,
    lastWeightKg = lastWeightKg,
    sets = sets,
    reps = reps,
    measurementMode = measurementMode,
)

internal fun ProgressSnapshotEntity.toProgressSnapshotDto(): ProgressSnapshotDto =
    ProgressSnapshotDto(
        dateIso = dateIso,
        imageUrl = imageUrl,
        weightKg = weightKg,
        bodyFatPercent = bodyFatPercent,
        muscleMassKg = muscleMassKg,
    )

internal fun NutritionSummaryEntity.toNutritionSummaryDto(): NutritionSummaryDto =
    NutritionSummaryDto(
        intake = intake,
        burned = burned,
        goal = goal,
)
