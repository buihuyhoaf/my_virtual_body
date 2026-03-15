package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.ExerciseDto
import com.hoabui.virtualbody3d.domain.model.BodyRegion
import com.hoabui.virtualbody3d.domain.model.Difficulty
import com.hoabui.virtualbody3d.domain.model.EquipmentType
import com.hoabui.virtualbody3d.domain.model.Exercise
import com.hoabui.virtualbody3d.domain.model.MuscleGroup

fun ExerciseDto.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    imageResId = imageResId,
    bodyRegion = bodyRegion.toBodyRegion(),
    difficulty = difficulty.toDifficulty(),
    description = description,
    primaryMuscles = primaryMuscles.mapNotNull { it.toMuscleGroupOrNull() },
    secondaryMuscles = secondaryMuscles.mapNotNull { it.toMuscleGroupOrNull() },
    equipment = equipment?.toEquipmentTypeOrNull(),
    safetyNotes = safetyNotes,
    lastWeightKg = lastWeightKg
)

/** Converts domain Exercise to DTO for detail dialog and persistence. */
fun Exercise.toDto(): ExerciseDto = ExerciseDto(
    id = id,
    name = name,
    imageResId = imageResId,
    bodyRegion = bodyRegion.name,
    difficulty = difficulty.name,
    description = description,
    primaryMuscles = primaryMuscles.map { it.name },
    secondaryMuscles = secondaryMuscles.map { it.name },
    equipment = equipment?.name,
    safetyNotes = safetyNotes,
    lastWeightKg = lastWeightKg
)

private fun String.toBodyRegion(): BodyRegion = when (this) {
    "Chest" -> BodyRegion.Chest
    "Back" -> BodyRegion.Back
    "Shoulders" -> BodyRegion.Shoulders
    "Arms" -> BodyRegion.Arms
    "Core" -> BodyRegion.Core
    "Legs" -> BodyRegion.Legs
    else -> BodyRegion.Chest
}

private fun String.toDifficulty(): Difficulty = when (this) {
    "Beginner" -> Difficulty.Beginner
    "Intermediate" -> Difficulty.Intermediate
    "Advanced" -> Difficulty.Advanced
    else -> Difficulty.Intermediate
}

private fun String.toMuscleGroupOrNull(): MuscleGroup? = when (this) {
    "Pectoralis" -> MuscleGroup.Pectoralis
    "LatissimusDorsi" -> MuscleGroup.LatissimusDorsi
    "Triceps" -> MuscleGroup.Triceps
    "Biceps" -> MuscleGroup.Biceps
    "Deltoids" -> MuscleGroup.Deltoids
    "Quadriceps" -> MuscleGroup.Quadriceps
    "Hamstrings" -> MuscleGroup.Hamstrings
    "Glutes" -> MuscleGroup.Glutes
    "Abdominals" -> MuscleGroup.Abdominals
    "Calves" -> MuscleGroup.Calves
    else -> null
}

private fun String.toEquipmentTypeOrNull(): EquipmentType? = when (this) {
    "Barbell" -> EquipmentType.Barbell
    "Dumbbell" -> EquipmentType.Dumbbell
    "Machine" -> EquipmentType.Machine
    "Cable" -> EquipmentType.Cable
    "Bodyweight" -> EquipmentType.Bodyweight
    "Kettlebell" -> EquipmentType.Kettlebell
    "ResistanceBand" -> EquipmentType.ResistanceBand
    else -> null
}
