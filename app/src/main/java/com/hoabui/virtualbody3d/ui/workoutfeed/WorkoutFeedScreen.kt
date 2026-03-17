package com.hoabui.virtualbody3d.ui.workoutfeed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = token.spacing.xl))
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

// ---------------------------------------------------------------------------
// WorkoutDayCard (stateless, receives domain model)
// ---------------------------------------------------------------------------

@Composable
private fun TodayWorkoutCard(
    day: WorkoutFeedItem
) {
    WorkoutDayCard(
        day = day,
        isToday = true
    )
}

@Composable
private fun WorkoutDayCard(
    day: WorkoutFeedItem,
    isToday: Boolean = false
) {
    val token = GymTheme.token
    val containerColor = if (isToday) {
        token.colors.surfaceSubtle
    } else {
        token.colors.surface
    }
    val contentPadding = if (isToday) {
        token.spacing.lg
    } else {
        token.card.padding
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.card.cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = token.card.elevation),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            ) {
                WorkoutDayHeader(
                    label = day.label,
                    date = day.date,
                    isToday = isToday
                )
                SectionHorizontalRow(
                    modifier = Modifier.padding(top = token.spacing.xs)
                ) {
                    items(
                        items = day.exercises,
                        key = { "${it.name}-${it.sets}x${it.reps}" }
                    ) { exercise ->
                        CardImageWithText(
                            imageRes = exercise.imageResId,
                            firstLineText = exercise.name,
                            secondLineText = "${exercise.sets}x${exercise.reps}",
                        ) {

                        }
                    }
                }
                WorkoutSummaryRow(
                    durationMinutes = day.durationMinutes,
                    estimatedCalories = day.estimatedCalories,
                    muscleGroups = day.muscleGroups
                )
            }
        }
    )
}

@Composable
private fun WorkoutDayHeader(
    label: String,
    date: LocalDate,
    isToday: Boolean = false
) {
    val token = GymTheme.token
    val dateStr = if (label.equals("Today", ignoreCase = true)) {
        "Hôm nay"
    } else {
        date.toVietnameseTopBarDate()
    }
    val textStyle = if (isToday) {
        token.typography.titleLarge
    } else {
        token.typography.titleSmall
    }
    Text(
        text = dateStr,
        style = textStyle,
        color = token.colors.textSecondary
    )
}

// ---------------------------------------------------------------------------
// FeelingSection (stateless)
// ---------------------------------------------------------------------------

@Composable
private fun FeelingSection(
    feeling: String
) {
    val token = GymTheme.token
    Column(
        modifier = Modifier.padding(top = token.spacing.lg)
    ) {
        Text(
            text = "Feeling",
            style = token.typography.labelMedium,
            color = token.colors.textSecondary
        )
        Text(
            text = feeling,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary,
            modifier = Modifier.padding(top = token.spacing.xxs)
        )
    }
}

// ---------------------------------------------------------------------------
// WorkoutSummaryRow – duration, calories, muscle groups
// ---------------------------------------------------------------------------

@Composable
private fun WorkoutSummaryRow(
    durationMinutes: Int,
    estimatedCalories: Int,
    muscleGroups: List<String>
) {
    val token = GymTheme.token

    val durationText = "$durationMinutes min"
    val caloriesText = "$estimatedCalories kcal"

    val muscleLabel = when {
        muscleGroups.isEmpty() -> null
        muscleGroups.size <= 2 -> muscleGroups.joinToString(", ")
        else -> {
            val first = muscleGroups.first()
            val remainingCount = muscleGroups.size - 1
            "$first +$remainingCount"
        }
    }

    Row(
        modifier = Modifier.padding(top = token.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = durationText,
            style = token.typography.bodySmall,
            color = token.colors.textSecondary
        )
        Text(
            text = "·",
            style = token.typography.bodySmall,
            color = token.colors.textSecondary
        )
        Text(
            text = caloriesText,
            style = token.typography.bodySmall,
            color = token.colors.textSecondary
        )
        if (muscleLabel != null) {
            Text(
                text = "·",
                style = token.typography.bodySmall,
                color = token.colors.textSecondary
            )
            Text(
                text = muscleLabel,
                style = token.typography.bodySmall,
                color = token.colors.textPrimary
            )
        }
    }
}
