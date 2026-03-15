package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            contentPadding = PaddingValues(vertical = token.spacing.xxs)
        ) {
            item(key = "body_all") {
                FilterChip(
                    selected = selectedBodyRegion == null,
                    onClick = { onBodyRegionSelect(null) },
                    label = { Text(stringResource(R.string.exercise_library_filter_all)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = token.colors.primarySoft,
                        selectedLabelColor = token.colors.primary,
                        containerColor = token.colors.surface,
                        labelColor = token.colors.textPrimary
                    )
                )
            }
            items(BodyRegion.entries, key = { it.name }) { region ->
                val label = stringResource(ExerciseDisplayResources.bodyRegionResId(region))
                FilterChip(
                    selected = selectedBodyRegion == region,
                    onClick = { onBodyRegionSelect(region) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = token.colors.primarySoft,
                        selectedLabelColor = token.colors.primary,
                        containerColor = token.colors.surface,
                        labelColor = token.colors.textPrimary
                    )
                )
            }
        }
        Text(
            text = stringResource(R.string.exercise_library_filter_difficulty),
            style = token.typography.labelMedium,
            color = token.colors.textSecondary
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            contentPadding = PaddingValues(vertical = token.spacing.xxs)
        ) {
            item(key = "diff_all") {
                FilterChip(
                    selected = selectedDifficulty == null,
                    onClick = { onDifficultySelect(null) },
                    label = { Text(stringResource(R.string.exercise_library_filter_all)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = token.colors.primarySoft,
                        selectedLabelColor = token.colors.primary,
                        containerColor = token.colors.surface,
                        labelColor = token.colors.textPrimary
                    )
                )
            }
            items(Difficulty.entries, key = { it.name }) { difficulty ->
                FilterChip(
                    selected = selectedDifficulty == difficulty,
                    onClick = { onDifficultySelect(difficulty) },
                    label = {
                        Text(
                            stringResource(
                                ExerciseDisplayResources.difficultyResId(
                                    difficulty
                                )
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = token.colors.primarySoft,
                        selectedLabelColor = token.colors.primary,
                        containerColor = token.colors.surface,
                        labelColor = token.colors.textPrimary
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
