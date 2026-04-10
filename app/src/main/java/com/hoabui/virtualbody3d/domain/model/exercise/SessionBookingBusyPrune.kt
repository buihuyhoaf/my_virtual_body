package com.hoabui.virtualbody3d.domain.model.exercise

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

/**
 * Prunes selected slot starts when they overlap [busy]; returns null if unchanged.
 */
fun prunedSlotStartsAfterBusyChange(
    selectedDateMillis: Long,
    selectedSlotStarts: Set<LocalTime>,
    busy: List<InstantInterval>,
    zoneId: ZoneId,
): Set<LocalTime>? {
    if (selectedSlotStarts.isEmpty()) return null
    val date = Instant.ofEpochMilli(selectedDateMillis).atZone(zoneId).toLocalDate()
    val pruned = pruneSelectionAgainstBusy(
        selectedSlotStarts,
        busy,
        date,
        zoneId,
    )
    return if (pruned == selectedSlotStarts) null else pruned
}
