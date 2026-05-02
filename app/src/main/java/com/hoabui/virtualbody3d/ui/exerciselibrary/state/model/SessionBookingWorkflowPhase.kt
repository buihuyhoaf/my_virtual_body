package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SessionBookingWorkflowPhase {
    data object Idle : SessionBookingWorkflowPhase
    data object AwaitingLongSessionAck : SessionBookingWorkflowPhase
    data object SlotConflict : SessionBookingWorkflowPhase
}
