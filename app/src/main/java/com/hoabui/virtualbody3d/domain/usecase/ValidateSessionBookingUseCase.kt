package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.InstantInterval
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryCartDraft
import com.hoabui.virtualbody3d.domain.model.exercise.isValidForSessionConfirm
import com.hoabui.virtualbody3d.domain.model.exercise.isContiguousThirtyMinuteChain
import com.hoabui.virtualbody3d.domain.model.exercise.isIntervalFreeForBooking
import com.hoabui.virtualbody3d.domain.model.exercise.proposedVariableSessionInterval
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

/**
 * Single authority for whether the booking confirm control should be enabled (pre-commit checks).
 */
class ValidateSessionBookingUseCase @Inject constructor() {

    fun canEnableConfirm(
        selectedSlotStarts: Set<LocalTime>,
        selectedLocationId: String,
        selectedDateMillis: Long,
        cart: LibraryCartDraft,
        exerciseMeasurementById: Map<String, ExerciseMeasurementMode>,
        busyIntervals: List<InstantInterval>,
        zoneId: ZoneId,
        isConfirming: Boolean,
    ): Boolean {
        if (isConfirming) return false
        if (!cart.isValidForSessionConfirm(exerciseMeasurementById)) return false
        if (selectedSlotStarts.isEmpty()) return false
        if (selectedLocationId.isBlank()) return false
        val date = Instant.ofEpochMilli(selectedDateMillis).atZone(zoneId).toLocalDate()
        val ordered = selectedSlotStarts.sorted()
        if (!isContiguousThirtyMinuteChain(ordered)) return false
        val minS = ordered.first()
        val maxS = ordered.last()
        val proposed = proposedVariableSessionInterval(date, minS, maxS, zoneId)
        return isIntervalFreeForBooking(proposed, busyIntervals)
    }
}
