package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

/**
 * Identifies which stepper field is being manipulated in a cart set row.
 * SETS controls the number of rows (add/remove); the others update a specific [SetRowDraft] value.
 */
enum class CartSetField {
    /** Number of sets (controls row count). Step = ±1. */
    SETS,
    /** Repetitions per set. Step = ±1. */
    REPS,
    /** Weight in kilograms. Step = ±2.5. */
    WEIGHT,
    /** Duration minutes component. Step = ±1. */
    MINUTES,
    /** Duration seconds component. Step = ±30 (normalised across minutes). */
    SECONDS,
}
