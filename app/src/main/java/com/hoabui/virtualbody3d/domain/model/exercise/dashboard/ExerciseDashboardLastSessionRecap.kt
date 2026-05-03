package com.hoabui.virtualbody3d.domain.model.exercise.dashboard

import java.time.LocalDate

data class ExerciseDashboardLastSessionRecap(
    val anchorDate: LocalDate,
    /** Display line e.g. "Bench · Squat". */
    val exerciseTitlesJoined: String,
    val durationMinutes: Int,
    val totalKcalRounded: Int,
)
