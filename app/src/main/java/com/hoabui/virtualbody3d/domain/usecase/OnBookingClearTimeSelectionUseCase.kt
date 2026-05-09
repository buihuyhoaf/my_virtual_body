package com.hoabui.virtualbody3d.domain.usecase

import javax.inject.Inject

class OnBookingClearTimeSelectionUseCase @Inject constructor(
    private val exerciseLibraryBookingManager: com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryBookingManager
) {
    operator fun invoke() {
        exerciseLibraryBookingManager.onBookingClearTimeSelection()
    }
}
