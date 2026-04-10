package com.hoabui.virtualbody3d.domain.model.exercise

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

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

/** Every 30m slot from [minSlot] through [maxSlot] inclusive (same rules as [bookingSlotStartsForDay]). */
fun contiguousSlotStartsInRange(
    minSlot: LocalTime,
    maxSlot: LocalTime,
): List<LocalTime> = bookingSlotStartsForDay(
    firstSlot = minSlot,
    lastSlot = maxSlot,
    slotStepMinutes = SESSION_BOOKING_SLOT_STEP_MINUTES,
)

/**
 * Next selection after user taps [tapped] on the booking grid (**anchor & pivot**).
 *
 * Let `currMin` / `currMax` be the min/max of the current contiguous selection.
 *
 * - **Scenario 3 — tap before start** (`tapped < currMin`): reset to provisional `{tapped}`.
 * - **Scenario 2 — tap after end** (`tapped > currMax`): extend to `[currMin, tapped]` (all contiguous slots).
 * - **Scenario 1 — tap inside** (`currMin <= tapped <= currMax`): shorten to `[currMin, tapped]` (discard slots after `tapped`).
 *   Tapping the current end is a no-op on multi-slot selection.
 *
 * **Single slot:** tap same cell again clears; tap before resets; tap after extends.
 *
 * Returns `null` when [tapped] is not on the grid; `emptySet()` when the chain is invalid or selection clears.
 */
fun computeNextSlotSelectionAfterToggle(
    current: Set<LocalTime>,
    tapped: LocalTime,
    gridSlotStarts: List<LocalTime>,
): Set<LocalTime>? {
    if (tapped !in gridSlotStarts) return null
    if (current.isEmpty()) {
        return setOf(tapped)
    }
    val ordered = current.sorted()
    if (!isContiguousThirtyMinuteChain(ordered)) return emptySet()
    val currMin = ordered.first()
    val currMax = ordered.last()

    if (currMin == currMax) {
        if (tapped == currMin) return emptySet()
        if (tapped.isBefore(currMin)) return setOf(tapped)
        return contiguousSlotStartsInRange(currMin, tapped).toSet()
    }

    if (tapped.isBefore(currMin)) return setOf(tapped)
    if (tapped.isAfter(currMax)) {
        return contiguousSlotStartsInRange(currMin, tapped).toSet()
    }
    return contiguousSlotStartsInRange(currMin, tapped).toSet()
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
