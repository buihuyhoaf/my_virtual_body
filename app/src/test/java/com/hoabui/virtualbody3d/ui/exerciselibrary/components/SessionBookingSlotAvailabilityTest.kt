package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionBookingSlotAvailabilityTest {

    private val d2026Jan1 = LocalDate.of(2026, 1, 1)
    private val d2026Jan2 = LocalDate.of(2026, 1, 2)
    private val d2025Dec31 = LocalDate.of(2025, 12, 31)

    @Test
    fun futureDay_allSlotsEnabled() {
        assertTrue(
            isSessionBookingSlotEnabled(
                selectedDay = d2026Jan2,
                today = d2026Jan1,
                slotStart = LocalTime.of(5, 0),
                nowMinute = LocalTime.of(22, 0),
            ),
        )
    }

    @Test
    fun pastDay_allSlotsDisabled() {
        assertFalse(
            isSessionBookingSlotEnabled(
                selectedDay = d2025Dec31,
                today = d2026Jan1,
                slotStart = LocalTime.of(22, 0),
                nowMinute = LocalTime.of(10, 0),
            ),
        )
    }

    @Test
    fun today_slotStrictlyBeforeNow_disabled() {
        assertFalse(
            isSessionBookingSlotEnabled(
                selectedDay = d2026Jan1,
                today = d2026Jan1,
                slotStart = LocalTime.of(21, 30),
                nowMinute = LocalTime.of(22, 0),
            ),
        )
    }

    @Test
    fun today_slotEqualToNowMinute_enabled() {
        assertTrue(
            isSessionBookingSlotEnabled(
                selectedDay = d2026Jan1,
                today = d2026Jan1,
                slotStart = LocalTime.of(22, 0),
                nowMinute = LocalTime.of(22, 0),
            ),
        )
    }

    @Test
    fun today_slotAfterNow_enabled() {
        assertTrue(
            isSessionBookingSlotEnabled(
                selectedDay = d2026Jan1,
                today = d2026Jan1,
                slotStart = LocalTime.of(22, 30),
                nowMinute = LocalTime.of(22, 0),
            ),
        )
    }
}
