package com.hoabui.virtualbody3d.ui.exerciselibrary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseDetailDialog
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibraryEmptyState
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseSearchBar
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseSearchSuggestionChips
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseSection
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel.ExerciseLibraryViewModel
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun ExerciseLibraryScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onAddToWorkout: (exerciseId: String) -> Unit = {},
    viewModel: ExerciseLibraryViewModel = hiltViewModel()
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    UiStateContent(
        state = screenState,
        modifier = modifier,
        successContent = { mod, data ->
            GScaffold(
                modifier = mod,
            ) {
                ExerciseLibraryScreenContent(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = data,
                    onQueryChange = viewModel::updateSearchQuery,
                    onQuickChipSelect = viewModel::selectQuickChip,
                    onExerciseClick = viewModel::selectExerciseForDetail,
                    onQuickAdd = onAddToWorkout,
                )
                data.selectedExerciseForDetail?.let { exercise ->
                    ExerciseDetailDialog(
                        exercise = exercise,
                        onAddClick = {
                            viewModel.clearExerciseDetail()
                            onAddToWorkout(exercise.id)
                        },
                        onDismiss = viewModel::clearExerciseDetail
                    )
                }
            }
        }
    )

}

@Composable
fun ExerciseLibraryScreenContent(
    modifier: Modifier = Modifier,
    state: ExerciseLibraryUiState,
    onQueryChange: (String) -> Unit,
    onQuickChipSelect: (ExerciseLibraryQuickChip?) -> Unit,
    onExerciseClick: (String) -> Unit = {},
    onQuickAdd: (String) -> Unit = {},
) {
    val isSearchFocused = remember { mutableStateOf(false) }
    val showSuggestionLayer = isSearchFocused.value || state.searchQuery.isNotEmpty()
    val token = GymTheme.token
    val quickAddCd = stringResource(R.string.exercise_quick_add_cd)
    val fadeSpec = tween<Float>(
        durationMillis = token.motion.duration.standard,
        easing = token.motion.easing.standard,
    )
    val sizeSpec = tween<IntSize>(
        durationMillis = token.motion.duration.standard,
        easing = token.motion.easing.standard,
    )
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = token.spacing.md),
    ) {
        stickyHeader(key = "exercise_library_search") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = token.spacing.xs,
                        bottom = token.spacing.xs,
                        start = token.spacing.md,
                        end = token.spacing.md,
                    ),
            ) {
                ExerciseSearchBar(
                    query = state.searchQuery,
                    onQueryChange = onQueryChange,
                    onSearchFocusChange = { isSearchFocused.value = it },
                )
                AnimatedVisibility(
                    visible = showSuggestionLayer,
                    enter = fadeIn(fadeSpec) + expandVertically(animationSpec = sizeSpec),
                    exit = fadeOut(fadeSpec) + shrinkVertically(animationSpec = sizeSpec),
                ) {
                    ExerciseSearchSuggestionChips(
                        libraryState = state,
                        onQuickChipSelect = onQuickChipSelect,
                    )
                }
            }
        }
        if (state.sections.isEmpty()) {
            item(key = "exercise_library_empty") {
                Box(
                    modifier = Modifier
                        .fillParentMaxHeight()
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    ExerciseLibraryEmptyState()
                }
            }
        } else {
            state.sections.forEach { section ->
                stickyHeader(key = "${section.bodyRegion.name}_header") {
                    val regionLabel = stringResource(ExerciseDisplayResources.bodyRegionResId(section.bodyRegion))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(token.colors.background)
                            .padding(
                                horizontal = token.spacing.md,
                                vertical = token.spacing.xs,
                            ),
                    ) {
                        GSectionHeader(title = regionLabel)
                    }
                }
                item(key = "${section.bodyRegion.name}_cards") {
                    ExerciseSection(
                        modifier = Modifier.padding(horizontal = token.spacing.md),
                        section = section,
                        onExerciseClick = { uiModel -> onExerciseClick(uiModel.id) },
                        onQuickAdd = onQuickAdd,
                        quickAddContentDescription = quickAddCd,
                    )
                }
            }
        }
    }
}
