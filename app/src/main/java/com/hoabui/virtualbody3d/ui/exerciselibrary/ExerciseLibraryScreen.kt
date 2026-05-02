package com.hoabui.virtualbody3d.ui.exerciselibrary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseDetailDialog
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibraryCartBar
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibraryEmptyState
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibraryFocusMusclesSection
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibraryWeeklyHeatmapCard
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibrarySearchLayer
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibrarySelectionBar
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseSection
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryChromeMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibraryIntent
import com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel.ExerciseLibraryViewModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.wiring.ExerciseCatalogActions
import com.hoabui.virtualbody3d.ui.exerciselibrary.wiring.GymMapChromeActions
import com.hoabui.virtualbody3d.ui.exerciselibrary.wiring.WorkoutBuilderActions
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

private object ExerciseLibraryListContentTypes {
    const val WeeklyHeatmap = "exercise_library_weekly_heatmap"
    const val RegionHeader = "exercise_library_region_header"
    const val RegionRow = "exercise_library_region_row"
    const val Empty = "exercise_library_empty"
}

@Composable
fun ExerciseLibraryScreen(
    modifier: Modifier = Modifier,
    onNavigateToWorkoutCalendar: () -> Unit,
    onNavigateToSessionBookingEditor: () -> Unit,
    scheduleRowIdToEdit: Long? = null,
    viewModel: ExerciseLibraryViewModel = hiltViewModel(),
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(scheduleRowIdToEdit) {
        val rowId = scheduleRowIdToEdit ?: return@LaunchedEffect
        viewModel.onEvent(ExerciseLibraryIntent.StartSelectionBarEditFromScheduleRow(rowId))
    }
    val catalogActions = remember(viewModel) {
        ExerciseCatalogActions(
            onQueryChange = { viewModel.onEvent(ExerciseLibraryIntent.SetSearchQuery(it)) },
            onExerciseClick = { viewModel.onEvent(ExerciseLibraryIntent.ExerciseClicked(it)) },
            onLibraryListToggle = { viewModel.onEvent(ExerciseLibraryIntent.LibraryListToggle(it)) },
            onDetailAddToCart = { viewModel.onEvent(ExerciseLibraryIntent.DetailAddToCart(it)) },
            onClearExerciseDetail = { viewModel.onEvent(ExerciseLibraryIntent.ClearExerciseDetail) },
        )
    }
    val workoutBuilderActions = remember(viewModel, onNavigateToSessionBookingEditor) {
        WorkoutBuilderActions(
            onSelectCartItem = { viewModel.onEvent(ExerciseLibraryIntent.SelectCartItem(it)) },
            onRemoveCartItem = { viewModel.onEvent(ExerciseLibraryIntent.RemoveCartItem(it)) },
            onClearCart = { viewModel.onEvent(ExerciseLibraryIntent.ClearCart) },
            onAddToSession = {
                viewModel.onEvent(ExerciseLibraryIntent.DismissAddExerciseSuccess)
                onNavigateToSessionBookingEditor()
            },
            onNavigateToSessionBookingEditor = {
                viewModel.onEvent(ExerciseLibraryIntent.DismissAddExerciseSuccess)
                onNavigateToSessionBookingEditor()
            },
            onStepCartField = { exerciseId, setIndex, field, delta ->
                viewModel.onEvent(
                    ExerciseLibraryIntent.StepCartField(
                        exerciseId = exerciseId,
                        setIndex = setIndex,
                        field = field,
                        delta = delta,
                    ),
                )
            },
            onSetCartFieldManual = { exerciseId, setIndex, field, value ->
                viewModel.onEvent(
                    ExerciseLibraryIntent.SetCartFieldManual(
                        exerciseId = exerciseId,
                        setIndex = setIndex,
                        field = field,
                        value = value,
                    ),
                )
            },
            onToggleCartExpanded = { viewModel.onEvent(ExerciseLibraryIntent.ToggleCartExpanded) },
            onConfirmSelectionBarEdit = { viewModel.onEvent(ExerciseLibraryIntent.ConfirmSelectionBarEdit) },
            onCancelSelectionBarEdit = { viewModel.onEvent(ExerciseLibraryIntent.CancelSelectionBarEdit) },
        )
    }
    val gymMapChromeActions = remember(viewModel, onNavigateToWorkoutCalendar) {
        GymMapChromeActions(
            onFocusStripQuadrantTap = { viewModel.onEvent(ExerciseLibraryIntent.FocusStripQuadrantTapped(it)) },
            onNavigateToWorkoutCalendar = {
                viewModel.onEvent(ExerciseLibraryIntent.DismissAddExerciseSuccess)
                onNavigateToWorkoutCalendar()
            },
        )
    }

    UiStateContent(
        state = screenState,
        modifier = modifier,
        successContent = { mod, data ->
            GScaffold(modifier = mod) {
                ExerciseLibraryScreenContent(
                    modifier = Modifier.fillMaxSize(),
                    state = data,
                    catalogActions = catalogActions,
                    workoutBuilderActions = workoutBuilderActions,
                    gymMapChromeActions = gymMapChromeActions,
                )
                data.libraryList.selectedExerciseForDetail?.let { detail ->
                    ExerciseDetailDialog(
                        detail = detail,
                        onDismiss = catalogActions.onClearExerciseDetail,
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
    catalogActions: ExerciseCatalogActions,
    workoutBuilderActions: WorkoutBuilderActions,
    gymMapChromeActions: GymMapChromeActions,
) {
    val token = GymTheme.token
    val listToggleAddCd = stringResource(R.string.exercise_library_list_toggle_add_cd)
    val listToggleRemoveCd = stringResource(R.string.exercise_library_cart_remove_item_cd)
    val fadeSpec = tween<Float>(
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
    val cartVisible = state.cart.itemDrafts.isNotEmpty()
    val cartCollapsedInset = token.bodyAnalysis.exerciseLibrarySelectionBarCollapsedListBottomInset
    val listBottomPadding = if (cartVisible) cartCollapsedInset else token.spacing.none
    val bodyTok = token.bodyAnalysis
    val cartItems = remember(state.libraryList.sections, state.cart.draftOrder) {
        val byId = state.libraryList.sections.asSequence().flatMap { it.items.asSequence() }
            .associateBy { it.id }
        state.cart.draftOrder.mapNotNull { byId[it] }
    }
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sticky search bar header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = token.spacing.md,
                        vertical = token.spacing.xs,
                    ),
            ) {
                ExerciseLibrarySearchLayer(
                    state = state,
                    actions = catalogActions,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            // Heatmap and focus-muscle quadrants (sticky)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = token.spacing.md,
                        end = token.spacing.md,
                        top = token.spacing.xs,
                        bottom = bodyTok.exerciseLibrarySearchToSummaryGap,
                    ),
            ) {
                ExerciseLibraryWeeklyHeatmapCard(
                    state = state.weeklyHeatmap,
                    onClick = gymMapChromeActions.onNavigateToWorkoutCalendar,
                    modifier = Modifier.fillMaxWidth(),
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(token.spacing.md))
                    ExerciseLibraryFocusMusclesSection(
                        imageNames = state.focusMusclesStrip,
                        onQuadrantClick = gymMapChromeActions.onFocusStripQuadrantTap,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = listBottomPadding),
                ) {
                    if (state.libraryList.sections.isEmpty()) {
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
                        state.libraryList.sections.forEach { section ->
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
                                    onNavigateDetail = catalogActions.onExerciseClick,
                                    onToggleSelection = catalogActions.onLibraryListToggle,
                                    toggleAddContentDescription = listToggleAddCd,
                                    toggleRemoveContentDescription = listToggleRemoveCd,
                                )
                            }
                        }
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
            if (state.chrome.mode is ExerciseLibraryChromeMode.EditingScheduleRow) {
                ExerciseLibrarySelectionBar(
                    libraryState = state,
                    actions = workoutBuilderActions,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                ExerciseLibraryCartBar(
                    libraryState = state,
                    actions = workoutBuilderActions,
                    cartItems = cartItems,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
