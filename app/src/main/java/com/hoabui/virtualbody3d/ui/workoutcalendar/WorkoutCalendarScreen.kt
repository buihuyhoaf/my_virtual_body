package com.hoabui.virtualbody3d.ui.workoutcalendar

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.base.UiState
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarSessionBlockUiModel
import com.hoabui.virtualbody3d.domain.model.calendar.toUiModel
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.dialog.GDialog
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBar
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBarBackIcon
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.common_ui.organism.workoutcalendar.WorkoutCalendarDayExerciseListOrganism
import com.hoabui.virtualbody3d.ui.common_ui.organism.workoutcalendar.WorkoutCalendarMonthGridOrganism
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.EditExerciseBar
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.wiring.WorkoutBuilderActions
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.workoutcalendar.model.buildMonthGridCells
import com.hoabui.virtualbody3d.ui.workoutcalendar.viewmodel.WorkoutCalendarContent
import com.hoabui.virtualbody3d.ui.workoutcalendar.viewmodel.WorkoutCalendarEvent
import com.hoabui.virtualbody3d.ui.workoutcalendar.viewmodel.WorkoutCalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun WorkoutCalendarScreen(
    onBack: () -> Unit,
    onNavigateToSessionBookingEditor: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WorkoutCalendarViewModel = hiltViewModel(),
) {
    val today = LocalDate.now()
    val screen by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                WorkoutCalendarEvent.None -> Unit
                WorkoutCalendarEvent.ScheduleDeletedShowUndoSnackbar -> {
                    when (
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.workout_calendar_delete_undo_snackbar),
                            actionLabel = context.getString(R.string.workout_calendar_undo),
                            duration = SnackbarDuration.Short)
                    ) {
                        SnackbarResult.ActionPerformed -> viewModel.undoLastDelete()
                        SnackbarResult.Dismissed -> viewModel.clearPendingUndoWithoutRestore()
                    }
                }
                WorkoutCalendarEvent.DeleteScheduleFailed -> {
                    snackbarHostState.showSnackbar(context.getString(R.string.workout_calendar_delete_failed))
                }
                is WorkoutCalendarEvent.TransientMessage ->
                    snackbarHostState.showSnackbar(event.message)
            }
        }
    }
    UiStateContent(
        state = screen,
        modifier = Modifier
            .fillMaxSize(),
        successContent = { mod, data ->
            GScaffold(
                modifier = modifier,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                contentWindowInsets = WindowInsets(0),
                topBar = {
                    GTopBar(
                        title = stringResource(R.string.workout_calendar_title),
                        windowInsets = WindowInsets(0),
                        navigationIcon = { GTopBarBackIcon(onBack = onBack) }
                    )
                }
            ) { padding ->
                WorkoutCalendarSuccessContent(
                    modifier = mod,
                    data = data,
                    today = today,
                    viewModel = viewModel,
                    onNavigateToSessionBookingEditor = onNavigateToSessionBookingEditor,
                )
            }

        },
    )

}

@Composable
private fun WorkoutCalendarSuccessContent(
    modifier: Modifier,
    data: WorkoutCalendarContent,
    today: LocalDate,
    viewModel: WorkoutCalendarViewModel,
    onNavigateToSessionBookingEditor: () -> Unit,
) {
    val cal = GymTheme.token.workoutCalendar
    val token = GymTheme.token
    val locale = LocalConfiguration.current.locales.get(0) ?: Locale.getDefault()
    val fullDateFormat = remember(locale) {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)
    }
    val cells = buildMonthGridCells(
        yearMonth = data.visibleYearMonth,
        selected = data.selectedDate,
        today = today,
        summaries = data.summariesByEpochDay,
    )

    // Map session blocks to UI models
    val sessionBlockUiModels: List<WorkoutCalendarSessionBlockUiModel> =
        data.sessionBlocks.map { block -> block.toUiModel() }

    val deleteDialog by viewModel.deleteDialog.collectAsStateWithLifecycle()
    val openSwipeRowId by viewModel.openSwipeRowId.collectAsStateWithLifecycle()
    val pendingSwipeCloseRowId by viewModel.pendingSwipeCloseRowId.collectAsStateWithLifecycle()
    val swipeHintSeen by viewModel.swipeHintSeen.collectAsStateWithLifecycle()

    val selectionBarActions =
        remember(viewModel, onNavigateToSessionBookingEditor) {
            WorkoutBuilderActions(
                onSelectCartItem = {
                    viewModel.selectCartItem(it)
                },
                onRemoveCartItem = {
                    viewModel.removeCartItem(it)
                },
                onClearCart = { viewModel.clearCart() },
                onNavigateToSessionBooking = {
                    onNavigateToSessionBookingEditor()
                },
                onNavigateToWorkoutCalendar = {},
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
                onToggleCartExpanded = {
                    viewModel.toggleCartExpanded()
                },
                onConfirmSelectionBarEdit = {
                    viewModel.confirmSelectionBarEdit()
                },
                onCancelSelectionBarEdit = {
                    viewModel.cancelSelectionBarEdit()
                },
            )
        }

    val fadeSpec = tween<Float>(
        durationMillis = token.motion.duration.standard,
        easing = token.motion.easing.standard,
    )
    val cartEnterSlide = tween<IntOffset>(
        durationMillis = token.motion.duration.standard,
        easing = token.motion.easing.standard,
    )

    BackHandler(
        onBack = {
            viewModel.cancelSelectionBarEdit()
        },
    )

    deleteDialog?.let { dialog ->
        val dateLabel = data.selectedDate.format(fullDateFormat)
        GDialog(
            onDismissRequest = viewModel::onDeleteDialogDismiss,
            title = stringResource(R.string.workout_calendar_delete_confirm_title),
            descriptionContent = {
                GText(
                    text = stringResource(
                        R.string.workout_calendar_delete_confirm_message,
                        dialog.exerciseName,
                        dateLabel,
                    ),
                    style = token.typography.bodyMedium,
                    color = token.colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
            },
            buttons = {
                GButton(
                    text = stringResource(R.string.workout_calendar_delete_cancel),
                    onClick = viewModel::onDeleteDialogDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    variant = GButtonVariant.Outlined,
                )
                GButton(
                    text = stringResource(R.string.workout_calendar_delete_confirm),
                    onClick = viewModel::onDeleteConfirmed,
                    modifier = Modifier.fillMaxWidth(),
                    variant = GButtonVariant.Ghost,
                    contentColor = token.colors.error,
                )
            },
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = cal.screenHorizontalPadding)
            .padding(top = cal.screenVerticalPadding),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            WorkoutCalendarMonthGridOrganism(
                yearMonth = data.visibleYearMonth,
                cells = cells,
                onPreviousMonth = {
                    viewModel.onVisibleMonthChanged(data.visibleYearMonth.minusMonths(1))
                },
                onNextMonth = {
                    viewModel.onVisibleMonthChanged(data.visibleYearMonth.plusMonths(1))
                },
                onDayClick = viewModel::onDaySelected,
            )
            WorkoutCalendarDayExerciseListOrganism(
                selectedDate = data.selectedDate,
                dailyTotalCaloriesKcal = data.dailyTotalCaloriesKcal,
                dailyCaloriesVisualLevel = data.dailyCaloriesVisualLevel,
                sessionBlocks = sessionBlockUiModels,
                listContentBottomInset = listBottomPadding,
                openSwipeRowId = openSwipeRowId,
                pendingSwipeCloseRowId = pendingSwipeCloseRowId,
                playSwipeHintNudge = sessionBlockUiModels.flatMap { it.exercises }.isNotEmpty() && !swipeHintSeen,
                onSwipeRowOpened = viewModel::onSwipeRowOpened,
                onSwipeRowSettledClosed = viewModel::onSwipeRowSettledClosed,
                onConsumePendingSwipeClose = viewModel::consumePendingSwipeCloseRow,
                onDeleteAffordanceClick = viewModel::onDeleteAffordanceClicked,
                onEditAffordanceClick = { rowId, _ ->
                    viewModel.onEditAffordanceClicked(rowId)
                    viewModel.startSelectionBarEditFromScheduleRow(rowId)
                },
                onSwipeHintConsumed = viewModel::markSwipeHintSeen,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .weight(1f),
            )
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
            EditExerciseBar(
                libraryState = libraryData,
                isSelectionBarScheduleEditActive = isSelectionBarScheduleEditActive,
                selectionBarMeasurementModeFallback = selectionBarMeasurementModeFallback,
                actions = selectionBarActions,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
