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
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.CartSetField
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryActions
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel.ExerciseLibraryViewModel
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
        viewModel.startSelectionBarEditFromScheduleRow(rowId)
    }
    val actions = remember(viewModel, onNavigateToWorkoutCalendar, onNavigateToSessionBookingEditor) {
        ExerciseLibraryActions(
            onQueryChange = viewModel::updateSearchQuery,
            onExerciseClick = { exerciseId ->
                viewModel.dismissAddExerciseSuccess()
                viewModel.selectExerciseForDetail(exerciseId)
            },
            onLibraryListToggle = viewModel::toggleExerciseInCartFromList,
            onDetailAddToCart = viewModel::ensureInCartAndFocusFromDetail,
            onSelectCartItem = viewModel::setActiveCartExercise,
            onRemoveCartItem = viewModel::removeFromCart,
            onClearCart = viewModel::clearAll,
            onActiveDraftChange = viewModel::updateActiveDraft,
            onAddToSession = {
                viewModel.dismissAddExerciseSuccess()
                onNavigateToSessionBookingEditor()
            },
            onNavigateToSessionBookingEditor = {
                viewModel.dismissAddExerciseSuccess()
                onNavigateToSessionBookingEditor()
            },
            onDismissSessionBooking = {
                viewModel.dismissAddExerciseSuccess()
                viewModel.dismissSessionBooking()
            },
            onBookingDateSelected = viewModel::onBookingDateSelected,
            onBookingLocationSelected = viewModel::onBookingLocationSelected,
            onBookingSlotToggled = viewModel::onBookingSlotToggled,
            onBookingClearTimeSelection = viewModel::onBookingClearTimeSelection,
            onConfirmSessionBooking = viewModel::confirmSessionBooking,
            onLongSessionEdit = viewModel::onLongSessionEdit,
            onLongSessionProceedAnyway = viewModel::onLongSessionProceedAnyway,
            onClearExerciseDetail = viewModel::clearExerciseDetail,
            onDismissAddExerciseSuccess = viewModel::dismissAddExerciseSuccess,
            onNavigateToWorkoutCalendar = {
                viewModel.dismissAddExerciseSuccess()
                onNavigateToWorkoutCalendar()
            },
            onStepCartField = viewModel::stepCartField,
            onAddCartSetRow = { exerciseId -> viewModel.stepCartField(exerciseId, 0, CartSetField.SETS, 1) },
            onSetCartFieldManual = viewModel::setCartFieldManual,
            onToggleCartExpanded = viewModel::toggleCartExpanded,
            onFocusStripQuadrantTap = viewModel::onFocusStripQuadrantTapped,
            onConfirmSelectionBarEdit = viewModel::onConfirmSelectionBarEdit,
            onCancelSelectionBarEdit = viewModel::onCancelSelectionBarEdit,
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
                    actions = actions,
                )
                data.libraryList.selectedExerciseForDetail?.let { detail ->
                    ExerciseDetailDialog(
                        detail = detail,
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
                    actions = actions,
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
                    onClick = actions.onNavigateToWorkoutCalendar,
                    modifier = Modifier.fillMaxWidth(),
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(token.spacing.md))
                    ExerciseLibraryFocusMusclesSection(
                        imageNames = state.focusMusclesStrip,
                        onQuadrantClick = actions.onFocusStripQuadrantTap,
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
                                    onNavigateDetail = actions.onExerciseClick,
                                    onToggleSelection = actions.onLibraryListToggle,
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
            if (state.chrome.isSelectionBarEditMode) {
                ExerciseLibrarySelectionBar(
                    libraryState = state,
                    actions = actions,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                ExerciseLibraryCartBar(
                    libraryState = state,
                    actions = actions,
                    cartItems = cartItems,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
