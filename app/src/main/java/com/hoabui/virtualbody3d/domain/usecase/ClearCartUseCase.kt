package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryBookingManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val exerciseLibraryCartManager: ExerciseLibraryCartManager,
    private val bookingManager: ExerciseLibraryBookingManager
) {
    operator fun invoke() {
        exerciseLibraryCartManager.clearCartOnly()
        exerciseLibraryCartManager.setChromeIdle()
        bookingManager.resetBookingAfterCartClear()
    }
}
