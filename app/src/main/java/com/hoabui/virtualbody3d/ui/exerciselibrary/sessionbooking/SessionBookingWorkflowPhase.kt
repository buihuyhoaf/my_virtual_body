package com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking

sealed interface SessionBookingWorkflowPhase {
    data object Idle : SessionBookingWorkflowPhase
    data object AwaitingLongSessionAck : SessionBookingWorkflowPhase
    data object SlotConflict : SessionBookingWorkflowPhase
}
