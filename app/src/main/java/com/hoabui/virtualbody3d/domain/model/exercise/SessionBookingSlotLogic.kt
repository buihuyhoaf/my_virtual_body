package com.hoabui.virtualbody3d.domain.model.exercise

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Returns true if the half-open **[proposedStart, proposedEnd)** does not overlap any busy interval
 * (half-open) for the same location.
 */
fun isIntervalFreeForBooking(
    proposed: InstantInterval,
    busyIntervals: List<InstantInterval>,
): Boolean = busyIntervals.none { proposed.overlaps(it) }

/**
 * Builds wall-clock slot starts for one local day from [firstSlot] inclusive through [lastSlot] inclusive,
 * both aligned to steps of [slotStepMinutes].
 */
fun bookingSlotStartsForDay(
    firstSlot: LocalTime,
    lastSlot: LocalTime,
    slotStepMinutes: Long,
): List<LocalTime> {
    val step = slotStepMinutes.toInt()
    require(step > 0)
    require(!firstSlot.isAfter(lastSlot)) { "firstSlot must not be after lastSlot" }
    val startMinuteOfDay = firstSlot.toSecondOfDay() / 60
    val endMinuteOfDay = lastSlot.toSecondOfDay() / 60
    val result = ArrayList<LocalTime>(1 + (endMinuteOfDay - startMinuteOfDay) / step)
    var m = startMinuteOfDay
    while (m <= endMinuteOfDay) {
        result.add(LocalTime.ofSecondOfDay(m.toLong() * 60))
        m += step
    }
    return result
}

/** Half-open **[start, start + 30m)** for a single grid row on [date] in [zoneId]. */
fun thirtyMinuteIntervalAtSlot(
    date: LocalDate,
    slotStart: LocalTime,
    zoneId: ZoneId,
): InstantInterval {
    val start = ZonedDateTime.of(date, slotStart, zoneId).toInstant()
    return instantIntervalFromStart(start, SESSION_BOOKING_SLOT_STEP_MINUTES)
}

/**
 * True if the **30-minute** row starting at [slotStart] does not overlap any busy interval.
 */
fun isThirtyMinuteSlotFree(
    date: LocalDate,
    slotStart: LocalTime,
    zoneId: ZoneId,
    busyIntervals: List<InstantInterval>,
): Boolean {
    val proposed = thirtyMinuteIntervalAtSlot(date, slotStart, zoneId)
    return isIntervalFreeForBooking(proposed, busyIntervals)
}

/** Every 30m slot from [minSlot] through [maxSlot] inclusive (same rules as [bookingSlotStartsForDay]). */
fun contiguousSlotStartsInRange(
    minSlot: LocalTime,
    maxSlot: LocalTime,
): List<LocalTime> = bookingSlotStartsForDay(
    firstSlot = minSlot,
    lastSlot = maxSlot,
    slotStepMinutes = SESSION_BOOKING_SLOT_STEP_MINUTES,
)

/** True when every 30m row in the inclusive range is free (used for expand / auto-fill validation). */
fun isContiguousThirtyMinuteRangeFree(
    date: LocalDate,
    zoneId: ZoneId,
    minSlot: LocalTime,
    maxSlot: LocalTime,
    busyIntervals: List<InstantInterval>,
): Boolean {
    for (slot in contiguousSlotStartsInRange(minSlot, maxSlot)) {
        if (!isThirtyMinuteSlotFree(date, slot, zoneId, busyIntervals)) return false
    }
    return true
}

/**
 * Next selection after user taps [tapped] on the booking grid (see Lead spec: single-slot clear,
 * edge shrink, expand + auto-fill). Returns `null` when the tap is a no-op or rejected.
 */
fun computeNextSlotSelectionAfterToggle(
    current: Set<LocalTime>,
    tapped: LocalTime,
    busyIntervals: List<InstantInterval>,
    date: LocalDate,
    zoneId: ZoneId,
    gridSlotStarts: List<LocalTime>,
): Set<LocalTime>? {
    if (tapped !in gridSlotStarts) return null
    if (current.isEmpty()) {
        if (!isThirtyMinuteSlotFree(date, tapped, zoneId, busyIntervals)) return null
        return setOf(tapped)
    }
    val ordered = current.sorted()
    if (!isContiguousThirtyMinuteChain(ordered)) return emptySet()
    val currMin = ordered.first()
    val currMax = ordered.last()
    if (currMin == currMax && tapped == currMin) return emptySet()
    if (tapped in current) {
        when (tapped) {
            currMin -> {
                val nextMin = currMin.plusMinutes(SESSION_BOOKING_SLOT_STEP_MINUTES)
                if (nextMin.isAfter(currMax)) return emptySet()
                return contiguousSlotStartsInRange(nextMin, currMax).toSet()
            }
            currMax -> {
                val nextMax = currMax.minusMinutes(SESSION_BOOKING_SLOT_STEP_MINUTES)
                if (currMin.isAfter(nextMax)) return emptySet()
                return contiguousSlotStartsInRange(currMin, nextMax).toSet()
            }
            else -> {
                // Tapped a selected slot that is not an edge: clear the whole block (edge-only shrink
                // is undiscoverable; returning null made the cell feel "stuck" selected).
                return emptySet()
            }
        }
    }
    val newMin = if (tapped.isBefore(currMin)) tapped else currMin
    val newMax = if (tapped.isAfter(currMax)) tapped else currMax
    if (!isContiguousThirtyMinuteRangeFree(date, zoneId, newMin, newMax, busyIntervals)) return null
    return contiguousSlotStartsInRange(newMin, newMax).toSet()
}

/**
 * True when [sortedSlots] is a contiguous chain of [SESSION_BOOKING_SLOT_STEP_MINUTES] steps
 * (no gaps, in ascending order).
 */
fun isContiguousThirtyMinuteChain(sortedSlots: List<LocalTime>): Boolean {
    if (sortedSlots.isEmpty()) return true
    val step = SESSION_BOOKING_SLOT_STEP_MINUTES
    var expected = sortedSlots[0]
    for (slot in sortedSlots) {
        if (slot != expected) return false
        expected = expected.plusMinutes(step)
    }
    return true
}

/**
 * If any selected slot overlaps [busyIntervals], returns an empty set (Lead default).
 * Otherwise returns [selectedSlotStarts] unchanged (only valid when the set is a contiguous chain).
 */
fun pruneSelectionAgainstBusy(
    selectedSlotStarts: Set<LocalTime>,
    busyIntervals: List<InstantInterval>,
    date: LocalDate,
    zoneId: ZoneId,
): Set<LocalTime> {
    if (selectedSlotStarts.isEmpty()) return emptySet()
    val ordered = selectedSlotStarts.sorted()
    if (!isContiguousThirtyMinuteChain(ordered)) return emptySet()
    for (slot in ordered) {
        if (!isThirtyMinuteSlotFree(date, slot, zoneId, busyIntervals)) return emptySet()
    }
    return selectedSlotStarts
}

/** Proposed workout session **[minSlot, maxSlot + 30m)** in [zoneId] (half-open). */
fun proposedVariableSessionInterval(
    date: LocalDate,
    minSlot: LocalTime,
    maxSlot: LocalTime,
    zoneId: ZoneId,
): InstantInterval {
    val start = ZonedDateTime.of(date, minSlot, zoneId).toInstant()
    val end = ZonedDateTime.of(date, maxSlot, zoneId)
        .plusMinutes(SESSION_BOOKING_SLOT_STEP_MINUTES)
        .toInstant()
    return InstantInterval(start, end)
}

/** Total minutes covered by contiguous selected slots (each row is 30 minutes). */
fun totalSelectedSessionMinutes(slotCount: Int): Int =
    slotCount * SESSION_BOOKING_SLOT_STEP_MINUTES.toInt()

/** True when booking confirm should show the long-session warning (> 2h wall duration). */
fun shouldWarnLongSession(selectedSlotCount: Int): Boolean =
    totalSelectedSessionMinutes(selectedSlotCount) > 120

/** [start, start + sessionDurationMinutes) as Instant interval from grid [date] + [slotStart] in [zoneId]. */
fun proposedSessionIntervalFromSlotStart(
    date: LocalDate,
    slotStart: LocalTime,
    zoneId: ZoneId,
    sessionDurationMinutes: Long,
): InstantInterval {
    val zdtStart = ZonedDateTime.of(date, slotStart, zoneId)
    val start = zdtStart.toInstant()
    return instantIntervalFromStart(start, sessionDurationMinutes)
}

/**
 * Legacy: true if the **60-minute** window from [slotStart] is free.
 * Prefer [isThirtyMinuteSlotFree] for per-cell booking UI.
 */
fun isStartSlotSelectable(
    date: LocalDate,
    slotStart: LocalTime,
    zoneId: ZoneId,
    busyIntervals: List<InstantInterval>,
): Boolean {
    val proposed = proposedSessionIntervalFromSlotStart(
        date = date,
        slotStart = slotStart,
        zoneId = zoneId,
        sessionDurationMinutes = SESSION_BOOKING_DURATION_MINUTES,
    )
    return isIntervalFreeForBooking(proposed, busyIntervals)
}
