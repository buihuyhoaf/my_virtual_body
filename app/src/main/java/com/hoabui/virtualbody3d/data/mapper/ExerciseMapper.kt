package com.hoabui.virtualbody3d.data.mapper

import android.content.Context
import com.hoabui.virtualbody3d.data.model.ExerciseDto
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.repository.ResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

class ExerciseMapper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resourceProvider: ResourceProvider,
) {
    fun toDomain(dto: ExerciseDto): Exercise = Exercise(
        id = dto.id.orEmpty(),
        name = dto.name.orEmpty(),
        image = dto.toImageSource(),
        category = dto.category.toExerciseCategory(),
        bodyRegion = dto.bodyRegion.orEmpty().toBodyRegion(),
        description = dto.description.orEmpty(),
        equipment = dto.equipment.orEmpty().toEquipmentTypeOrNull(),
        safetyNotes = dto.safetyNotes.orEmpty(),
        lastWeightKg = dto.lastWeightKg,
        measurementMode = dto.measurementMode.toMeasurementMode(),
    )

    private fun ExerciseDto.toImageSource(): ImageSource {
        imageResUrl?.takeIf { it.isNotBlank() }?.let { return ImageSource.Network(it) }
        resolveDrawableNameFromExerciseLabel(name.orEmpty(), resourceProvider)?.let {
            return ImageSource.LocalResource(it)
        }
        val resolvedName = imageResId?.let { context.safeResourceEntryName(it) }
        return ImageSource.LocalResource(resolvedName ?: FALLBACK_IMAGE_NAME)
    }

    private fun Context.safeResourceEntryName(resId: Int): String? = runCatching {
        resources.getResourceEntryName(resId)
    }.getOrNull()
}

private val ExerciseNameToDrawableNonAlphanumeric = Regex("[^a-z0-9]+")

/**
 * Basenames to try for `res/drawable` and `assets/gif` `.gif` files, derived from display [exerciseName].
 * Order: snake_case (e.g. `lat_pulldown`) then compact (`latpulldown`).
 */
internal fun exerciseNameToDrawableBasenameCandidates(exerciseName: String): List<String> {
    val trimmed = exerciseName.trim()
    if (trimmed.isEmpty()) return emptyList()
    val snake = trimmed.lowercase(Locale.US)
        .replace(ExerciseNameToDrawableNonAlphanumeric, "_")
        .trim('_')
    if (snake.isEmpty()) return emptyList()
    val compact = snake.replace("_", "")
    return buildList {
        add(snake)
        if (compact != snake) add(compact)
    }.distinct()
}

/**
 * First drawable basename that exists under `res/drawable`, derived from display [exerciseName].
 */
internal fun resolveDrawableNameFromExerciseLabel(
    exerciseName: String,
    resourceProvider: ResourceProvider,
): String? = exerciseNameToDrawableBasenameCandidates(exerciseName)
    .firstOrNull { resourceProvider.drawableResId(it) != null }

private const val FALLBACK_IMAGE_NAME = "body_unsplash"

private fun String?.toMeasurementMode(): ExerciseMeasurementMode {
    return when (this?.trim()?.lowercase().orEmpty()) {
        "duration" -> ExerciseMeasurementMode.Duration
        else -> ExerciseMeasurementMode.Strength
    }
}

private fun String?.toExerciseCategory(): ExerciseCategory {
    val key = this?.trim()?.lowercase().orEmpty()
    return when (key) {
        "strength" -> ExerciseCategory.Strength
        "mobility" -> ExerciseCategory.Mobility
        "stretching" -> ExerciseCategory.Stretching
        "cardio" -> ExerciseCategory.Cardio
        else -> ExerciseCategory.Strength
    }
}

private fun String.toBodyRegion(): BodyRegion = when (this.trim()) {
    "Chest", "CHEST" -> BodyRegion.Chest
    "Back", "BACK" -> BodyRegion.Back
    "Shoulders", "SHOULDERS" -> BodyRegion.Shoulders
    "Arms", "ARMS" -> BodyRegion.Arms
    "Core", "CORE" -> BodyRegion.Core
    "Legs", "LEGS" -> BodyRegion.Legs
    "GLUTES" -> BodyRegion.Legs
    else -> BodyRegion.Chest
}

private fun String.toEquipmentTypeOrNull(): EquipmentType? = when (this.trim()) {
    "Barbell", "BARBELL" -> EquipmentType.Barbell
    "Dumbbell", "DUMBBELL" -> EquipmentType.Dumbbell
    "Machine", "MACHINE" -> EquipmentType.Machine
    "Cable", "CABLE" -> EquipmentType.Cable
    "Bodyweight", "BODYWEIGHT" -> EquipmentType.Bodyweight
    "Kettlebell", "KETTLEBELL" -> EquipmentType.Kettlebell
    "ResistanceBand", "RESISTANCEBAND", "RESISTANCE_BAND" -> EquipmentType.ResistanceBand
    else -> null
}
