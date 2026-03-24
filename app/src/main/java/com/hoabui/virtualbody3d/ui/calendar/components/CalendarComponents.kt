package com.hoabui.virtualbody3d.ui.calendar.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.calendar.extensions.estimatedKcal
import com.hoabui.virtualbody3d.ui.calendar.extensions.toCalendarOffset
import com.hoabui.virtualbody3d.ui.calendar.extensions.toIcon
import com.hoabui.virtualbody3d.ui.calendar.state.DailyItem
import com.hoabui.virtualbody3d.ui.calendar.state.DailyItemType
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun CalendarMonthSection(
    month: YearMonth,
    cells: List<LocalDate?>,
    selectedDate: LocalDate?,
    today: LocalDate,
    dailyItemsByDate: Map<LocalDate, List<DailyItem>>,
    onDateSelected: (LocalDate) -> Unit,
) {
    val token = GymTheme.token
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.md),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            GText(
                text = month.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase(Locale.ENGLISH),
                style = token.typography.labelLarge,
                color = token.calendar.monthTextColor,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(token.calendar.monthDividerColor),
            )
        }

        cells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            ) {
                week.forEach { date ->
                    if (date == null) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                        )
                    } else {
                        CalendarDayCell(
                            modifier = Modifier.weight(1f),
                            date = date,
                            isToday = date == today,
                            items = dailyItemsByDate[date].orEmpty(),
                            selected = date == selectedDate,
                            onClick = { onDateSelected(date) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun CalendarDayCell(
    modifier: Modifier = Modifier,
    date: LocalDate,
    isToday: Boolean,
    items: List<DailyItem>,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val token = GymTheme.token
    val thumbnail = items.firstOrNull()
    val badgeCount = (items.size - 1).coerceAtLeast(0)
    val placeholderResId = R.drawable.ic_launcher_foreground
    val scale by animateFloatAsState(targetValue = if (selected) 1.05f else 1f, label = "day_scale")
    val cellShape = RoundedCornerShape(token.radius.md)
    val badgeText = if (badgeCount > 9) "9+" else "+$badgeCount"

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(cellShape)
            .background(if (selected) token.calendar.selectedDayBackground else token.colors.surface)
            .border(
                width = when {
                    selected -> 1.5.dp
                    isToday -> 1.dp
                    else -> 0.dp
                },
                color = when {
                    selected -> token.calendar.selectedBorderColor
                    isToday -> token.calendar.todayBorderColor
                    else -> token.colors.backgroundTransparent
                },
                shape = cellShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(token.spacing.xxs)
                .clip(RoundedCornerShape(token.radius.sm)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = thumbnail?.thumbnailResId ?: placeholderResId),
                contentDescription = "Day thumbnail ${date.dayOfMonth}",
                contentScale = ContentScale.Crop,
                alpha = if (items.isEmpty()) 0.45f else 1f,
                modifier = Modifier.fillMaxSize(),
            )
        }
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .background(color = token.colors.primary, shape = RoundedCornerShape(token.radius.md)),
            ) {
                GText(
                    text = badgeText,
                    style = token.typography.labelSmall,
                    color = token.colors.surface,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
    }
}

@Composable
internal fun CalendarDetailPanel(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate?,
    dailyItems: List<DailyItem>,
) {
    val token = GymTheme.token
    val mealCount = dailyItems.count { it.type == DailyItemType.Meal }
    val activityCount = dailyItems.count { it.type == DailyItemType.Activity }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.md),
    ) {
        if (selectedDate == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                GText(
                    text = "Select a day to view meals and activities",
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary,
                )
            }
            return
        }

        Column(verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)) {
            GText(
                text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)),
                style = token.typography.titleMedium,
                color = token.colors.textPrimary,
            )
            GText(
                text = "$mealCount meals • $activityCount activities",
                style = token.typography.bodyMedium,
                color = token.colors.textSecondary,
            )
        }
        if (dailyItems.isEmpty()) {
            GText(
                text = "No meals or activities for this day",
                style = token.typography.bodyMedium,
                color = token.colors.textSecondary,
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(token.spacing.md),
            ) {
                items(items = dailyItems, key = { it.id }) { item ->
                    GSurface(
                        shape = RoundedCornerShape(token.radius.md),
                        color = token.colors.surface,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = token.spacing.md, vertical = token.spacing.xs),
                            horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(id = item.thumbnailResId),
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(token.radius.sm)),
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                GText(
                                    text = item.title,
                                    style = token.typography.bodyMedium,
                                    color = token.colors.textPrimary,
                                )
                                item.estimatedKcal()?.let { kcal ->
                                    GText(
                                        text = "$kcal kcal",
                                        style = token.typography.labelMedium,
                                        color = if (item.type == DailyItemType.Meal) {
                                            token.colors.primary
                                        } else {
                                            token.colors.textSecondary
                                        },
                                    )
                                }
                            }
                            Icon(
                                imageVector = item.toIcon(),
                                contentDescription = item.type.name,
                                tint = if (item.type == DailyItemType.Meal) token.colors.primary else token.colors.textSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

internal fun buildMonthCells(month: YearMonth): List<LocalDate?> {
    val firstDay = month.atDay(1).dayOfWeek.toCalendarOffset()
    val days = (1..month.lengthOfMonth()).map { day -> month.atDay(day) }
    val leading = List(firstDay) { null }
    val all = leading + days
    val trailing = (7 - (all.size % 7)).let { if (it == 7) 0 else it }
    return all + List(trailing) { null }
}

internal fun generateItemsForMonth(month: YearMonth): Map<LocalDate, List<DailyItem>> {
    return (1..month.lengthOfMonth()).associate { day ->
        val date = month.atDay(day)
        date to createItemsForDate(date)
    }
}

private fun createItemsForDate(date: LocalDate): List<DailyItem> {
    val bucket = (date.dayOfMonth + date.monthValue) % 5
    if (bucket == 0) return emptyList()
    val items = mutableListOf<DailyItem>()
    items += DailyItem(
        id = "meal-${date}-0",
        title = "Meal plan",
        type = DailyItemType.Meal,
        thumbnailResId = R.drawable.muscles,
    )
    if (bucket % 2 == 0) {
        items += DailyItem(
            id = "activity-${date}-0",
            title = "Cardio session",
            type = DailyItemType.Activity,
            thumbnailResId = R.drawable.muscles,
        )
    }
    if (bucket >= 3) {
        items += DailyItem(
            id = "meal-${date}-1",
            title = "Snack log",
            type = DailyItemType.Meal,
            thumbnailResId = R.drawable.muscles,
        )
    }
    return items
}
