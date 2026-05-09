package com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking

import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.AddExerciseSuccessSummary
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField
import java.time.LocalTime

sealed class SessionBookingEvent {
    data class CartItemSelected(val exerciseId: String) : SessionBookingEvent()
    data class CartItemRemoved(val exerciseId: String) : SessionBookingEvent()
    object CartCleared : SessionBookingEvent()
    data class CartFieldStepped(val exerciseId: String, val setIndex: Int, val field: CartSetField, val delta: Int) : SessionBookingEvent()
    data class CartFieldManualSet(val exerciseId: String, val setIndex: Int, val field: CartSetField, val value: String) : SessionBookingEvent()
    object SessionBookingOpened : SessionBookingEvent()
    object SessionBookingDismissed : SessionBookingEvent()
    data class BookingDateSelected(val dateMillis: Long) : SessionBookingEvent()
    data class BookingLocationSelected(val locationId: String) : SessionBookingEvent()
    data class BookingSlotToggled(val slotStart: LocalTime) : SessionBookingEvent()
    object BookingTimeSelectionCleared : SessionBookingEvent()
    object SessionBookingConfirmed : SessionBookingEvent()
    object LongSessionEditStarted : SessionBookingEvent()
    object LongSessionProceedAnyway : SessionBookingEvent()
    data class ShowAddExerciseSuccess(val summary: AddExerciseSuccessSummary) : SessionBookingEvent()
}
