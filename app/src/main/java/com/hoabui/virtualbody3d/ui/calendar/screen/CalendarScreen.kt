package com.hoabui.virtualbody3d.ui.calendar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.calendar.components.CalendarDetailPanel
import com.hoabui.virtualbody3d.ui.calendar.components.CalendarMonthSection
import com.hoabui.virtualbody3d.ui.calendar.components.buildMonthCells
import com.hoabui.virtualbody3d.ui.calendar.components.generateItemsForMonth
import com.hoabui.virtualbody3d.ui.calendar.model.MonthGridUiModel
import com.hoabui.virtualbody3d.ui.calendar.state.DailyItem
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

private const val CalendarMonthBatchSize = 3

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val today = remember { LocalDate.now() }
    var months by remember {
        mutableStateOf(
            List(CalendarMonthBatchSize) { YearMonth.now().plusMonths(it.toLong()) }
        )
    }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val dailyItemsByDate = remember(months) {
        months.flatMap { month -> generateItemsForMonth(month).entries }.associate { it.toPair() }
    }
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

    fun onLoadMoreMonths() {
        val nextStart = months.lastOrNull()?.plusMonths(1) ?: YearMonth.now()
        months = months + List(CalendarMonthBatchSize) { nextStart.plusMonths(it.toLong()) }
    }

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
                selectedDate = today
            }
            didInitialFocus = true
        }
    }

    GScaffold(modifier = modifier) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                                onDateSelected = { selectedDate = it }
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
}

