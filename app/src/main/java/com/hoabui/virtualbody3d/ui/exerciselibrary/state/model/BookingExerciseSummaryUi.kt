package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.ExerciseLibraryCardImage

@Immutable
data class BookingExerciseSummaryUi(
    val id: String,
    val title: String,
    val image: ExerciseLibraryCardImage,
    val orderIndex: Int,
    /** Preformatted at sheet open (sets×reps or duration); empty when draft not displayable. */
    val parametersSummary: String,
)
