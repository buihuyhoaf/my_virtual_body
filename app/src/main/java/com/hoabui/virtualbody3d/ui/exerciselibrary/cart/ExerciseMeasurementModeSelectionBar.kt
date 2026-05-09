package com.hoabui.virtualbody3d.ui.exerciselibrary.cart

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode

fun ExerciseMeasurementMode.toSelectionBarExerciseMeasurementKind(): SelectionBarExerciseMeasurementKind =
    when (this) {
        ExerciseMeasurementMode.Strength -> SelectionBarExerciseMeasurementKind.Strength
        ExerciseMeasurementMode.Duration -> SelectionBarExerciseMeasurementKind.Duration
    }

fun SelectionBarExerciseMeasurementKind.toDomainMeasurementMode(): ExerciseMeasurementMode =
    when (this) {
        SelectionBarExerciseMeasurementKind.Strength -> ExerciseMeasurementMode.Strength
        SelectionBarExerciseMeasurementKind.Duration -> ExerciseMeasurementMode.Duration
    }
