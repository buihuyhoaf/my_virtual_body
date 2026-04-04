package com.hoabui.virtualbody3d.domain.model.exercise

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

class SessionBookingSlotLogicTest {

    @Test
    fun bookingSlotStartsForDay_includesLastSlot_stopsWithoutWrapping() {
        val slots = bookingSlotStartsForDay(
            firstSlot = LocalTime.of(23, 0),
            lastSlot = LocalTime.of(23, 30),
            slotStepMinutes = 30L,
        )
        assertEquals(listOf(LocalTime.of(23, 0), LocalTime.of(23, 30)), slots)
    }

    @Test
    fun bookingSlotStartsForDay_defaultWindow_reasonableCount() {
        val slots = bookingSlotStartsForDay(
            firstSlot = SESSION_BOOKING_GRID_FIRST_SLOT,
            lastSlot = SESSION_BOOKING_GRID_LAST_SLOT,
            slotStepMinutes = SESSION_BOOKING_SLOT_STEP_MINUTES,
        )
        assertEquals(LocalTime.of(5, 0), slots.first())
        assertEquals(LocalTime.of(21, 30), slots.last())
        assertEquals(34, slots.size)
    }

    @Test
    fun pruneSelectionAgainstBusy_clearsWhenAnySlotTouchesBusy() {
        val zone = java.time.ZoneId.of("UTC")
        val date = java.time.LocalDate.of(2026, 6, 1)
        val busyStart = java.time.ZonedDateTime.of(date, LocalTime.of(8, 30), zone).toInstant()
        val busy = listOf(
            InstantInterval(
                busyStart,
                busyStart.plusSeconds(30 * 60),
            ),
        )
        val selection = setOf(LocalTime.of(8, 0), LocalTime.of(8, 30))
        assertTrue(pruneSelectionAgainstBusy(selection, busy, date, zone).isEmpty())
    }

    @Test
    fun pruneSelectionAgainstBusy_keepsWhenFree() {
        val zone = java.time.ZoneId.of("UTC")
        val date = java.time.LocalDate.of(2026, 6, 1)
        val sel = setOf(LocalTime.of(9, 0))
        assertEquals(sel, pruneSelectionAgainstBusy(sel, emptyList(), date, zone))
    }

    @Test
    fun shouldWarnLongSession_boundary() {
        assertFalse(shouldWarnLongSession(4))
        assertTrue(shouldWarnLongSession(5))
    }

    @Test
    fun computeNextSlotSelection_singleSlotTapAgain_clears() {
        val zone = java.time.ZoneId.of("UTC")
        val date = java.time.LocalDate.of(2026, 6, 1)
        val t = LocalTime.of(9, 0)
        val grid = bookingSlotStartsForDay(
            LocalTime.of(5, 0),
            LocalTime.of(21, 30),
            30L,
        )
        val next = computeNextSlotSelectionAfterToggle(
            current = setOf(t),
            tapped = t,
            busyIntervals = emptyList(),
            date = date,
            zoneId = zone,
            gridSlotStarts = grid,
        )
        assertTrue(next!!.isEmpty())
    }

    @Test
    fun computeNextSlotSelection_expand_fillsGap() {
        val zone = java.time.ZoneId.of("UTC")
        val date = java.time.LocalDate.of(2026, 6, 1)
        val grid = bookingSlotStartsForDay(
            LocalTime.of(5, 0),
            LocalTime.of(21, 30),
            30L,
        )
        val next = computeNextSlotSelectionAfterToggle(
            current = setOf(LocalTime.of(9, 0)),
            tapped = LocalTime.of(10, 0),
            busyIntervals = emptyList(),
            date = date,
            zoneId = zone,
            gridSlotStarts = grid,
        )
        assertEquals(
            setOf(LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0)),
            next,
        )
    }
}
