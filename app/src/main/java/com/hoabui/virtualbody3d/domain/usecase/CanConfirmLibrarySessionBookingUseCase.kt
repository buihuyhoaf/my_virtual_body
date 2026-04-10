package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryCartDraft
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

/**
 * Facade for [ValidateSessionBookingUseCase.canEnableConfirm] to keep call sites single-entry.
 */
class CanConfirmLibrarySessionBookingUseCase @Inject constructor(
    private val validateSessionBookingUseCase: ValidateSessionBookingUseCase,
) {
    operator fun invoke(
        selectedSlotStarts: Set<LocalTime>,
        selectedLocationId: String,
        selectedDateMillis: Long,
        cart: LibraryCartDraft,
        exerciseMeasurementById: Map<String, ExerciseMeasurementMode>,
        zoneId: ZoneId,
        isConfirming: Boolean,
    ): Boolean = validateSessionBookingUseCase.canEnableConfirm(
        selectedSlotStarts = selectedSlotStarts,
        selectedLocationId = selectedLocationId,
        selectedDateMillis = selectedDateMillis,
        cart = cart,
        exerciseMeasurementById = exerciseMeasurementById,
        zoneId = zoneId,
        isConfirming = isConfirming,
    )
}
