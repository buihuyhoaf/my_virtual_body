package com.hoabui.virtualbody3d.ui.calendar.screen

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.calendar.extensions.estimatedKcal
import com.hoabui.virtualbody3d.ui.calendar.extensions.toCalendarOffset
import com.hoabui.virtualbody3d.ui.calendar.extensions.toIcon
import com.hoabui.virtualbody3d.ui.calendar.model.MonthGridUiModel
import com.hoabui.virtualbody3d.ui.calendar.state.DailyItem
import com.hoabui.virtualbody3d.ui.calendar.state.DailyItemType
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    months: List<YearMonth>,
    selectedDate: LocalDate?,
    dailyItemsByDate: Map<LocalDate, List<DailyItem>>,
    onDateSelected: (LocalDate) -> Unit,
    onLoadMoreMonths: () -> Unit
) {
    val token = GymTheme.token
    val listState = rememberLazyListState()
    var didInitialFocus by remember { mutableStateOf(false) }
    val monthGrids = remember(months) { months.map { month -> MonthGridUiModel(month, buildMonthCells(month)) } }
    val visibleYear by remember(listState, monthGrids) {
        derivedStateOf {
            monthGrids.getOrNull(listState.firstVisibleItemIndex)?.month?.year
                ?: selectedDate?.year
                ?: LocalDate.now().year
        }
    }
    val today = remember { LocalDate.now() }

    LaunchedEffect(listState, months.size) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            val threshold = (info.totalItemsCount - 2).coerceAtLeast(0)
            lastVisible >= threshold
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadMoreMonths() }
    }

    LaunchedEffect(months, selectedDate) {
        if (didInitialFocus || months.isEmpty()) return@LaunchedEffect
        val todayMonthIndex = months.indexOfFirst { it.year == today.year && it.month == today.month }
        if (todayMonthIndex >= 0) {
            listState.scrollToItem(todayMonthIndex)
            if (selectedDate == null) {
                onDateSelected(today)
            }
            didInitialFocus = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = token.spacing.md, vertical = token.spacing.xs),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
    ) {
        Text(
            text = visibleYear.toString(),
            style = token.typography.headlineMedium,
            color = token.calendar.yearTextColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = token.spacing.md, vertical = token.spacing.md)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(token.radius.lg),
                color = token.colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(token.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(token.spacing.md)
                ) {
                    Text(
                        text = "Timeline",
                        style = token.typography.titleMedium,
                        color = token.colors.textSecondary
                    )
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(token.spacing.xl)
                    ) {
                        items(items = monthGrids, key = { it.month }) { month ->
                            CalendarMonthSection(
                                month = month.month,
                                cells = month.cells,
                                selectedDate = selectedDate,
                                today = today,
                                dailyItemsByDate = dailyItemsByDate,
                                onDateSelected = onDateSelected
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.32f)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-12).dp),
                shape = RoundedCornerShape(
                    topStart = token.radius.xl,
                    topEnd = token.radius.xl,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),
                color = token.colors.surfaceOverlay,
                border = androidx.compose.foundation.BorderStroke(1.dp, token.calendar.panelBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = token.spacing.md, vertical = token.spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .height(4.dp)
                            .fillMaxWidth(0.18f)
                            .clip(RoundedCornerShape(token.radius.lg))
                            .background(token.colors.surfaceBorder)
                    )
                    CalendarDetailPanel(
                        modifier = Modifier.fillMaxSize(),
                        selectedDate = selectedDate,
                        dailyItems = selectedDate?.let { dailyItemsByDate[it] }.orEmpty()
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarMonthSection(
    month: YearMonth,
    cells: List<LocalDate?>,
    selectedDate: LocalDate?,
    today: LocalDate,
    dailyItemsByDate: Map<LocalDate, List<DailyItem>>,
    onDateSelected: (LocalDate) -> Unit
) {
    val token = GymTheme.token
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.md)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
        ) {
            Text(
                text = month.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase(Locale.ENGLISH),
                style = token.typography.labelLarge,
                color = token.calendar.monthTextColor
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(token.calendar.monthDividerColor)
            )
        }

        cells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                week.forEach { date ->
                    if (date == null) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    } else {
                        CalendarDayCell(
                            modifier = Modifier.weight(1f),
                            date = date,
                            isToday = date == today,
                            items = dailyItemsByDate[date].orEmpty(),
                            selected = date == selectedDate,
                            onClick = { onDateSelected(date) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    modifier: Modifier = Modifier,
    date: LocalDate,
    isToday: Boolean,
    items: List<DailyItem>,
    selected: Boolean,
    onClick: () -> Unit
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
                shape = cellShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .clip(RoundedCornerShape(token.radius.sm)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = thumbnail?.thumbnailResId ?: placeholderResId),
                contentDescription = "Day thumbnail ${date.dayOfMonth}",
                contentScale = ContentScale.Crop,
                alpha = if (items.isEmpty()) 0.45f else 1f,
                modifier = Modifier.fillMaxSize()
            )
        }
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .background(color = token.colors.primary, shape = RoundedCornerShape(token.radius.md))
            ) {
                Text(
                    text = badgeText,
                    style = token.typography.labelSmall,
                    color = token.colors.surface,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun CalendarDetailPanel(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate?,
    dailyItems: List<DailyItem>
) {
    val token = GymTheme.token
    val mealCount = dailyItems.count { it.type == DailyItemType.Meal }
    val activityCount = dailyItems.count { it.type == DailyItemType.Activity }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.md)
    ) {
        if (selectedDate == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Select a day to view meals and activities",
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary
                )
            }
            return
        }

        Column(verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)) {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)),
                style = token.typography.titleMedium,
                color = token.colors.textPrimary
            )
            Text(
                text = "$mealCount meals • $activityCount activities",
                style = token.typography.bodyMedium,
                color = token.colors.textSecondary
            )
        }
        if (dailyItems.isEmpty()) {
            Text(
                text = "No meals or activities for this day",
                style = token.typography.bodyMedium,
                color = token.colors.textSecondary
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(token.spacing.md)
            ) {
                items(items = dailyItems, key = { it.id }) { item ->
                    Surface(
                        shape = RoundedCornerShape(token.radius.md),
                        color = token.colors.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = token.spacing.md, vertical = token.spacing.xs),
                            horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = item.thumbnailResId),
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(token.radius.sm))
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    style = token.typography.bodyMedium,
                                    color = token.colors.textPrimary
                                )
                                item.estimatedKcal()?.let { kcal ->
                                    Text(
                                        text = "$kcal kcal",
                                        style = token.typography.labelMedium,
                                        color = if (item.type == DailyItemType.Meal) {
                                            token.colors.primary
                                        } else {
                                            token.colors.textSecondary
                                        }
                                    )
                                }
                            }
                            Icon(
                                imageVector = item.toIcon(),
                                contentDescription = item.type.name,
                                tint = if (item.type == DailyItemType.Meal) token.colors.primary else token.colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildMonthCells(month: YearMonth): List<LocalDate?> {
    val firstDay = month.atDay(1).dayOfWeek.toCalendarOffset()
    val days = (1..month.lengthOfMonth()).map { day -> month.atDay(day) }
    val leading = List(firstDay) { null }
    val all = leading + days
    val trailing = (7 - (all.size % 7)).let { if (it == 7) 0 else it }
    return all + List(trailing) { null }
}
