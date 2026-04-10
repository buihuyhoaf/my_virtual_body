package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import java.time.LocalTime

@Immutable
data class SessionBookingPeriodUiModel(
    val id: SessionBookingPeriodId,
    val labelResId: Int,
    val periodStartInclusive: LocalTime,
)
