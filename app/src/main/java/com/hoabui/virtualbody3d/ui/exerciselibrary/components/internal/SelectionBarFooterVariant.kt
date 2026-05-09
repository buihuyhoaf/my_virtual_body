package com.hoabui.virtualbody3d.ui.exerciselibrary.components.internal

internal sealed interface SelectionBarFooterVariant {
    data class BookingCartPrimary(
        val bookingEnabled: Boolean,
        val onAddToSession: () -> Unit,
    ) : SelectionBarFooterVariant

    data class SessionBookingConfirmCancel(
        val isConfirmEnabled: Boolean,
        val onConfirm: () -> Unit,
        val onCancel: () -> Unit,
    ) : SelectionBarFooterVariant

    data class CalendarScheduleSaveDelete(
        val isSaveEnabled: Boolean,
        val onSave: () -> Unit,
        val onDelete: () -> Unit,
    ) : SelectionBarFooterVariant
}
