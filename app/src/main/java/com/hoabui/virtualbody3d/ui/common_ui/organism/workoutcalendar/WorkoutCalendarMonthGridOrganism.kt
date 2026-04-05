package com.hoabui.virtualbody3d.ui.common_ui.organism.workoutcalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarDayCellStatus
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import com.hoabui.virtualbody3d.ui.workoutcalendar.model.WorkoutCalendarDayCellUiModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WorkoutCalendarMonthGridOrganism(
    yearMonth: YearMonth,
    cells: List<WorkoutCalendarDayCellUiModel>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val cal = token.workoutCalendar
    GSurface(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top),
        shape = RoundedCornerShape(token.radius.lg),
        color = token.colors.surface,
        shadowElevation = cal.sectionSurfaceElevation,
        treatment = GSurfaceTreatment.Flat,
    ) {
        val locale = LocalConfiguration.current.locales.get(0) ?: Locale.getDefault()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = cal.sectionSurfacePaddingHorizontal,
                    vertical = cal.sectionSurfacePaddingVertical,
                ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.workout_calendar_prev_month_cd),
                        tint = token.colors.textPrimary,
                    )
                }
                Text(
                    text = monthTitle(yearMonth, locale),
                    style = workoutCalendarMonthTitleStyle(token),
                    color = token.colors.textPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
                IconButton(onClick = onNextMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.workout_calendar_next_month_cd),
                        tint = token.colors.textPrimary,
                    )
                }
            }
            Spacer(modifier = Modifier.height(token.spacing.xs))
            weekDayLettersRow()
            Spacer(modifier = Modifier.height(cal.sectionTitleToContentGap))
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val gap = cal.monthGridCellGap
                val cellSide = ((maxWidth - gap * 6) / 7).coerceAtLeast(cal.monthGridCellMinSize)
                val rows = cells.chunked(7)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(gap),
                ) {
                    rows.forEach { rowCells ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(gap),
                        ) {
                            rowCells.forEach { cell ->
                                MonthDayCell(
                                    cell = cell,
                                    cellSide = cellSide,
                                    onDayClick = onDayClick,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun weekDayLettersRow() {
    val token = GymTheme.token
    val letters = listOf(
        R.string.workout_calendar_weekday_mo,
        R.string.workout_calendar_weekday_tu,
        R.string.workout_calendar_weekday_we,
        R.string.workout_calendar_weekday_th,
        R.string.workout_calendar_weekday_fr,
        R.string.workout_calendar_weekday_sa,
        R.string.workout_calendar_weekday_su,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        letters.forEach { res ->
            Text(
                text = stringResource(res),
                style = token.typography.labelSmall,
                color = token.colors.textMuted,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun MonthDayCell(
    cell: WorkoutCalendarDayCellUiModel,
    cellSide: Dp,
    onDayClick: (LocalDate) -> Unit,
) {
    val token = GymTheme.token
    val cal = token.workoutCalendar
    val date = cell.date
    if (date == null) {
        Spacer(modifier = Modifier.size(cellSide))
        return
    }
    val surfaces = workoutCalendarDayCellSurfaces(cell = cell, token = token)
    val shape = RoundedCornerShape(cal.dayCellCornerRadius)
    val borderMod = surfaces.border?.let { b ->
        Modifier.border(b, shape)
    } ?: Modifier
    Column(
        modifier = Modifier
            .size(cellSide)
            .clip(shape)
            .then(borderMod)
            .background(surfaces.background)
            .clickable { onDayClick(date) }
            .padding(token.spacing.xxs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "${date.dayOfMonth}",
            style = token.typography.bodyMedium,
            color = if (cell.inCurrentMonth) token.colors.textPrimary else token.colors.textMuted,
            textAlign = TextAlign.Center,
        )
        StatusDots(cellStatus = cell.cellStatus)
    }
}

@Composable
private fun StatusDots(cellStatus: WorkoutCalendarDayCellStatus) {
    val token = GymTheme.token
    when (cellStatus) {
        WorkoutCalendarDayCellStatus.Empty -> Spacer(modifier = Modifier.height(token.spacing.xxs))
        WorkoutCalendarDayCellStatus.Scheduled -> DotRow(
            token.colors.primary,
            token.colors.backgroundTransparent,
            token.colors.backgroundTransparent,
        )
        WorkoutCalendarDayCellStatus.Completed -> DotRow(
            token.colors.success,
            token.colors.backgroundTransparent,
            token.colors.backgroundTransparent,
        )
        WorkoutCalendarDayCellStatus.Missed -> DotRow(
            token.colors.error,
            token.colors.backgroundTransparent,
            token.colors.backgroundTransparent,
        )
        WorkoutCalendarDayCellStatus.Mixed -> DotRow(
            token.colors.success,
            token.colors.primary,
            token.colors.error,
        )
    }
}

@Composable
private fun DotRow(a: androidx.compose.ui.graphics.Color, b: androidx.compose.ui.graphics.Color, c: androidx.compose.ui.graphics.Color) {
    val token = GymTheme.token
    Row(
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xxxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(token.spacing.xxxs)
                .clip(CircleShape)
                .background(a),
        )
        Box(
            modifier = Modifier
                .size(token.spacing.xxxs)
                .clip(CircleShape)
                .background(b),
        )
        Box(
            modifier = Modifier
                .size(token.spacing.xxxs)
                .clip(CircleShape)
                .background(c),
        )
    }
}

private fun monthTitle(ym: YearMonth, locale: Locale): String {
    val month = ym.month.getDisplayName(TextStyle.FULL, locale)
    return "$month ${ym.year}"
}

@Preview(showBackground = true)
@Composable
private fun WorkoutCalendarMonthGridOrganismPreviewLight() {
    GymTheme(darkTheme = false) {
        Surface(color = GymTheme.token.colors.surface) {
            val cells = sampleCells()
            WorkoutCalendarMonthGridOrganism(
                yearMonth = YearMonth.of(2024, 4),
                cells = cells,
                onPreviousMonth = {},
                onNextMonth = {},
                onDayClick = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutCalendarMonthGridOrganismPreviewDark() {
    GymTheme(darkTheme = true) {
        Surface(color = GymTheme.token.colors.surface) {
            WorkoutCalendarMonthGridOrganism(
                yearMonth = YearMonth.of(2024, 4),
                cells = sampleCells(),
                onPreviousMonth = {},
                onNextMonth = {},
                onDayClick = {},
            )
        }
    }
}

private fun sampleCells(): List<WorkoutCalendarDayCellUiModel> {
    val ym = YearMonth.of(2024, 4)
    val d1 = ym.atDay(5)
    val d2 = ym.atDay(6)
    val list = mutableListOf<WorkoutCalendarDayCellUiModel>()
    val pad = (ym.atDay(1).dayOfWeek.value - 1) % 7
    repeat(pad) {
        list.add(
            WorkoutCalendarDayCellUiModel(
                date = null,
                inCurrentMonth = false,
                cellStatus = WorkoutCalendarDayCellStatus.Empty,
                isSelected = false,
                isToday = false,
            ),
        )
    }
    for (i in 1..30) {
        val d = ym.atDay(i)
        val st = when (i) {
            5 -> WorkoutCalendarDayCellStatus.Completed
            6 -> WorkoutCalendarDayCellStatus.Mixed
            7 -> WorkoutCalendarDayCellStatus.Scheduled
            else -> WorkoutCalendarDayCellStatus.Empty
        }
        list.add(
            WorkoutCalendarDayCellUiModel(
                date = d,
                inCurrentMonth = true,
                cellStatus = st,
                isSelected = d == d1,
                isToday = d == d2,
            ),
        )
    }
    while (list.size % 7 != 0) {
        list.add(
            WorkoutCalendarDayCellUiModel(
                date = null,
                inCurrentMonth = false,
                cellStatus = WorkoutCalendarDayCellStatus.Empty,
                isSelected = false,
                isToday = false,
            ),
        )
    }
    return list
}
