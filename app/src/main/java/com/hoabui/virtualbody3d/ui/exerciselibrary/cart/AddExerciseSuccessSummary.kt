package com.hoabui.virtualbody3d.ui.exerciselibrary.cart

import androidx.compose.runtime.Immutable
import java.time.Instant

@Immutable
data class AddExerciseSuccessSummary(
    val sessionStartInstant: Instant,
    val sessionEndInstant: Instant,
    val scheduledDateMillis: Long,
    val exerciseCount: Int,
    val primaryExerciseTitle: String?,
    val locationDisplayName: String,
)
