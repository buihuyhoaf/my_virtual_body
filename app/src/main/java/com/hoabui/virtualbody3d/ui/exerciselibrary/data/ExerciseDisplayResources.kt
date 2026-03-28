package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import androidx.annotation.StringRes
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.MuscleGroup

/**
 * Maps domain enums to string resource IDs for display in Exercise Library and Detail screens.
 * Use with [stringResource] in composables to get bodyRegionLabel, etc.
 */
object ExerciseDisplayResources {

    @StringRes
    fun categoryResId(category: ExerciseCategory): Int = when (category) {
        ExerciseCategory.Strength -> R.string.exercise_category_strength
        ExerciseCategory.Mobility -> R.string.exercise_category_mobility
        ExerciseCategory.Stretching -> R.string.exercise_category_stretching
        ExerciseCategory.Cardio -> R.string.exercise_category_cardio
    }

    @StringRes
    fun bodyRegionResId(region: BodyRegion): Int = when (region) {
        BodyRegion.Chest -> R.string.exercise_region_chest
        BodyRegion.Back -> R.string.exercise_region_back
        BodyRegion.Shoulders -> R.string.exercise_region_shoulders
        BodyRegion.Arms -> R.string.exercise_region_arms
        BodyRegion.Core -> R.string.exercise_region_core
        BodyRegion.Legs -> R.string.exercise_region_legs
    }

    @StringRes
    fun muscleGroupResId(muscle: MuscleGroup): Int = when (muscle) {
        MuscleGroup.Pectoralis -> R.string.exercise_muscle_pectoralis
        MuscleGroup.LatissimusDorsi -> R.string.exercise_muscle_latissimus_dorsi
        MuscleGroup.Triceps -> R.string.exercise_muscle_triceps
        MuscleGroup.Biceps -> R.string.exercise_muscle_biceps
        MuscleGroup.Deltoids -> R.string.exercise_muscle_deltoids
        MuscleGroup.Quadriceps -> R.string.exercise_muscle_quadriceps
        MuscleGroup.Hamstrings -> R.string.exercise_muscle_hamstrings
        MuscleGroup.Glutes -> R.string.exercise_muscle_glutes
        MuscleGroup.Abdominals -> R.string.exercise_muscle_abdominals
        MuscleGroup.Calves -> R.string.exercise_muscle_calves
    }

    @StringRes
    fun equipmentResId(equipment: EquipmentType?): Int = when (equipment) {
        EquipmentType.Barbell -> R.string.exercise_equipment_barbell
        EquipmentType.Dumbbell -> R.string.exercise_equipment_dumbbell
        EquipmentType.Machine -> R.string.exercise_equipment_machine
        EquipmentType.Cable -> R.string.exercise_equipment_cable
        EquipmentType.Bodyweight -> R.string.exercise_equipment_bodyweight
        EquipmentType.Kettlebell -> R.string.exercise_equipment_kettlebell
        EquipmentType.ResistanceBand -> R.string.exercise_equipment_resistance_band
        null -> R.string.exercise_equipment_none
    }
}
