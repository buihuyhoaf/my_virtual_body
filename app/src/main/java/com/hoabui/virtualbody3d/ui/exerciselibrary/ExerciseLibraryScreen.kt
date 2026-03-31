package com.hoabui.virtualbody3d.ui.exerciselibrary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.AddExerciseSuccessDialog
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibraryWorkoutPlanFab
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseDetailDialog
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibraryEmptyState
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibrarySearchLayer
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibrarySelectionBar
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseSection
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryActions
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel.ExerciseLibraryViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

private object ExerciseLibraryListContentTypes {
    const val StickySearch = "exercise_library_sticky_search"
    const val RegionHeader = "exercise_library_region_header"
    const val RegionRow = "exercise_library_region_row"
    const val Empty = "exercise_library_empty"
}

@Composable
fun ExerciseLibraryScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onAddToWorkout: (exerciseId: String) -> Unit = {},
    viewModel: ExerciseLibraryViewModel = hiltViewModel(),
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val actions = remember(viewModel) {
        ExerciseLibraryActions(
            onQueryChange = viewModel::updateSearchQuery,
            onQuickChipSelect = viewModel::selectQuickChip,
            onExerciseClick = viewModel::selectExerciseForDetail,
            onLibraryListToggle = viewModel::toggleExerciseInCartFromList,
            onDetailAddToCart = viewModel::ensureInCartAndFocusFromDetail,
            onSelectCartItem = viewModel::setActiveCartExercise,
            onRemoveCartItem = viewModel::removeFromCart,
            onClearCart = viewModel::clearAll,
            onCartDateSelected = viewModel::updateCartDate,
            onCartTimeSelected = viewModel::updateCartTime,
            onActiveDraftChange = viewModel::updateActiveDraft,
            onConfirmCart = viewModel::confirmCartToWorkout,
            onClearExerciseDetail = viewModel::clearExerciseDetail,
            onDismissAddExerciseSuccess = viewModel::dismissAddExerciseSuccess,
            onOpenWorkoutPlan = viewModel::onWorkoutPlanFabClick,
        )
    }

    UiStateContent(
        state = screenState,
        modifier = modifier,
        successContent = { mod, data ->
            val dataRef = rememberUpdatedState(data)
            val addToWorkoutRef = rememberUpdatedState(onAddToWorkout)
            GScaffold(modifier = mod) {
                ExerciseLibraryScreenContent(
                    modifier = Modifier.fillMaxSize(),
                    state = data,
                    actions = actions,
                )
                data.addExerciseSuccess?.let { summary ->
                    AddExerciseSuccessDialog(
                        summary = summary,
                        onDismiss = actions.onDismissAddExerciseSuccess,
                        onViewWorkoutPlan = actions.onOpenWorkoutPlan,
                    )
                }
                data.selectedExerciseForDetail?.let { exercise ->
                    val onDetailAdd = remember(exercise.id, actions, addToWorkoutRef, dataRef) {
                        { ex: Exercise ->
                            val d = dataRef.value
                            val wasInCart = d.itemDrafts.containsKey(ex.id)
                            actions.onDetailAddToCart(ex.id)
                            actions.onClearExerciseDetail()
                            if (!wasInCart) addToWorkoutRef.value(ex.id)
                        }
                    }
                    ExerciseDetailDialog(
                        exercise = exercise,
                        onAddClick = onDetailAdd,
                        onDismiss = actions.onClearExerciseDetail,
                    )
                }
            }
        },
    )
}

@Composable
fun ExerciseLibraryScreenContent(
    modifier: Modifier = Modifier,
    state: ExerciseLibraryUiState,
    actions: ExerciseLibraryActions,
) {
    val isSearchFocused = remember { mutableStateOf(false) }
    val token = GymTheme.token
    val listToggleAddCd = stringResource(R.string.exercise_library_list_toggle_add_cd)
    val listToggleRemoveCd = stringResource(R.string.exercise_library_cart_remove_item_cd)
    val fadeSpec = tween<Float>(
        durationMillis = token.motion.duration.standard,
        easing = token.motion.easing.standard,
    )
    val slideSpec = tween<IntOffset>(
        durationMillis = token.motion.duration.standard,
        easing = token.motion.easing.standard,
    )
    val cartEnterSlide = tween<IntOffset>(
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
    val fabSize = token.bodyAnalysis.exerciseLibraryWorkoutPlanFabSize
    val fabListGutter = token.spacing.md
    val listBottomPadding: Dp = fabSize + fabListGutter +
        if (cartVisible) barMinHeight else token.spacing.none
    val fabBottomPadding by animateDpAsState(
        targetValue = if (cartVisible) barMinHeight + fabListGutter else fabListGutter,
        animationSpec = tween(
            durationMillis = token.motion.duration.standard,
            easing = token.motion.easing.standard,
        ),
        label = "exercise_library_fab_bottom",
    )
    Box(modifier = modifier.fillMaxSize().navigationBarsPadding()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = listBottomPadding),
        ) {
            stickyHeader(
                key = "exercise_library_search",
                contentType = ExerciseLibraryListContentTypes.StickySearch,
            ) {
                Box(
                    modifier = Modifier
                        .heightIn(min = token.bodyAnalysis.exerciseLibraryStickySearchHeaderMinHeight)
                        .fillMaxWidth()
                        .padding(
                            horizontal = token.spacing.md,
                            vertical = token.spacing.xs,
                        ),
                ) {
                    ExerciseLibrarySearchLayer(
                        state = state,
                        actions = actions,
                        isSearchFocused = isSearchFocused.value,
                        onSearchFocusChanged = onSearchFocusChanged,
                        fadeSpec = fadeSpec,
                        slideSpec = slideSpec,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (state.sections.isEmpty()) {
                item(
                    key = "exercise_library_empty",
                    contentType = ExerciseLibraryListContentTypes.Empty,
                ) {
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
                    stickyHeader(
                        key = "${section.bodyRegion.name}_header",
                        contentType = ExerciseLibraryListContentTypes.RegionHeader,
                    ) {
                        val regionLabel =
                            stringResource(ExerciseDisplayResources.bodyRegionResId(section.bodyRegion))
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
                        key = "${section.bodyRegion.name}_row",
                        contentType = ExerciseLibraryListContentTypes.RegionRow,
                    ) {
                        ExerciseSection(
                            modifier = Modifier
                                .padding(horizontal = token.spacing.md),
                            section = section,
                            onNavigateDetail = actions.onExerciseClick,
                            onToggleSelection = actions.onLibraryListToggle,
                            toggleAddContentDescription = listToggleAddCd,
                            toggleRemoveContentDescription = listToggleRemoveCd,
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = cartVisible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = fadeIn(fadeSpec) + slideInVertically(
                animationSpec = cartEnterSlide,
                initialOffsetY = { it },
            ),
            exit = fadeOut(fadeSpec) + slideOutVertically(
                animationSpec = cartEnterSlide,
                targetOffsetY = { it },
            ),
        ) {
            ExerciseLibrarySelectionBar(
                libraryState = state,
                actions = actions,
                modifier = Modifier
                    .fillMaxWidth(),
            )
        }
        AnimatedVisibility(
            visible = true,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = token.spacing.md,
                    bottom = fabBottomPadding,
                ),
            enter = fadeIn(fadeSpec) + slideInVertically(
                animationSpec = cartEnterSlide,
                initialOffsetY = { it / 2 },
            ),
            exit = fadeOut(fadeSpec) + slideOutVertically(
                animationSpec = cartEnterSlide,
                targetOffsetY = { it / 2 },
            ),
        ) {
            ExerciseLibraryWorkoutPlanFab(
                badgeCount = state.workoutPlanFabBadgeCount,
                onClick = actions.onOpenWorkoutPlan,
            )
        }
    }
}
