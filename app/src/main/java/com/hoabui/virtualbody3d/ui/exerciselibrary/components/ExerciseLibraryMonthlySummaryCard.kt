package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.icon.GIcon
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryMonthlySummaryState
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ExerciseLibraryMonthlySummaryCard(
    state: LibraryMonthlySummaryState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val body = token.bodyAnalysis
    val navCd = stringResource(R.string.exercise_library_monthly_summary_nav_cd)
    val loadingCd = stringResource(R.string.exercise_library_monthly_summary_loading_cd)
    val workoutLabel = stringResource(R.string.exercise_library_monthly_summary_workout_label)
    val restLabel = stringResource(R.string.exercise_library_monthly_summary_rest_label)
    val sep = stringResource(R.string.exercise_library_monthly_summary_separator)
    val errFallback = stringResource(R.string.exercise_library_monthly_summary_error)
    val monthFormatter = remember {
        DateTimeFormatter.ofPattern("LLLL yyyy", Locale.forLanguageTag("vi-VN"))
    }
    val a11yLabel = when (state) {
        LibraryMonthlySummaryState.Loading -> loadingCd
        else -> navCd
    }
    val border = BorderStroke(
        width = token.borderWidth.hairline,
        color = token.colors.exerciseLibraryMonthlySummaryCardBorder,
    )
    GCard(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = a11yLabel },
        onClick = onClick,
        containerColor = token.colors.surfaceSubtle,
        border = border,
        elevation = token.elevation.level0,
        treatment = GSurfaceTreatment.Flat,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = body.exerciseLibraryMonthlySummaryCardHorizontalPadding,
                    vertical = body.exerciseLibraryMonthlySummaryCardVerticalPadding,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            when (state) {
                LibraryMonthlySummaryState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(body.exerciseLibraryBookingSectionIconSize),
                        strokeWidth = token.borderWidth.thin,
                        color = token.colors.primary,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                is LibraryMonthlySummaryState.Loaded -> {
                    val monthText = remember(state.yearMonth, monthFormatter) {
                        monthFormatter.format(state.yearMonth.atDay(1))
                    }
                    val bodyStyle = token.typography.bodyMedium
                    FlowRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(token.spacing.xxs),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        GText(
                            text = "$monthText: ",
                            style = bodyStyle,
                            color = token.colors.textSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        GText(
                            text = state.workoutDayCount.toString(),
                            style = bodyStyle,
                            color = token.colors.exerciseLibraryMonthlySummaryWorkoutCount,
                            maxLines = 1,
                        )
                        GText(
                            text = " $workoutLabel$sep",
                            style = bodyStyle,
                            color = token.colors.textSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        GText(
                            text = state.restDayCount.toString(),
                            style = bodyStyle,
                            color = token.colors.exerciseLibraryMonthlySummaryRestCount,
                            maxLines = 1,
                        )
                        GText(
                            text = " $restLabel",
                            style = bodyStyle,
                            color = token.colors.textSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                is LibraryMonthlySummaryState.Error -> {
                    GText(
                        text = state.message.ifBlank { errFallback },
                        modifier = Modifier.weight(1f),
                        style = token.typography.bodyMedium,
                        color = token.colors.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            GIcon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.padding(start = token.spacing.xs),
                tint = token.colors.textSecondary,
            )
        }
    }
}

@Preview(name = "Monthly summary card — Light", showBackground = true)
@Composable
private fun ExerciseLibraryMonthlySummaryCardLoadedLightPreview() {
    GymTheme(darkTheme = false) {
        ExerciseLibraryMonthlySummaryCard(
            state = LibraryMonthlySummaryState.Loaded(
                yearMonth = YearMonth.of(2026, 4),
                workoutDayCount = 5,
                restDayCount = 25,
            ),
            onClick = {},
            modifier = Modifier.padding(GymTheme.token.spacing.md),
        )
    }
}

@Preview(name = "Monthly summary card — Dark", showBackground = true)
@Composable
private fun ExerciseLibraryMonthlySummaryCardLoadedDarkPreview() {
    GymTheme(darkTheme = true) {
        ExerciseLibraryMonthlySummaryCard(
            state = LibraryMonthlySummaryState.Loaded(
                yearMonth = YearMonth.of(2026, 4),
                workoutDayCount = 5,
                restDayCount = 25,
            ),
            onClick = {},
            modifier = Modifier.padding(GymTheme.token.spacing.md),
        )
    }
}
