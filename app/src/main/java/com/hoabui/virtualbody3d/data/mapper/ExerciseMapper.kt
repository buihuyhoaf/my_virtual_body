package com.hoabui.virtualbody3d.data.mapper

import android.content.Context
import com.hoabui.virtualbody3d.data.model.ExerciseDto
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Difficulty
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.FeedExercise
import com.hoabui.virtualbody3d.domain.model.exercise.MuscleGroup
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExerciseMapper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun toDomain(dto: ExerciseDto): Exercise = Exercise(
        id = dto.id.orEmpty(),
        name = dto.name.orEmpty(),
        image = dto.toImageSource(),
        bodyRegion = dto.bodyRegion.orEmpty().toBodyRegion(),
        difficulty = dto.difficulty.orEmpty().toDifficulty(),
        description = dto.description.orEmpty(),
        primaryMuscles = dto.primaryMuscles.orEmpty().mapNotNull { it.toMuscleGroupOrNull() },
        secondaryMuscles = dto.secondaryMuscles.orEmpty().mapNotNull { it.toMuscleGroupOrNull() },
        equipment = dto.equipment.orEmpty().toEquipmentTypeOrNull(),
        safetyNotes = dto.safetyNotes.orEmpty(),
        lastWeightKg = dto.lastWeightKg
    )

    fun toFeedExercise(dto: ExerciseDto): FeedExercise = FeedExercise(
        id = dto.id.orEmpty(),
        name = dto.name.orEmpty(),
        image = dto.toImageSource(),
        sets = dto.sets ?: 0,
        reps = dto.reps ?: 0,
        difficulty = dto.difficulty.orEmpty().toDifficulty(),
    )

    private fun ExerciseDto.toImageSource(): ImageSource {
        imageResUrl?.takeIf { it.isNotBlank() }?.let { return ImageSource.Network(it) }
        localImageName?.takeIf { it.isNotBlank() }?.let { return ImageSource.LocalResource(it) }
        val resolvedName = imageResId?.let { context.safeResourceEntryName(it) }
        return ImageSource.LocalResource(resolvedName ?: FALLBACK_IMAGE_NAME)
    }

    private fun Context.safeResourceEntryName(resId: Int): String? = runCatching {
        resources.getResourceEntryName(resId)
    }.getOrNull()
}

private const val FALLBACK_IMAGE_NAME = "body_unsplash"

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
