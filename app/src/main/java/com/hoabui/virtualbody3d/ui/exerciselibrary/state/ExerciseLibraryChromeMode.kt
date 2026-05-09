package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode

@Immutable
sealed interface ExerciseLibraryChromeMode {
    data object Idle : ExerciseLibraryChromeMode

    data class EditingScheduleRow(
        val scheduleRowId: Long,
        val measurementModeFallback: ExerciseMeasurementMode,
    ) : ExerciseLibraryChromeMode
}
