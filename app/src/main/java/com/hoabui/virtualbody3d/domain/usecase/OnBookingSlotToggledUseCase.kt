package com.hoabui.virtualbody3d.domain.usecase

import javax.inject.Inject

class OnBookingSlotToggledUseCase @Inject constructor(
    private val exerciseLibraryBookingManager: com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryBookingManager
) {
    operator fun invoke(slotStart: java.time.LocalTime) {
        exerciseLibraryBookingManager.onBookingSlotToggled(slotStart)
    }
}
