package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import com.hoabui.virtualbody3d.domain.usecase.CommitLibrarySessionBookingResult
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.AddExerciseSuccessSummary
import javax.inject.Inject

/**
 * Maps domain booking commit success to chrome [AddExerciseSuccessSummary] (reducer stays free of mapping logic).
 */
class CommitLibrarySessionBookingSuccessUiMapper @Inject constructor() {
    fun toAddExerciseSuccessSummary(result: CommitLibrarySessionBookingResult.Success): AddExerciseSuccessSummary =
        AddExerciseSuccessSummary(
            sessionStartInstant = result.session.startInstant,
            sessionEndInstant = result.session.endInstant,
            scheduledDateMillis = result.scheduledDateMillis,
            exerciseCount = result.scheduledCount,
            primaryExerciseTitle = result.primaryExerciseTitle,
            locationDisplayName = result.locationDisplayName,
        )
}
