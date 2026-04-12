package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryWeeklyHeatmapState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.WeeklyHeatmapDayUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ExerciseLibraryMonthlySummaryCard(
    state: LibraryWeeklyHeatmapState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val body = token.bodyAnalysis
    val navCd = stringResource(R.string.exercise_library_weekly_heatmap_nav_cd)
    val loadingCd = stringResource(R.string.exercise_library_weekly_heatmap_loading_cd)
    val a11yLabel = when (state) {
        LibraryWeeklyHeatmapState.Loading -> loadingCd
        else -> navCd
    }
    val cardBorder = BorderStroke(
        width = token.borderWidth.hairline,
        color = token.colors.exerciseLibraryHeatmapCardBorder,
    )
    GCard(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = a11yLabel },
        onClick = onClick,
        containerColor = token.colors.surfaceSubtle,
        border = cardBorder,
        elevation = token.elevation.level0,
        treatment = GSurfaceTreatment.Flat,
    ) {
        when (state) {
            LibraryWeeklyHeatmapState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = body.exerciseLibraryHeatmapCardHorizontalPadding,
                            vertical = body.exerciseLibraryHeatmapCardVerticalPadding,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(body.exerciseLibraryBookingSectionIconSize),
                        strokeWidth = token.borderWidth.thin,
                        color = token.colors.primary,
                    )
                }
            }
            is LibraryWeeklyHeatmapState.Loaded -> {
                WeeklyHeatmapRow(
                    days = state.days,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = body.exerciseLibraryHeatmapCardHorizontalPadding,
                            vertical = body.exerciseLibraryHeatmapCardVerticalPadding,
                        ),
                )
            }
            is LibraryWeeklyHeatmapState.Error -> {
                val errFallback = stringResource(R.string.exercise_library_weekly_heatmap_error)
                GText(
                    text = state.message.ifBlank { errFallback },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = body.exerciseLibraryHeatmapCardHorizontalPadding,
                            vertical = body.exerciseLibraryHeatmapCardVerticalPadding,
                        ),
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun WeeklyHeatmapRow(
    days: ImmutableList<WeeklyHeatmapDayUiModel>,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val body = token.bodyAnalysis
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(body.exerciseLibraryHeatmapDayItemSpacing),
    ) {
        days.forEach { day ->
            HeatmapDayItem(
                day = day,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HeatmapDayItem(
    day: WeeklyHeatmapDayUiModel,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val body = token.bodyAnalysis
    val cornerRadius = body.exerciseLibraryHeatmapDayItemCornerRadius
    val shape = RoundedCornerShape(cornerRadius)
    val primary = token.colors.primary
    val backgroundColor = when (day.densityLevel) {
        0 -> token.colors.surfaceSubtle
        1 -> primary.copy(alpha = PrimitiveAlphaTokens.LOW)
        2 -> primary.copy(alpha = PrimitiveAlphaTokens.MEDIUM)
        else -> primary
    }
    val todayBorderModifier = if (day.isToday) {
        Modifier.border(
            width = token.borderWidth.thin,
            color = primary,
            shape = shape,
        )
    } else {
        Modifier
    }
    val textColor = if (day.densityLevel >= 3) token.colors.onPrimary else token.colors.textPrimary

    Column(
        modifier = modifier
            .height(body.exerciseLibraryHeatmapDayItemHeight)
            .clip(shape)
            .background(backgroundColor)
            .then(todayBorderModifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GText(
            text = day.dayLabel,
            style = token.typography.labelSmall,
            color = textColor,
            maxLines = 1,
        )
        GText(
            text = day.dayOfMonth.toString(),
            style = token.typography.bodySmall,
            color = textColor,
            maxLines = 1,
        )
    }
}

@Preview(name = "Weekly heatmap card — Light", showBackground = true)
@Composable
private fun ExerciseLibraryWeeklyHeatmapCardLoadedLightPreview() {
    GymTheme(darkTheme = false) {
        ExerciseLibraryMonthlySummaryCard(
            state = LibraryWeeklyHeatmapState.Loaded(
                days = previewHeatmapDays(),
            ),
            onClick = {},
            modifier = Modifier.padding(GymTheme.token.spacing.md),
        )
    }
}

@Preview(name = "Weekly heatmap card — Dark", showBackground = true)
@Composable
private fun ExerciseLibraryWeeklyHeatmapCardLoadedDarkPreview() {
    GymTheme(darkTheme = true) {
        ExerciseLibraryMonthlySummaryCard(
            state = LibraryWeeklyHeatmapState.Loaded(
                days = previewHeatmapDays(),
            ),
            onClick = {},
            modifier = Modifier.padding(GymTheme.token.spacing.md),
        )
    }
}

@Preview(name = "Weekly heatmap card — Loading", showBackground = true)
@Composable
private fun ExerciseLibraryWeeklyHeatmapCardLoadingPreview() {
    GymTheme(darkTheme = false) {
        ExerciseLibraryMonthlySummaryCard(
            state = LibraryWeeklyHeatmapState.Loading,
            onClick = {},
            modifier = Modifier.padding(GymTheme.token.spacing.md),
        )
    }
}

private fun previewHeatmapDays(): ImmutableList<WeeklyHeatmapDayUiModel> = persistentListOf(
    WeeklyHeatmapDayUiModel(dayLabel = "T2", dayOfMonth = 7, densityLevel = 0, isToday = false),
    WeeklyHeatmapDayUiModel(dayLabel = "T3", dayOfMonth = 8, densityLevel = 1, isToday = false),
    WeeklyHeatmapDayUiModel(dayLabel = "T4", dayOfMonth = 9, densityLevel = 2, isToday = false),
    WeeklyHeatmapDayUiModel(dayLabel = "T5", dayOfMonth = 10, densityLevel = 3, isToday = false),
    WeeklyHeatmapDayUiModel(dayLabel = "T6", dayOfMonth = 11, densityLevel = 0, isToday = false),
    WeeklyHeatmapDayUiModel(dayLabel = "T7", dayOfMonth = 12, densityLevel = 1, isToday = true),
    WeeklyHeatmapDayUiModel(dayLabel = "CN", dayOfMonth = 13, densityLevel = 0, isToday = false),
)

