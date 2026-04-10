package com.hoabui.virtualbody3d.domain.model.exercise

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class SessionBookingSlotLogicTest {

    private val grid = bookingSlotStartsForDay(
        LocalTime.of(5, 0),
        LocalTime.of(21, 30),
        SESSION_BOOKING_SLOT_STEP_MINUTES,
    )

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
    fun shouldWarnLongSession_boundary() {
        assertFalse(shouldWarnLongSession(4))
        assertTrue(shouldWarnLongSession(5))
    }

    @Test
    fun computeNextSlotSelection_singleSlotTapAgain_clears() {
        val t = LocalTime.of(9, 0)
        val next = computeNextSlotSelectionAfterToggle(
            current = setOf(t),
            tapped = t,
            gridSlotStarts = grid,
        )
        assertTrue(next!!.isEmpty())
    }

    @Test
    fun computeNextSlotSelection_expand_fillsGap() {
        val next = computeNextSlotSelectionAfterToggle(
            current = setOf(LocalTime.of(9, 0)),
            tapped = LocalTime.of(10, 0),
            gridSlotStarts = grid,
        )
        assertEquals(
            setOf(LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0)),
            next,
        )
    }

    @Test
    fun scenario1_tapInside_shortensEndToTapped() {
        val current = setOf(
            LocalTime.of(9, 0),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0),
        )
        val next = computeNextSlotSelectionAfterToggle(
            current = current,
            tapped = LocalTime.of(9, 30),
            gridSlotStarts = grid,
        )
        assertEquals(
            setOf(LocalTime.of(9, 0), LocalTime.of(9, 30)),
            next,
        )
    }

    @Test
    fun reset_tapBeforeCurrMin_multiSlot_clearsToNewStartOnly() {
        val current = setOf(
            LocalTime.of(9, 0),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0),
        )
        val next = computeNextSlotSelectionAfterToggle(
            current = current,
            tapped = LocalTime.of(8, 0),
            gridSlotStarts = grid,
        )
        assertEquals(setOf(LocalTime.of(8, 0)), next)
    }

    @Test
    fun reset_tapBeforeSoleSlot_retargesStart() {
        val next = computeNextSlotSelectionAfterToggle(
            current = setOf(LocalTime.of(10, 0)),
            tapped = LocalTime.of(8, 30),
            gridSlotStarts = grid,
        )
        assertEquals(setOf(LocalTime.of(8, 30)), next)
    }

    @Test
    fun reset_doesNot_expandDownward_whenEarlierTap() {
        val current = setOf(
            LocalTime.of(9, 0),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0),
        )
        val next = computeNextSlotSelectionAfterToggle(
            current = current,
            tapped = LocalTime.of(8, 0),
            gridSlotStarts = grid,
        )
        assertEquals(1, next!!.size)
        assertTrue(LocalTime.of(10, 0) !in next)
    }

    @Test
    fun scenario1_shortenByTappingNewEndSlot() {
        val current = setOf(
            LocalTime.of(9, 0),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0),
        )
        val next = computeNextSlotSelectionAfterToggle(
            current = current,
            tapped = LocalTime.of(9, 30),
            gridSlotStarts = grid,
        )
        assertEquals(
            setOf(LocalTime.of(9, 0), LocalTime.of(9, 30)),
            next,
        )
    }

    @Test
    fun scenario1_repeatedShorten_untilSingleSlot() {
        var current = setOf(
            LocalTime.of(9, 0),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0),
        )
        current = computeNextSlotSelectionAfterToggle(current, LocalTime.of(9, 30), grid)!!
        assertEquals(
            setOf(LocalTime.of(9, 0), LocalTime.of(9, 30)),
            current,
        )
        current = computeNextSlotSelectionAfterToggle(current, LocalTime.of(9, 0), grid)!!
        assertEquals(setOf(LocalTime.of(9, 0)), current)
    }

    @Test
    fun expandBeyondCurrMax() {
        val current = setOf(
            LocalTime.of(9, 0),
            LocalTime.of(9, 30),
        )
        val next = computeNextSlotSelectionAfterToggle(
            current = current,
            tapped = LocalTime.of(10, 30),
            gridSlotStarts = grid,
        )
        assertEquals(
            setOf(
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
            ),
            next,
        )
    }

    @Test
    fun scenario1_tapStart_shortensToSingleAnchorSlot() {
        val current = setOf(
            LocalTime.of(9, 0),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0),
        )
        val next = computeNextSlotSelectionAfterToggle(
            current = current,
            tapped = LocalTime.of(9, 0),
            gridSlotStarts = grid,
        )
        assertEquals(setOf(LocalTime.of(9, 0)), next)
    }

    @Test
    fun scenario1_tapCurrentEnd_isNoOpOnMultiSlot() {
        val current = setOf(
            LocalTime.of(9, 0),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0),
        )
        val next = computeNextSlotSelectionAfterToggle(
            current = current,
            tapped = LocalTime.of(10, 0),
            gridSlotStarts = grid,
        )
        assertEquals(current, next)
    }

    @Test
    fun gridFirstSlot_expandFromSingle() {
        val next = computeNextSlotSelectionAfterToggle(
            current = setOf(LocalTime.of(5, 0)),
            tapped = LocalTime.of(5, 30),
            gridSlotStarts = grid,
        )
        assertEquals(setOf(LocalTime.of(5, 0), LocalTime.of(5, 30)), next)
    }

    @Test
    fun gridLastSlot_toggleEndCollapsesToEmptyWhenOnlyOneSlot() {
        val only = LocalTime.of(21, 30)
        val next = computeNextSlotSelectionAfterToggle(
            current = setOf(only),
            tapped = only,
            gridSlotStarts = grid,
        )
        assertTrue(next!!.isEmpty())
    }

    @Test
    fun rapidToggles_deterministic() {
        var s = emptySet<LocalTime>()
        s = computeNextSlotSelectionAfterToggle(s, LocalTime.of(12, 0), grid)!!
        s = computeNextSlotSelectionAfterToggle(s, LocalTime.of(13, 0), grid)!!
        s = computeNextSlotSelectionAfterToggle(s, LocalTime.of(11, 0), grid)!!
        assertEquals(setOf(LocalTime.of(11, 0)), s)
        s = computeNextSlotSelectionAfterToggle(s, LocalTime.of(14, 0), grid)!!
        assertEquals(
            setOf(
                LocalTime.of(11, 0),
                LocalTime.of(11, 30),
                LocalTime.of(12, 0),
                LocalTime.of(12, 30),
                LocalTime.of(13, 0),
                LocalTime.of(13, 30),
                LocalTime.of(14, 0),
            ),
            s,
        )
    }

    @Test
    fun proposedVariableSessionInterval_twoDifferentLocalDates() {
        val zone = ZoneId.of("UTC")
        val d1 = LocalDate.of(2026, 6, 1)
        val d2 = LocalDate.of(2026, 6, 2)
        val a = proposedVariableSessionInterval(d1, LocalTime.of(22, 0), LocalTime.of(23, 0), zone)
        val b = proposedVariableSessionInterval(d2, LocalTime.of(22, 0), LocalTime.of(23, 0), zone)
        assertTrue(a.start.isBefore(b.start))
        assertTrue(a.end.isBefore(b.start))
    }

    @Test
    fun tapNotInGrid_returnsNull() {
        assertNull(
            computeNextSlotSelectionAfterToggle(
                current = setOf(LocalTime.of(9, 0)),
                tapped = LocalTime.of(4, 0),
                gridSlotStarts = grid,
            ),
        )
    }

    @Test
    fun invalidNonContiguousCurrent_returnsEmptySet() {
        val next = computeNextSlotSelectionAfterToggle(
            current = setOf(LocalTime.of(9, 0), LocalTime.of(10, 0)),
            tapped = LocalTime.of(9, 30),
            gridSlotStarts = grid,
        )
        assertTrue(next!!.isEmpty())
    }
}
