package com.hoabui.virtualbody3d.domain.usecase

import javax.inject.Inject

class OnBookingDateSelectedUseCase @Inject constructor(
    private val exerciseLibraryBookingManager: com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryBookingManager
) {
    operator fun invoke(dateMillis: Long) {
        exerciseLibraryBookingManager.onBookingDateSelected(dateMillis)
    }
}
