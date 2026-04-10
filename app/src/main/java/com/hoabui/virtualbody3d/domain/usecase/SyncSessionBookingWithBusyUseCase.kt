package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.InstantInterval
import com.hoabui.virtualbody3d.domain.model.exercise.prunedSlotStartsAfterBusyChange
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

/**
 * Returns pruned slot selection when [busy] invalidates the current contiguous selection; null if unchanged.
 */
class SyncSessionBookingWithBusyUseCase @Inject constructor() {

    operator fun invoke(
        selectedDateMillis: Long,
        selectedSlotStarts: Set<LocalTime>,
        busy: List<InstantInterval>,
        zoneId: ZoneId,
    ): Set<LocalTime>? =
        prunedSlotStartsAfterBusyChange(
            selectedDateMillis = selectedDateMillis,
            selectedSlotStarts = selectedSlotStarts,
            busy = busy,
            zoneId = zoneId,
        )
}
