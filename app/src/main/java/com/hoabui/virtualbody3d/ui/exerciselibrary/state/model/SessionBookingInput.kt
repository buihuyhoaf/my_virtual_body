package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentSet
import java.time.LocalTime

@Immutable
data class SessionBookingInput(
    val selectedDateMillis: Long,
    val selectedLocationId: String,
    val selectedSlotStarts: PersistentSet<LocalTime>,
    val bookingExerciseSnapshot: ImmutableList<BookingExerciseSummaryUi>,
    val longSessionAcknowledged: Boolean,
    val isConfirming: Boolean,
)
