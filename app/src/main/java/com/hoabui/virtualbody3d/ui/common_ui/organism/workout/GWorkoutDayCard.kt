package com.hoabui.virtualbody3d.ui.common_ui.organism.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.CardSize
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCard
import com.hoabui.virtualbody3d.ui.theme.GymTheme

data class GWorkoutExerciseUiModel(
    val id: String,
    val imageModel: Any?,
    val title: String,
    val subtitle: String,
)

data class GWorkoutDayUiModel(
    val id: String,
    val dateLabel: String,
    val isToday: Boolean,
    val exercises: List<GWorkoutExerciseUiModel>,
    val durationMinutes: Int,
    val estimatedCalories: Int,
    val muscleGroups: List<String>,
)

@Composable
fun GWorkoutDayCard(
    day: GWorkoutDayUiModel,
    modifier: Modifier = Modifier,
    onExerciseClick: (exerciseId: String) -> Unit = {},
    onDayClick: (() -> Unit)? = null,
) {
    val token = GymTheme.token
    val containerColor = if (day.isToday) token.colors.surfaceSubtle else token.colors.surface
    val contentPadding = if (day.isToday) token.spacing.lg else token.card.padding

    GCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = containerColor,
        onClick = onDayClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
        ) {
            GText(
                text = day.dateLabel,
                style = if (day.isToday) token.typography.titleLarge else token.typography.titleSmall,
                color = token.colors.textSecondary,
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = token.spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
                contentPadding = PaddingValues(horizontal = token.spacing.xxs, vertical = token.spacing.xs),
            ) {
                items(
                    items = day.exercises,
                    key = { it.id },
                ) { exercise ->
                    GImageCard(
                        model = exercise.imageModel,
                        contentDescription = exercise.title,
                        firstLineText = exercise.title,
                        secondLineText = exercise.subtitle,
                        cardSize = CardSize.Large,
                        onClick = { onExerciseClick(exercise.id) },
                    )
                }
            }
            GWorkoutDaySummaryRow(
                durationMinutes = day.durationMinutes,
                estimatedCalories = day.estimatedCalories,
                muscleGroups = day.muscleGroups,
            )
        }
    }
}

@Composable
private fun GWorkoutDaySummaryRow(
    durationMinutes: Int,
    estimatedCalories: Int,
    muscleGroups: List<String>,
) {
    val token = GymTheme.token
    val durationText = "$durationMinutes min"
    val caloriesText = "$estimatedCalories kcal"
    val muscleLabel = when {
        muscleGroups.isEmpty() -> null
        muscleGroups.size <= 2 -> muscleGroups.joinToString(", ")
        else -> "${muscleGroups.first()} +${muscleGroups.size - 1}"
    }

    Row(
        modifier = Modifier.padding(top = token.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GText(text = durationText, style = token.typography.bodySmall, color = token.colors.textSecondary)
        GText(text = "·", style = token.typography.bodySmall, color = token.colors.textSecondary)
        GText(text = caloriesText, style = token.typography.bodySmall, color = token.colors.textSecondary)
        if (muscleLabel != null) {
            GText(text = "·", style = token.typography.bodySmall, color = token.colors.textSecondary)
            GText(text = muscleLabel, style = token.typography.bodySmall, color = token.colors.textPrimary)
        }
    }
}
