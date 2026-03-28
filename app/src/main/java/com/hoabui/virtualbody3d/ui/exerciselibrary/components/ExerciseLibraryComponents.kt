package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import com.hoabui.virtualbody3d.ui.common_ui.atom.field.GTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.chip.GFilterChip
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.image.toImageModel
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseSectionCardRow
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseSectionUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.ExerciseLibraryQuickChip
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.exerciselibrary.selectedQuickChip
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseSectionUiItem
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun ExerciseSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.pill),
        color = token.colors.surface,
        shadowElevation = token.elevation.level1,
    ) {
        GTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = token.spacing.lg + token.spacing.md)
                .onFocusChanged { onSearchFocusChange(it.isFocused) },
            placeholder = stringResource(R.string.exercise_library_search_hint),
            textStyle = token.typography.bodyMedium,
            placeholderStyle = token.typography.bodyMedium,
            shape = RoundedCornerShape(token.radius.pill),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(token.spacing.md),
                    tint = token.colors.textSecondary,
                )
            },
            singleLine = true,
        )
    }
}

@Composable
fun ExerciseSearchSuggestionChips(
    libraryState: ExerciseLibraryUiState,
    onQuickChipSelect: (ExerciseLibraryQuickChip?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val selected = libraryState.selectedQuickChip()
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        contentPadding = PaddingValues(
            top = token.spacing.xs,
            bottom = token.spacing.xxs,
        ),
    ) {
        items(ExerciseLibraryQuickChip.entries.toList(), key = { it.name }) { chip ->
            GFilterChip(
                label = stringResource(chip.labelRes),
                selected = chip == selected,
                onSelectedChange = { nowSelected ->
                    if (nowSelected) onQuickChipSelect(chip) else onQuickChipSelect(null)
                },
                labelStyle = token.typography.labelSmall,
            )
        }
    }
}

@Composable
fun ExerciseLibraryEmptyState(modifier: Modifier = Modifier) {
    val token = GymTheme.token
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = token.spacing.lg, vertical = token.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GText(
            text = stringResource(R.string.exercise_library_empty_title),
            style = token.typography.titleMedium,
            color = token.colors.textPrimary,
        )
        GText(
            text = stringResource(R.string.exercise_library_empty_message),
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary,
            modifier = Modifier.padding(top = token.spacing.sm),
        )
    }
}

@Composable
fun ExerciseSection(
    modifier: Modifier = Modifier,
    section: ExerciseSectionUiItem,
    onExerciseClick: (ExerciseUiModel) -> Unit = {},
    onQuickAdd: ((String) -> Unit)? = null,
    quickAddContentDescription: String = "",
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
                subtitle = exerciseCardSubtitleLine(exercise),
            )
        },
    )
    GExerciseSectionCardRow(
        section = uiSection,
        modifier = modifier,
        onItemClick = { id -> section.exercises.firstOrNull { it.id == id }?.let(onExerciseClick) },
        onQuickAdd = onQuickAdd,
        quickAddContentDescription = quickAddContentDescription,
    )
}

@Composable
private fun exerciseCardSubtitleLine(exercise: ExerciseUiModel): String {
    if (exercise.primaryMuscles.isNotEmpty()) {
        val parts = mutableListOf<String>()
        for (muscle in exercise.primaryMuscles.take(2)) {
            parts.add(stringResource(ExerciseDisplayResources.muscleGroupResId(muscle)))
        }
        return parts.joinToString()
    }
    if (exercise.equipment != null) {
        return stringResource(ExerciseDisplayResources.equipmentResId(exercise.equipment))
    }
    return stringResource(R.string.exercise_library_card_subtitle_fallback)
}
