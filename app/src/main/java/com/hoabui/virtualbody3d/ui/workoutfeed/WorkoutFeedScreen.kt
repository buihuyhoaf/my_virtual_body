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

// ---------------------------------------------------------------------------
// Date formatting (pure, no UI)
// ---------------------------------------------------------------------------

private fun formatHeaderDate(date: LocalDate): String {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val monthDay = date.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()))
    return "$dayOfWeek, $monthDay"
}

// ---------------------------------------------------------------------------
// Screen (stateless: collects state from ViewModel, passes to composables)
// ---------------------------------------------------------------------------

@Composable
fun WorkoutFeedScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = token.spacing.md)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md),
            contentPadding = PaddingValues(
                top = token.spacing.md,
                bottom = token.spacing.xl
            )
        ) {
            items(
                items = feedItems,
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
private fun WorkoutDayCard(
    day: WorkoutFeedItem
) {
    val token = GymTheme.token
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.card.cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = token.colors.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = token.card.elevation),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(token.card.padding)
            ) {
                WorkoutDayHeader(
                    label = day.label,
                    date = day.date
                )
                SectionHorizontalRow(
                    modifier = Modifier.padding(top = token.spacing.md)
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
                FeelingSection(feeling = day.feeling)
            }
        }
    )
}

@Composable
private fun WorkoutDayHeader(
    label: String,
    date: LocalDate
) {
    val token = GymTheme.token
    val dateStr = if (label.equals("Today", ignoreCase = true)) {
        "Hôm nay"
    } else {
        date.toVietnameseTopBarDate()
    }
    Text(
        text = dateStr,
        style = token.typography.titleSmall,
        color = token.colors.textSecondary
    )
}

// ---------------------------------------------------------------------------
// ExerciseRow (stateless, receives primitive data)
// ---------------------------------------------------------------------------

@Composable
private fun ExerciseRow(
    name: String,
    sets: Int,
    reps: Int
) {
    val token = GymTheme.token
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
        Text(
            text = "$sets × $reps",
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary
        )
    }
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
