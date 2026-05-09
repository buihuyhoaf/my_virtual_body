package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryBookingManager
import javax.inject.Inject

class OnBookingLocationSelectedUseCase @Inject constructor(
    private val exerciseLibraryBookingManager: ExerciseLibraryBookingManager
) {
    operator fun invoke(locationId: String) {
        exerciseLibraryBookingManager.onBookingLocationSelected(locationId)
    }
}
