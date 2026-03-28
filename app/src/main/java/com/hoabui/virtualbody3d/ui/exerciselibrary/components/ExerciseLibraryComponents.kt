package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
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
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.image.toImageModel
import com.hoabui.virtualbody3d.ui.common_ui.molecule.chip.GSelectableTagRow
import com.hoabui.virtualbody3d.ui.common_ui.molecule.chip.GTagOption
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseSectionRow
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseSectionUiModel
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
    val resourceProvider = LocalResourceProvider.current
    val regionLabel = stringResource(ExerciseDisplayResources.bodyRegionResId(section.bodyRegion))
    val uiSection = GExerciseSectionUiModel(
        id = section.bodyRegion.name,
        title = regionLabel,
        items = section.exercises.map { exercise ->
            GExerciseCardUiModel(
                id = exercise.id,
                imageModel = exercise.image.toImageModel(resourceProvider),
                title = exercise.name,
                subtitle = stringResource(ExerciseDisplayResources.bodyRegionResId(exercise.bodyRegion)),
                badgeText = stringResource(ExerciseDisplayResources.difficultyResId(exercise.difficulty)),
            )
        },
    )
    GExerciseSectionRow(
        section = uiSection,
        modifier = modifier,
        onItemClick = { id -> section.exercises.firstOrNull { it.id == id }?.let(onExerciseClick) },
        badgeContent = { item ->
            val exercise = section.exercises.firstOrNull { it.id == item.id }
            if (exercise != null) {
                val token = GymTheme.token
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(token.radius.sm))
                        .badgeLevelBackground(exercise.difficulty)
                        .padding(
                            horizontal = token.spacing.xs,
                            vertical = token.spacing.xxs,
                        ),
                ) {
                    GText(
                        text = item.badgeText ?: "",
                        style = token.typography.labelSmall,
                        color = token.colors.onPrimary,
                    )
                }
            }
        },
    )
}
