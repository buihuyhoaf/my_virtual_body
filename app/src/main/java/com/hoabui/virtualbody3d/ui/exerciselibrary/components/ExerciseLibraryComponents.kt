package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.hoabui.virtualbody3d.ui.common_ui.atom.field.GTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.extensions.badgeLevelBackground
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Difficulty
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.CardSize
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCard
import com.hoabui.virtualbody3d.ui.common_ui.molecule.chip.GSelectableTagRow
import com.hoabui.virtualbody3d.ui.common_ui.molecule.chip.GTagOption
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseSectionUiItem
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun ExerciseSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    GTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = stringResource(R.string.exercise_library_search_hint),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = token.colors.textSecondary
            )
        },
        singleLine = true,
    )
}

private const val FILTER_ALL_ID = "__all__"

@Composable
fun ExerciseFilterChips(
    selectedBodyRegion: BodyRegion?,
    selectedDifficulty: Difficulty?,
    onBodyRegionSelect: (BodyRegion?) -> Unit,
    onDifficultySelect: (Difficulty?) -> Unit,
    modifier: Modifier = Modifier
) {
    val allLabel = stringResource(R.string.exercise_library_filter_all)
    val bodyRegionOptions = buildList {
        add(GTagOption(id = FILTER_ALL_ID, label = allLabel))
        BodyRegion.entries.forEach { region ->
            add(GTagOption(id = region.name, label = stringResource(ExerciseDisplayResources.bodyRegionResId(region))))
        }
    }
    val difficultyOptions = buildList {
        add(GTagOption(id = FILTER_ALL_ID, label = allLabel))
        Difficulty.entries.forEach { difficulty ->
            add(GTagOption(id = difficulty.name, label = stringResource(ExerciseDisplayResources.difficultyResId(difficulty))))
        }
    }
    val selectedRegion = if (selectedBodyRegion == null) setOf(FILTER_ALL_ID) else setOf(selectedBodyRegion.name)
    val selectedDiff = if (selectedDifficulty == null) setOf(FILTER_ALL_ID) else setOf(selectedDifficulty.name)

    Column(modifier = modifier.fillMaxWidth()) {
        GSelectableTagRow(
            options = bodyRegionOptions,
            selected = selectedRegion,
            onToggle = { id ->
                if (id == FILTER_ALL_ID) onBodyRegionSelect(null)
                else onBodyRegionSelect(BodyRegion.valueOf(id))
            },
            title = stringResource(R.string.exercise_library_filter_body_region),
            singleSelect = true,
        )
        GSelectableTagRow(
            options = difficultyOptions,
            selected = selectedDiff,
            onToggle = { id ->
                if (id == FILTER_ALL_ID) onDifficultySelect(null)
                else onDifficultySelect(Difficulty.valueOf(id))
            },
            title = stringResource(R.string.exercise_library_filter_difficulty),
            singleSelect = true,
        )
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
        GSectionHeader(title = regionLabel)
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
            val token = GymTheme.token
            GImageCard(
                model = item.imageResId,
                contentDescription = item.name,
                firstLineText = item.name,
                secondLineText = stringResource(ExerciseDisplayResources.bodyRegionResId(item.bodyRegion)),
                cardSize = CardSize.Large,
                badge = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(token.radius.sm))
                            .badgeLevelBackground(item.difficulty)
                            .padding(
                                horizontal = token.spacing.xs,
                                vertical = token.spacing.xxs,
                            ),
                    ) {
                        GText(
                            text = stringResource(ExerciseDisplayResources.difficultyResId(item.difficulty)),
                            style = token.typography.labelSmall,
                            color = token.colors.onPrimary,
                        )
                    }
                },
                onClick = { onExerciseClick(item) },
            )
        }
    }
}
