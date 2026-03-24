package com.hoabui.virtualbody3d.ui.workoutfeed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hoabui.virtualbody3d.core.extensions.toVietnameseTopBarDate
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutFeedItem
import com.hoabui.virtualbody3d.ui.body.components.SectionHorizontalRow
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.CardSize
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCard
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.LocalDate


// ---------------------------------------------------------------------------
// WorkoutDayCard (stateless, receives domain model)
// ---------------------------------------------------------------------------

@Composable
fun TodayWorkoutCard(
    day: WorkoutFeedItem
) {
    WorkoutDayCard(
        day = day,
        isToday = true
    )
}

@Composable
fun WorkoutDayCard(
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

    GCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = containerColor,
    ) {
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
                        GImageCard(
                            model = exercise.imageResId,
                            contentDescription = exercise.name,
                            firstLineText = exercise.name,
                            secondLineText = "${exercise.sets}x${exercise.reps}",
                            cardSize = CardSize.Large,
                            onClick = {},
                        )
                    }
                }
                WorkoutSummaryRow(
                    durationMinutes = day.durationMinutes,
                    estimatedCalories = day.estimatedCalories,
                    muscleGroups = day.muscleGroups
                )
            }
    }
}

@Composable
fun WorkoutDayHeader(
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
