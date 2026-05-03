package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.SelectionBarCartBaseline

@Immutable
sealed interface ExerciseLibraryChromeMode {
    data object Idle : ExerciseLibraryChromeMode

    data class EditingScheduleRow(
        val scheduleRowId: Long,
        val baselineCart: SelectionBarCartBaseline,
        val isIsolatedScheduleRowSelectionEdit: Boolean,
        val measurementMode: ExerciseMeasurementMode,
    ) : ExerciseLibraryChromeMode
}
