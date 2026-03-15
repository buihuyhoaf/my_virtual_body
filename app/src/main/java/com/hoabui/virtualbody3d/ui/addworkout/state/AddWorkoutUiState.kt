package com.hoabui.virtualbody3d.ui.addworkout.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.Exercise
import java.time.LocalDate
import java.time.LocalTime

/**
 * UI state for Add Workout screen.
 */
@Immutable
data class AddWorkoutUiState(
    val exercise: Exercise? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime = LocalTime.now(),
    val sets: Int = 3,
    val reps: Int = 10,
    val weightKg: Double = 0.0,
    val restSeconds: Int = 90,
    val notes: String = "",
    val showConfirmDialog: Boolean = false,
    val isWorkoutAdded: Boolean = false
)
