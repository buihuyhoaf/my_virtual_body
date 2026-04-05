package com.hoabui.virtualbody3d.ui.common_ui.organism.workoutcalendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken
import com.hoabui.virtualbody3d.ui.workoutcalendar.model.WorkoutCalendarDayCellUiModel

/**
 * Shared visual language for month grid day cells.
 */
internal data class WorkoutCalendarDayCellSurfaces(
    val background: Color,
    val border: BorderStroke?,
)

internal fun workoutCalendarDayCellSurfaces(
    cell: WorkoutCalendarDayCellUiModel,
    token: GymToken,
): WorkoutCalendarDayCellSurfaces {
    val cal = token.workoutCalendar
    val colors = token.colors
    return when {
        cell.isSelected -> WorkoutCalendarDayCellSurfaces(
            background = colors.primarySoft,
            border = BorderStroke(cal.daySelectedBorderWidth, colors.primary),
        )
        cell.isToday -> WorkoutCalendarDayCellSurfaces(
            background = colors.surfaceSubtle,
            border = BorderStroke(cal.dayTodayBorderWidth, colors.borderSubtle),
        )
        else -> WorkoutCalendarDayCellSurfaces(
            background = colors.backgroundTransparent,
            border = null,
        )
    }
}
