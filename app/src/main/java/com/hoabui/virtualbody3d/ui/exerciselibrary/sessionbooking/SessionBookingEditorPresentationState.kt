package com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState

@Immutable
data class SessionBookingEditorPresentationState(
    val libraryUi: ExerciseLibraryUiState,
    val sessionBookingInput: SessionBookingInput?,
    val sessionBookingUiModel: SessionBookingUiModel?,
    val sessionBookingWorkflowPhase: SessionBookingWorkflowPhase,
)
