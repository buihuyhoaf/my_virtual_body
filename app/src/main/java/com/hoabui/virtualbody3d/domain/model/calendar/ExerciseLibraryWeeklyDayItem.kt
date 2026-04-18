package com.hoabui.virtualbody3d.domain.model.calendar

import java.time.LocalDate

/**
 * Session activity for a single day in the current week (Mon–Sun), used by the
 * Exercise Library weekly heatmap header card.
 *
 * [sessionCount] is the number of workout schedules on that [date].
 * [isToday] marks the day matching today's local date.
 */
data class ExerciseLibraryWeeklyDayItem(
    val date: LocalDate,
    val sessionCount: Int,
    val isToday: Boolean,
)
