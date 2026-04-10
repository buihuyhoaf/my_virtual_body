package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable

/**
 * Session booking bottom sheet: mutable [input] while open; [uiModel] derived in the ViewModel combine.
 */
@Immutable
data class SessionBookingSheetState(
    val input: SessionBookingInput? = null,
    val uiModel: SessionBookingUiModel? = null,
)
