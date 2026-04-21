package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import java.time.LocalDate
import java.time.LocalTime

/**
 * Whether a booking grid slot can be toggled for [selectedDay].
 * [nowMinute] must be [LocalTime] truncated to minutes (see [java.time.temporal.ChronoUnit.MINUTES]).
 */
internal fun isSessionBookingSlotEnabled(
    selectedDay: LocalDate,
    today: LocalDate,
    slotStart: LocalTime,
    nowMinute: LocalTime,
): Boolean = when {
    selectedDay.isBefore(today) -> false
    selectedDay.isAfter(today) -> true
    else -> !slotStart.isBefore(nowMinute)
}
