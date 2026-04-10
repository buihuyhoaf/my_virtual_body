package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import java.time.LocalTime

/** Visual role for a selected contiguous slot range (Add to Schedule requires ≥2 slots). */
enum class TimeSlotSelectionRangeRole {
    None,
    Single,
    RangeStart,
    RangeMiddle,
    RangeEnd,
}

@Immutable
data class TimeSlotCellUiModel(
    val slotStart: LocalTime,
    val label: String,
    val selected: Boolean,
    val rangeRole: TimeSlotSelectionRangeRole,
)
