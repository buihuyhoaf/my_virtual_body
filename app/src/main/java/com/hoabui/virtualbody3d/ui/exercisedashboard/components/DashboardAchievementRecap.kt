package com.hoabui.virtualbody3d.ui.exercisedashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.DashboardAchievementUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DashboardAchievementRecap(
    achievement: DashboardAchievementUiModel?,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val dash = token.dashboardExercise
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = token.spacing.md,
                vertical = dash.achievementSectionVerticalPadding,
            ),
    ) {
        if (achievement == null) {
            GText(
                text = stringResource(R.string.exercise_dashboard_no_session),
                style = token.typography.bodyMedium,
                color = token.colors.textMuted,
                modifier = Modifier.fillMaxWidth(),
            )
            return@Column
        }
        val anchor = LocalDate.ofEpochDay(achievement.anchorEpochDay)
        val today = LocalDate.now()
        val dateLabelRes = when (anchor) {
            today -> R.string.exercise_dashboard_date_today
            today.minusDays(1) -> R.string.exercise_dashboard_date_yesterday
            else -> null
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            GText(
                text = stringResource(R.string.exercise_dashboard_last_session),
                style = token.typography.labelSmall,
                color = token.colors.textSecondary,
            )
            val dateText = dateLabelRes?.let { stringResource(it) } ?: anchor.format(dateFormatter)
            GText(
                text = dateText,
                style = token.typography.labelSmall,
                color = token.colors.textSecondary,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(modifier = Modifier.height(token.spacing.sm))
        if (achievement.exerciseTitlesLine.isNotBlank()) {
            GText(
                text = achievement.exerciseTitlesLine,
                style = token.typography.titleMedium,
                color = token.colors.textPrimary,
                maxLines = 2,
            )
            Spacer(modifier = Modifier.height(token.spacing.sm))
        }
        HorizontalDivider(color = token.colors.borderSubtle, thickness = dash.achievementSectionDividerThickness)
        Spacer(modifier = Modifier.height(token.spacing.sm))
        DashboardMetricRow(
            kcalDisplay = achievement.totalKcal,
            durationMinutesDisplay = achievement.durationMinutes,
        )
    }
}
