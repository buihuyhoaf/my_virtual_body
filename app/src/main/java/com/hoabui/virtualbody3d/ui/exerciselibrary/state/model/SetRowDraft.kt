package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable

/**
 * Represents a single set row in the cart stepper UI.
 * Used for both strength (reps + weightKg) and cardio (minutes + seconds) exercises.
 */
@Immutable
data class SetRowDraft(
    /** Repetitions per set (used for strength mode). */
    val reps: Int = 10,
    /** Weight in kilograms (used for strength mode). */
    val weightKg: Double = 0.0,
    /** Duration minutes component (used for cardio mode). */
    val minutes: Int = 0,
    /** Duration seconds component, 0–59 (used for cardio mode). */
    val seconds: Int = 30,
)
