package com.hoabui.virtualbody3d.ui.common_ui.organism.workoutcalendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLineUiModel
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun WorkoutCalendarDayExerciseListOrganism(
    selectedDate: LocalDate,
    lines: List<WorkoutCalendarExerciseLineUiModel>,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val cal = token.workoutCalendar
    val locale =
        LocalConfiguration.current.locales.get(0) ?: Locale.getDefault()
    val headerFormat =
        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = cal.exerciseListHeaderToListGap),
        ) {
            WorkoutCalendarSectionLabel(text = selectedDate.format(headerFormat))
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(token.spacing.none),
            verticalArrangement = Arrangement.spacedBy(cal.exerciseItemListGap),
        ) {
            if (lines.isEmpty()) {
                item(key = "empty") {
                    GText(
                        text = stringResource(R.string.workout_calendar_empty_day),
                        style = token.typography.bodyMedium,
                        color = token.colors.textMuted,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                items(lines, key = { it.rowId }) { line ->
                    GCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = null,
                        shape = RoundedCornerShape(token.radius.sm),
                        containerColor = token.colors.surface,
                        border = BorderStroke(token.borderWidth.hairline, token.colors.borderSubtle),
                        treatment = GSurfaceTreatment.Flat,
                        elevation = token.elevation.level0,
                        contentModifier = Modifier.padding(cal.exerciseItemInnerPadding),
                    ) {
                        GText(
                            text = line.title,
                            style = workoutCalendarExerciseNameStyle(token),
                            color = token.colors.textPrimary,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        GText(
                            text = line.metricsLabel,
                            style = workoutCalendarSupportingBodyStyle(token),
                            color = token.colors.textSecondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = cal.exerciseRowTitleToMetricsGap),
                        )
                        GText(
                            text = line.statusLabel,
                            style = workoutCalendarSupportingLabelStyle(token),
                            color = token.colors.textMuted,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = cal.exerciseRowMetricsToStatusGap),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayListPreviewLight() {
    GymTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = GymTheme.token.colors.surface,
        ) {
            WorkoutCalendarDayExerciseListOrganism(
                selectedDate = LocalDate.of(2024, 4, 10),
                lines = listOf(
                    WorkoutCalendarExerciseLineUiModel(
                        rowId = 1L,
                        title = "Squat",
                        metricsLabel = "3 × 10",
                        statusLabel = "Scheduled",
                    ),
                    WorkoutCalendarExerciseLineUiModel(
                        rowId = 2L,
                        title = "Romanian deadlift",
                        metricsLabel = "4 × 8",
                        statusLabel = "Completed",
                    ),
                ),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayListPreviewDark() {
    GymTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = GymTheme.token.colors.surface,
        ) {
            WorkoutCalendarDayExerciseListOrganism(
                selectedDate = LocalDate.of(2024, 4, 10),
                lines = emptyList(),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
