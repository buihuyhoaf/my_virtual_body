package com.hoabui.virtualbody3d.ui.exerciselibrary.cart

import androidx.compose.runtime.Immutable

/** UI-only strength vs duration presentation for selection-bar steppers (no domain coupling). */
@Immutable
enum class SelectionBarExerciseMeasurementKind {
    Strength,
    Duration,
}
