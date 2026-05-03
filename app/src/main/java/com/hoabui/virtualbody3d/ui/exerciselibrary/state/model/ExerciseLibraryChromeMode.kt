package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode

@Immutable
sealed interface ExerciseLibraryChromeMode {
    data object Idle : ExerciseLibraryChromeMode

    data class EditingScheduleRow(
        val scheduleRowId: Long,
        val baselineCart: LibraryCartState,
        val isIsolatedScheduleRowSelectionEdit: Boolean,
        val measurementMode: ExerciseMeasurementMode,
    ) : ExerciseLibraryChromeMode
}
