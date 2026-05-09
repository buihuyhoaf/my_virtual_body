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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import android.util.Log
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibraryCartBar
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibraryEmptyState
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibrarySearchLayer
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibrarySelectionBar
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseSection
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBar
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBarBackIcon
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryChromeMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel.ExerciseLibraryViewModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.wiring.ExerciseCatalogActions
import com.hoabui.virtualbody3d.ui.exerciselibrary.wiring.WorkoutBuilderActions
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

private object ExerciseLibraryListContentTypes {
    const val RegionHeader = "exercise_library_region_header"
    const val RegionRow = "exercise_library_region_row"
    const val Empty = "exercise_library_empty"
}

@Composable
fun ExerciseLibraryScreen(
    modifier: Modifier = Modifier,
    onNavigateToWorkoutCalendar: () -> Unit,
    onBack: () -> Unit,
    scheduleRowIdToEdit: Long? = null,
    initialExerciseCategory: String? = null,
    initialBodyRegions: List<String>? = null,
    viewModel: ExerciseLibraryViewModel = hiltViewModel(),
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val chromeMode by viewModel.chromeMode.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            Log.d("ExerciseLibrary", "Library screen collect event: $event")
        }
    }

    val filterAppliedKey =
        "${initialExerciseCategory.orEmpty()}|${initialBodyRegions?.sorted()?.joinToString()}"
    LaunchedEffect(filterAppliedKey, initialExerciseCategory, initialBodyRegions) {
        if (!initialBodyRegions.isNullOrEmpty()) {
            val regions = initialBodyRegions.map { BodyRegion.valueOf(it) }.toImmutableSet()
            viewModel.setInitialBodyRegionFilter(regions)
        } else if (initialExerciseCategory != null) {
            viewModel.setInitialExerciseCategoryFilter(
                ExerciseCategory.valueOf(initialExerciseCategory),
            )
        }
    }
    LaunchedEffect(scheduleRowIdToEdit) {
        val rowId = scheduleRowIdToEdit ?: return@LaunchedEffect
        viewModel.startSelectionBarEditFromScheduleRow(rowId)
    }
    val catalogActions = remember(viewModel) {
        ExerciseCatalogActions(
            onQueryChange = { viewModel.setSearchQuery(it) },
            onCardTap = { viewModel.toggleCardSelection(it) },
        )
    }
    val workoutBuilderActions = remember(viewModel, onNavigateToWorkoutCalendar) {
        WorkoutBuilderActions(
            onSelectCartItem = { viewModel.selectCartItem(it) },
            onRemoveCartItem = { viewModel.removeCartItem(it) },
            onClearCart = { viewModel.clearCart() },
            onAddToSession = {
                onNavigateToWorkoutCalendar()
            },
            onNavigateToWorkoutCalendar = {
                onNavigateToWorkoutCalendar()
            },
            onStepCartField = { exerciseId, setIndex, field, delta ->
                viewModel.stepCartField(
                    exerciseId = exerciseId,
                    setIndex = setIndex,
                    field = field,
                    delta = delta,
                )
            },
            onSetCartFieldManual = { exerciseId, setIndex, field, value ->
                viewModel.setCartFieldManual(
                    exerciseId = exerciseId,
                    setIndex = setIndex,
                    field = field,
                    value = value,
                )
            },
            onToggleCartExpanded = { viewModel.toggleCartExpanded() },
            onConfirmSelectionBarEdit = { viewModel.confirmSelectionBarEdit() },
            onCancelSelectionBarEdit = { viewModel.cancelSelectionBarEdit() },
        )
    }
    UiStateContent(
        state = screenState,
        modifier = modifier,
        successContent = { mod, data ->
            GScaffold(
                modifier = mod,
                contentWindowInsets = WindowInsets(0),
                topBar = {
                    GTopBar(
                        title = stringResource(R.string.exercise_library_title),
                        windowInsets = WindowInsets(0),
                        navigationIcon = { GTopBarBackIcon(onBack = onBack) }
                    )
                }
            ) { padding ->
                ExerciseLibraryScreenContent(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = data,
                    chromeMode = chromeMode,
                    catalogActions = catalogActions,
                    workoutBuilderActions = workoutBuilderActions,
                )
            }
        },
    )
}

@Composable
fun ExerciseLibraryScreenContent(
    modifier: Modifier = Modifier,
    state: ExerciseLibraryUiState,
    chromeMode: ExerciseLibraryChromeMode,
    catalogActions: ExerciseCatalogActions,
    workoutBuilderActions: WorkoutBuilderActions,
) {
    val token = GymTheme.token
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
    val cartVisible = state.itemDrafts.isNotEmpty()
    val cartCollapsedInset = token.bodyAnalysis.exerciseLibrarySelectionBarCollapsedListBottomInset
    val listBottomPadding = if (cartVisible) cartCollapsedInset else token.spacing.none
    val cartItems = remember(state.libraryList.sections, state.draftOrder) {
        val byId = state.libraryList.sections.asSequence().flatMap { it.items.asSequence() }
            .associateBy { it.id }
        state.draftOrder.mapNotNull { byId[it] }
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
                                    onCardTap = catalogActions.onCardTap,
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
            if (chromeMode is ExerciseLibraryChromeMode.EditingScheduleRow) {
                ExerciseLibrarySelectionBar(
                    libraryState = state,
                    chromeMode = chromeMode,
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
