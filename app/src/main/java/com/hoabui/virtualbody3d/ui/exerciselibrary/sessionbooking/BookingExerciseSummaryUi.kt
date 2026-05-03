package com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.ExerciseLibraryCardImage

@Immutable
data class BookingExerciseSummaryUi(
    val id: String,
    val title: String,
    val image: ExerciseLibraryCardImage,
    val orderIndex: Int,
    val parametersSummary: String,
)
