package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.BodyRegion
import com.hoabui.virtualbody3d.domain.model.Difficulty
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseSectionUiItem
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.outlinedTextFieldColors

@Composable
fun ExerciseSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = stringResource(R.string.exercise_library_search_hint),
                style = token.typography.bodyMedium,
                color = token.colors.textPlaceholder
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = token.colors.textSecondary
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(token.radius.sm),
        colors = outlinedTextFieldColors(token.colors)
    )
}

@Composable
fun ExerciseFilterChips(
    selectedBodyRegion: BodyRegion?,
    selectedDifficulty: Difficulty?,
    onBodyRegionSelect: (BodyRegion?) -> Unit,
    onDifficultySelect: (Difficulty?) -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
    ) {
        Text(
            text = stringResource(R.string.exercise_library_filter_body_region),
            style = token.typography.labelMedium,
            color = token.colors.textSecondary
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
        ) {
            FilterChip(
                selected = selectedBodyRegion == null,
                onClick = { onBodyRegionSelect(null) },
                label = { Text(stringResource(R.string.exercise_library_filter_all)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = token.colors.primarySoft,
                    selectedLabelColor = token.colors.primary,
                    containerColor = token.colors.surfaceSubtle,
                    labelColor = token.colors.textSecondary
                )
            )

            BodyRegion.entries.forEach { region ->
                val label = stringResource(ExerciseDisplayResources.bodyRegionResId(region))
                FilterChip(
                    selected = selectedBodyRegion == region,
                    onClick = { onBodyRegionSelect(region) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = token.colors.primarySoft,
                        selectedLabelColor = token.colors.primary,
                        containerColor = token.colors.surfaceSubtle,
                        labelColor = token.colors.textSecondary
                    )
                )
            }
        }
        Text(
            text = stringResource(R.string.exercise_library_filter_difficulty),
            style = token.typography.labelMedium,
            color = token.colors.textSecondary
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
        ){
            FilterChip(
                selected = selectedDifficulty == null,
                onClick = { onDifficultySelect(null) },
                label = { Text(stringResource(R.string.exercise_library_filter_all)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = token.colors.primarySoft,
                    selectedLabelColor = token.colors.primary,
                    containerColor = token.colors.surfaceSubtle,
                    labelColor = token.colors.textSecondary
                )
            )
            Difficulty.entries.forEach { difficulty ->
                val difficultyLabelColor = when (difficulty) {
                    Difficulty.Beginner -> token.colors.difficultyBeginnerText
                    Difficulty.Intermediate -> token.colors.difficultyIntermediateText
                    Difficulty.Advanced -> token.colors.difficultyAdvancedText
                }
                val isSelected = selectedDifficulty == difficulty
                FilterChip(
                    selected = isSelected,
                    onClick = { onDifficultySelect(difficulty) },
                    label = {
                        Text(
                            stringResource(ExerciseDisplayResources.difficultyResId(difficulty)),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = token.colors.primarySoft,
                        selectedLabelColor = token.colors.primary,
                        containerColor = token.colors.surfaceSubtle,
                        labelColor = difficultyLabelColor
                    )
                )
            }
        }

    }
}

@Composable
fun ExerciseSection(
    modifier: Modifier = Modifier,
    section: ExerciseSectionUiItem,
    onExerciseClick: (ExerciseUiModel) -> Unit = {},
) {
    val token = GymTheme.token
    val regionLabel = stringResource(ExerciseDisplayResources.bodyRegionResId(section.bodyRegion))
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
    ) {
        Text(
            text = regionLabel,
            style = token.typography.titleMedium,
            color = token.colors.textPrimary
        )
        ExerciseRow(
            exercises = section.exercises,
            onExerciseClick = onExerciseClick
        )
    }
}

@Composable
fun ExerciseRow(
    exercises: List<ExerciseUiModel>,
    onExerciseClick: (ExerciseUiModel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
        contentPadding = PaddingValues(horizontal = token.spacing.xxs, vertical = token.spacing.xs)
    ) {
        items(exercises, key = { it.id }) { item ->
            CardImageWithText(
                imageRes = item.imageResId,
                firstLineText = item.name,
                secondLineText = stringResource(ExerciseDisplayResources.bodyRegionResId(item.bodyRegion)),
                badgeText = stringResource(ExerciseDisplayResources.difficultyResId(item.difficulty)),
                badgeLevel = item.difficulty,
                onClick = { onExerciseClick(item) }
            )
        }
    }
}
