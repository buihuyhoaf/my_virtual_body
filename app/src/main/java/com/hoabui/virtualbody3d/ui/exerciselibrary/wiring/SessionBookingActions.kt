package com.hoabui.virtualbody3d.ui.exerciselibrary.wiring

import java.time.LocalTime

data class SessionBookingActions(
    val onOpenSessionBooking: () -> Unit,
    val onDismissSessionBooking: () -> Unit,
    val onBookingDateSelected: (Long) -> Unit,
    val onBookingLocationSelected: (String) -> Unit,
    val onBookingSlotToggled: (LocalTime) -> Unit,
    val onBookingClearTimeSelection: () -> Unit,
    val onConfirmSessionBooking: () -> Unit,
    val onLongSessionEdit: () -> Unit,
    val onLongSessionProceedAnyway: () -> Unit,
    val onDismissAddExerciseSuccess: () -> Unit,
)
