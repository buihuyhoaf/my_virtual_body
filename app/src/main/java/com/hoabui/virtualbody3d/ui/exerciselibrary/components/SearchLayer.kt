package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.chip.GFilterChip
import com.hoabui.virtualbody3d.ui.common_ui.atom.icon.GIcon
import com.hoabui.virtualbody3d.ui.common_ui.atom.field.GTextField
import com.hoabui.virtualbody3d.ui.exerciselibrary.ExerciseLibraryQuickChip
import com.hoabui.virtualbody3d.ui.exerciselibrary.selectedQuickChip
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryActions
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.icons.ExerciseLibraryPhosphorIcons
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment

@Composable
fun ExerciseSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    GSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.pill),
        color = token.colors.surface,
        shadowElevation = token.elevation.level1,
        treatment = GSurfaceTreatment.Flat,
        border = null,
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
                GIcon(
                    imageVector = ExerciseLibraryPhosphorIcons.search,
                    contentDescription = null,
                    modifier = Modifier.size(token.spacing.md),
                    tint = token.colors.textSecondary,
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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
            val chipSelected = chip == selected
            GFilterChip(
                label = stringResource(chip.labelRes),
                selected = chipSelected,
                onSelectedChange = { nowSelected ->
                    if (nowSelected) onQuickChipSelect(chip) else onQuickChipSelect(null)
                },
                labelStyle = token.typography.labelSmall,
                leadingIcon = if (chipSelected) {
                    {
                        GIcon(
                            imageVector = ExerciseLibraryPhosphorIcons.filterChipSelected,
                            contentDescription = null,
                            modifier = Modifier.size(token.spacing.sm),
                        )
                    }
                } else {
                    null
                },
            )
        }
    }
}

/**
 * Search field + animated suggestion chips; lives in a sticky header to limit list recomputation scope.
 */
@Composable
fun ExerciseLibrarySearchLayer(
    state: ExerciseLibraryUiState,
    actions: ExerciseLibraryActions,
    isSearchFocused: Boolean,
    onSearchFocusChanged: (Boolean) -> Unit,
    fadeSpec: FiniteAnimationSpec<Float>,
    slideSpec: FiniteAnimationSpec<IntOffset>,
    modifier: Modifier = Modifier,
) {
    val showSuggestionLayer = isSearchFocused || state.searchQuery.isNotEmpty()
    Column(modifier = modifier.fillMaxWidth()) {
        ExerciseSearchBar(
            query = state.searchQuery,
            onQueryChange = actions.onQueryChange,
            onSearchFocusChange = onSearchFocusChanged,
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.animation.AnimatedVisibility(
                visible = showSuggestionLayer,
                enter = fadeIn(fadeSpec) + slideInVertically(
                    animationSpec = slideSpec,
                    initialOffsetY = { fullHeight -> -(fullHeight / 2) },
                ),
                exit = fadeOut(fadeSpec) + slideOutVertically(
                    animationSpec = slideSpec,
                    targetOffsetY = { fullHeight -> -(fullHeight / 2) },
                ),
            ) {
                ExerciseSearchSuggestionChips(
                    libraryState = state,
                    onQuickChipSelect = actions.onQuickChipSelect,
                )
            }
        }
    }
}
