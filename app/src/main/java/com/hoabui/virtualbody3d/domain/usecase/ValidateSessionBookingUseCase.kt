package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryCartDraft
import com.hoabui.virtualbody3d.domain.model.exercise.isContiguousThirtyMinuteChain
import com.hoabui.virtualbody3d.domain.model.exercise.isValidForSessionConfirm
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
        zoneId: ZoneId,
        isConfirming: Boolean,
    ): Boolean {
        if (isConfirming) return false
        if (!cart.isValidForSessionConfirm(exerciseMeasurementById)) return false
        if (selectedSlotStarts.size < 2) return false
        if (selectedLocationId.isBlank()) return false
        val ordered = selectedSlotStarts.sorted()
        return isContiguousThirtyMinuteChain(ordered)
    }
}
