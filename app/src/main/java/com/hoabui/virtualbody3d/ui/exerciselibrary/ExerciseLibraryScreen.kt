package com.hoabui.virtualbody3d.ui.exerciselibrary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseDetailDialog
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibraryEmptyState
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseSearchBar
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseSearchSuggestionChips
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibrarySelectionBar
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseSection
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel.ExerciseLibraryViewModel
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens
import java.time.LocalTime
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

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
            val dataRef = rememberUpdatedState(data)
            val addToWorkoutRef = rememberUpdatedState(onAddToWorkout)
            val onQuickAdd = remember(viewModel) {
                { exerciseId: String -> viewModel.onQuickAdd(exerciseId) }
            }
            GScaffold(modifier = mod) {
                ExerciseLibraryScreenContent(
                    modifier = Modifier.fillMaxSize(),
                    state = data,
                    onQueryChange = viewModel::updateSearchQuery,
                    onQuickChipSelect = viewModel::selectQuickChip,
                    onExerciseClick = viewModel::selectExerciseForDetail,
                    onQuickAdd = onQuickAdd,
                    onSelectCartItem = viewModel::setActiveCartExercise,
                    onClearCart = viewModel::clearAll,
                    onCartDateSelected = viewModel::updateCartDate,
                    onCartTimeSelected = viewModel::updateCartTime,
                    onActiveDraftChange = viewModel::updateActiveDraft,
                    onConfirmCart = viewModel::confirmCartToWorkout,
                )
                data.selectedExerciseForDetail?.let { exercise ->
                    val onDetailAdd = remember(exercise.id, viewModel) {
                        { ex: Exercise ->
                            val d = dataRef.value
                            val wasInCart = d.itemDrafts.containsKey(ex.id)
                            viewModel.onQuickAdd(ex.id)
                            viewModel.clearExerciseDetail()
                            if (!wasInCart) addToWorkoutRef.value(ex.id)
                        }
                    }
                    ExerciseDetailDialog(
                        exercise = exercise,
                        onAddClick = onDetailAdd,
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
    onSelectCartItem: (String) -> Unit = {},
    onClearCart: () -> Unit = {},
    onCartDateSelected: (Long) -> Unit = {},
    onCartTimeSelected: (LocalTime) -> Unit = {},
    onActiveDraftChange: (sets: String, reps: String) -> Unit = { _, _ -> },
    onConfirmCart: () -> Unit = {},
) {
    val isSearchFocused = remember { mutableStateOf(false) }
    val showSuggestionLayer = isSearchFocused.value || state.searchQuery.isNotEmpty()
    val token = GymTheme.token
    val quickAddCd = stringResource(R.string.exercise_quick_add_cd)
    val fadeSpec = tween<Float>(
        durationMillis = token.motion.duration.standard,
        easing = token.motion.easing.standard,
    )
    val slideSpec = tween<IntOffset>(
        durationMillis = token.motion.duration.standard,
        easing = token.motion.easing.standard,
    )
    val lazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val stickyHeaderScrim = remember(token.colors.background) {
        token.colors.background.copy(
            alpha = PrimitiveAlphaTokens.STICKY_HEADER_SCRIM,
        )
    }
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
    }
    val onSearchFocusChanged = remember {
        { focused: Boolean -> isSearchFocused.value = focused }
    }
    val cartVisible = state.itemDrafts.isNotEmpty()
    val barMinHeight = token.bodyAnalysis.exerciseLibrarySelectionBarMinHeight
    val listBottomPadding =
        if (cartVisible) barMinHeight else token.spacing.md
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = listBottomPadding),
        ) {
        stickyHeader(key = "exercise_library_search") {
            Box(
                modifier = Modifier
                    .heightIn(min = token.bodyAnalysis.exerciseLibraryStickySearchHeaderMinHeight)
                    .fillMaxWidth()
                    .padding(
                        horizontal = token.spacing.md,
                        vertical = token.spacing.xs,
                    ),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ExerciseSearchBar(
                        query = state.searchQuery,
                        onQueryChange = onQueryChange,
                        onSearchFocusChange = onSearchFocusChanged,
                    )
                    ExerciseLibrarySuggestionLayer(
                        showSuggestionLayer = showSuggestionLayer,
                        fadeSpec = fadeSpec,
                        slideSpec = slideSpec,
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
                    ExerciseLibraryEmptyState(
                        modifier = Modifier
                            .fillParentMaxHeight()
                            .fillMaxWidth(),
                    )
                }
            }
        } else {
            state.sections.forEach { section ->
                stickyHeader(key = "${section.bodyRegion.name}_header") {
                    val regionLabel = stringResource(ExerciseDisplayResources.bodyRegionResId(section.bodyRegion))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(stickyHeaderScrim)
                            .padding(
                                horizontal = token.spacing.md,
                                vertical = token.spacing.xxs,
                            ),
                    ) {
                        GSectionHeader(title = regionLabel)
                    }
                }
                item(
                    key = "${section.bodyRegion.name}_cards",
                    contentType = "exercise_section_row",
                ) {
                    ExerciseSection(
                        modifier = Modifier
                            .padding(horizontal = token.spacing.md),
                        section = section,
                        onExerciseClick = onExerciseClick,
                        onQuickAdd = onQuickAdd,
                        quickAddContentDescription = quickAddCd,
                    )
                }
            }
        }
        }
        if (cartVisible) {
            ExerciseLibrarySelectionBar(
                libraryState = state,
                onSelectCartItem = onSelectCartItem,
                onClearAll = onClearCart,
                onCartDateSelected = onCartDateSelected,
                onCartTimeSelected = onCartTimeSelected,
                onActiveDraftChange = onActiveDraftChange,
                onConfirm = onConfirmCart,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ExerciseLibrarySuggestionLayer(
    showSuggestionLayer: Boolean,
    fadeSpec: FiniteAnimationSpec<Float>,
    slideSpec: FiniteAnimationSpec<IntOffset>,
    libraryState: ExerciseLibraryUiState,
    onQuickChipSelect: (ExerciseLibraryQuickChip?) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        AnimatedVisibility(
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
                libraryState = libraryState,
                onQuickChipSelect = onQuickChipSelect,
            )
        }
    }
}
