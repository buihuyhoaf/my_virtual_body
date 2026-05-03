package com.hoabui.virtualbody3d.domain.model.exercise.dashboard

data class ExerciseLibraryWeekStripDay(
    val epochDay: Long,
    /** 0 … 3 for heatmap shading. */
    val densityLevel: Int,
)
