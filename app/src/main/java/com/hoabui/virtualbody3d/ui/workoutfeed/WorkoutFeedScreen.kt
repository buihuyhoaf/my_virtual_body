package com.hoabui.virtualbody3d.ui.workoutfeed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.extensions.toVietnameseTopBarDate
import com.hoabui.virtualbody3d.domain.model.WorkoutFeedItem
import com.hoabui.virtualbody3d.navigation.AppTopBarBack
import com.hoabui.virtualbody3d.ui.body.components.SectionHorizontalRow
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.CardImageWithText
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.workoutfeed.components.TodayWorkoutCard
import com.hoabui.virtualbody3d.ui.workoutfeed.components.WorkoutDayCard
import com.hoabui.virtualbody3d.ui.workoutfeed.state.WorkoutFeedUiState
import com.hoabui.virtualbody3d.ui.workoutfeed.viewmodel.WorkoutFeedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WorkoutFeedScreen(
    modifier: Modifier = Modifier,
    viewModel: WorkoutFeedViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    UiStateContent(
        state = state,
        modifier = modifier,
        successContent = { mod: Modifier, uiState: WorkoutFeedUiState ->
            Column(
                modifier = mod.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                WorkoutFeedContent(
                    modifier = Modifier.weight(1f),
                    feedItems = uiState.feedItems
                )
            }
        }
    )
}

@Composable
private fun WorkoutFeedContent(
    modifier: Modifier,
    feedItems: List<WorkoutFeedItem>
) {
    val token = GymTheme.token
    val today = LocalDate.now()

    val todayItem = feedItems.firstOrNull { item ->
        item.label.equals("Today", ignoreCase = true) || item.date == today
    }
    val pastItems = feedItems.filterNot { item ->
        item.label.equals("Today", ignoreCase = true) || item.date == today
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = token.spacing.md)
    ) {
        if (todayItem != null) {
            TodayWorkoutCard(day = todayItem)
        }
        Spacer(modifier = Modifier.padding(bottom = token.spacing.xl))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
            contentPadding = PaddingValues(
                top = token.spacing.xs,
                bottom = token.spacing.xl
            )
        ) {
            items(
                items = pastItems,
                key = { it.date.toString() + it.workoutName }
            ) { day ->
                WorkoutDayCard(day = day)
            }
        }
    }
}
