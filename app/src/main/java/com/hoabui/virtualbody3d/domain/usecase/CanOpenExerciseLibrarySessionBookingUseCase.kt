package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryCartDraft
import com.hoabui.virtualbody3d.domain.model.exercise.isValidForSessionConfirm
import javax.inject.Inject

/**
 * Whether the library cart is non-empty and valid for opening the session booking sheet.
 */
class CanOpenExerciseLibrarySessionBookingUseCase @Inject constructor() {
    operator fun invoke(
        cart: LibraryCartDraft,
        exerciseMeasurementById: Map<String, ExerciseMeasurementMode>,
    ): Boolean = cart.itemDrafts.isNotEmpty() && cart.isValidForSessionConfirm(exerciseMeasurementById)
}
