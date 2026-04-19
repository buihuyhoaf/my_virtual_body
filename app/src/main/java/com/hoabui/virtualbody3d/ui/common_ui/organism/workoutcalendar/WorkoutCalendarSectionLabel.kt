package com.hoabui.virtualbody3d.ui.common_ui.organism.workoutcalendar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Unified section title line (e.g. "Tuần này", formatted date header): same typography and color as specified.
 */
@Composable
fun WorkoutCalendarSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    maxLines: Int = 2,
) {
    val token = GymTheme.token
    val cal = token.workoutCalendar
    GText(
        text = text,
        style = workoutCalendarUnifiedSectionTitleStyle(token),
        color = color ?: token.colors.textSecondary,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        softWrap = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = cal.sectionTitleSafePaddingVertical),
    )
}
