package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.SlotDensityTier
import java.time.LocalTime

@Immutable
data class TimeSlotCellUiModel(
    val slotStart: LocalTime,
    val label: String,
    val selected: Boolean,
    val densityTier: SlotDensityTier,
    val overCapacity: Boolean,
    val utilizationRatio: Float,
)
