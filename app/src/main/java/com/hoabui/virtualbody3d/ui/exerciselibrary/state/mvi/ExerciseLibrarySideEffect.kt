package com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi

/**
 * Side effects decoupled from the reducer / pure dispatch.
 * Consumed by the ViewModel to start async workflows (e.g. booking confirmation).
 */
sealed interface ExerciseLibrarySideEffect {
    /** Run the session booking confirmation pipeline (prepare / long-session / commit). */
    data object RunBookingConfirmation : ExerciseLibrarySideEffect
}
