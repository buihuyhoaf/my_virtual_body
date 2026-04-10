package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import java.time.Instant

/** Shown after cart commit succeeds; cleared when the user dismisses the success dialog. */
@Immutable
data class AddExerciseSuccessSummary(
    val sessionStartInstant: Instant,
    val sessionEndInstant: Instant,
    val scheduledDateMillis: Long,
    val exerciseCount: Int,
    /** First exercise in cart order for "Squat and N others"; null/blank → generic count-only line in UI. */
    val primaryExerciseTitle: String?,
    val locationDisplayName: String,
)
