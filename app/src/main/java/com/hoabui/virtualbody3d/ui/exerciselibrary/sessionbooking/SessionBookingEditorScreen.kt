package com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.base.UiState
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.divider.GDivider
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBar
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBarBackIcon
import com.hoabui.virtualbody3d.ui.common_ui.organism.exerciselibrary.ActiveExerciseDraftEditorOrganism
import com.hoabui.virtualbody3d.ui.common_ui.organism.exerciselibrary.SessionBookingEditorOrganism
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.AddExerciseSuccessDialog
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.CartThumbnailRow
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.LongSessionWarningDialog
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.AddExerciseSuccessSummary
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.rememberActiveExerciseInfoFromLibraryState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.rememberCartItemsFromLibraryState
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.isCartDraftValidForSessionConfirm
import com.hoabui.virtualbody3d.ui.exerciselibrary.wiring.SessionBookingActions
import com.hoabui.virtualbody3d.ui.exerciselibrary.wiring.WorkoutBuilderActions
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun SessionBookingEditorScreen(
    onBack: () -> Unit,
    onNavigateToWorkoutCalendar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionBookingEditorViewModel = hiltViewModel(),
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    var addSuccessSummary by remember { mutableStateOf<AddExerciseSuccessSummary?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            Log.d("SessionBooking", "Booking screen collect event: $event")
            when (event) {
                is SessionBookingEvent.ShowAddExerciseSuccess -> {
                    addSuccessSummary = event.summary
                }
                else -> {}
            }
        }
    }

    val data: SessionBookingEditorPresentationState? = (screenState as? UiState.Success)?.data

    LaunchedEffect(data?.sessionBookingInput) {
        if (data != null && data.sessionBookingInput == null) {
            viewModel.openSessionBooking()
        }
    }

    BackHandler {
        viewModel.dismissSessionBooking()
        onBack()
    }

    val sessionBookingActions = remember(viewModel) {
        SessionBookingActions(
            onOpenSessionBooking = { viewModel.openSessionBooking() },
            onDismissSessionBooking = { viewModel.dismissSessionBooking() },
            onBookingDateSelected = {
                viewModel.onBookingDateSelected(it)
            },
            onBookingLocationSelected = {
                viewModel.onBookingLocationSelected(it)
            },
            onBookingSlotToggled = {
                viewModel.onBookingSlotToggled(it)
            },
            onBookingClearTimeSelection = {
                viewModel.onBookingClearTimeSelection()
            },
            onConfirmSessionBooking = {
                viewModel.confirmSessionBooking()
            },
            onLongSessionEdit = { viewModel.onLongSessionEdit() },
            onLongSessionProceedAnyway = {
                viewModel.onLongSessionProceedAnyway()
            },
        )
    }
    val workoutBuilderActions = remember(viewModel) {
        WorkoutBuilderActions(
            onSelectCartItem = {
                viewModel.selectCartItem(it)
            },
            onRemoveCartItem = {
                viewModel.removeCartItem(it)
            },
            onClearCart = { viewModel.clearCart() },
            onNavigateToSessionBooking = {},
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
            onToggleCartExpanded = {},
            onConfirmSelectionBarEdit = {},
            onCancelSelectionBarEdit = {},
        )
    }

    when (val s = screenState) {
        is UiState.Success -> SessionBookingEditorScreenContent(
            modifier = modifier,
            data = s.data,
            addSuccessSummary = addSuccessSummary,
            onDismissAddSuccess = { addSuccessSummary = null },
            onBack = {
                viewModel.dismissSessionBooking()
                onBack()
            },
            onNavigateToWorkoutCalendar = onNavigateToWorkoutCalendar,
            sessionBookingActions = sessionBookingActions,
            workoutBuilderActions = workoutBuilderActions,
        )

        else -> Box(modifier = modifier.fillMaxSize())
    }
}

@Composable
private fun SessionBookingEditorScreenContent(
    data: SessionBookingEditorPresentationState,
    addSuccessSummary: AddExerciseSuccessSummary?,
    onDismissAddSuccess: () -> Unit,
    onBack: () -> Unit,
    onNavigateToWorkoutCalendar: () -> Unit,
    sessionBookingActions: SessionBookingActions,
    workoutBuilderActions: WorkoutBuilderActions,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val library = data.libraryUi
    val cartItems = rememberCartItemsFromLibraryState(library)
    val activeExerciseInfo = rememberActiveExerciseInfoFromLibraryState(library, cartItems)
    val scroll = rememberScrollState()
    val bookingUi = data.sessionBookingUiModel
    val input = data.sessionBookingInput
    val confirmEnabled = library.isCartDraftValidForSessionConfirm() &&
        (bookingUi?.isBookingConfirmEnabled == true) &&
        (input?.isConfirming != true)

    GScaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            GTopBar(
                title = stringResource(R.string.exercise_library_booking_title),
                windowInsets = WindowInsets(0),
                navigationIcon = {
                    GTopBarBackIcon(
                        onBack = {
                            sessionBookingActions.onDismissSessionBooking()
                            onBack()
                        },
                    )
                },
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = token.spacing.md, vertical = token.spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.sm),
            ) {
                GButton(
                    text = stringResource(R.string.exercise_library_booking_cancel),
                    onClick = {
                        sessionBookingActions.onDismissSessionBooking()
                        onBack()
                    },
                    modifier = Modifier.weight(1f),
                    variant = GButtonVariant.Outlined,
                )
                GButton(
                    text = stringResource(R.string.exercise_library_booking_confirm),
                    onClick = sessionBookingActions.onConfirmSessionBooking,
                    modifier = Modifier.weight(1f),
                    enabled = confirmEnabled,
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scroll)
                    .padding(horizontal = token.spacing.md),
                verticalArrangement = Arrangement.spacedBy(token.spacing.md),
            ) {
                CartThumbnailRow(
                    cartItems = cartItems,
                    activeExerciseId = library.activeExerciseId,
                    onSelectCartItem = workoutBuilderActions.onSelectCartItem,
                    onRemoveCartItem = workoutBuilderActions.onRemoveCartItem,
                    onClearAll = workoutBuilderActions.onClearCart,
                    showClearAllButton = false,
                    showRemoveOnThumbnail = false,
                    modifier = Modifier.fillMaxWidth(),
                )
                ActiveExerciseDraftEditorOrganism(
                    activeExerciseInfo = activeExerciseInfo,
                    onStepCartField = workoutBuilderActions.onStepCartField,
                    onSetCartFieldManual = workoutBuilderActions.onSetCartFieldManual,
                    isSelectionBarContext = false,
                    modifier = Modifier.fillMaxWidth(),
                )
                GDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = token.colors.borderSubtle,
                )
                if (bookingUi != null) {
                    SessionBookingEditorOrganism(
                        booking = bookingUi,
                        showSlotConflict = data.sessionBookingWorkflowPhase is SessionBookingWorkflowPhase.SlotConflict,
                        onDateMillisSelected = sessionBookingActions.onBookingDateSelected,
                        onLocationSelected = sessionBookingActions.onBookingLocationSelected,
                        onSlotToggled = sessionBookingActions.onBookingSlotToggled,
                        onClearTimeSelection = sessionBookingActions.onBookingClearTimeSelection,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (data.sessionBookingWorkflowPhase is SessionBookingWorkflowPhase.AwaitingLongSessionAck) {
                LongSessionWarningDialog(
                    onDismissRequest = sessionBookingActions.onLongSessionEdit,
                    onEditSession = sessionBookingActions.onLongSessionEdit,
                    onProceedAnyway = sessionBookingActions.onLongSessionProceedAnyway,
                )
            }
            addSuccessSummary?.let { summary ->
                AddExerciseSuccessDialog(
                    summary = summary,
                    onDismiss = {
                        onDismissAddSuccess()
                        onBack()
                    },
                    onViewWorkoutPlan = {
                        onDismissAddSuccess()
                        onNavigateToWorkoutCalendar()
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "SessionBookingEditor — shell (Light)")
@Composable
private fun PreviewSessionBookingEditorShellLight() {
    GymTheme {
        GScaffold(
            topBar = {
                GTopBar(
                    title = stringResource(R.string.exercise_library_booking_title),
                    windowInsets = WindowInsets(0),
                    navigationIcon = { GTopBarBackIcon(onBack = {}) },
                )
            },
            bottomBar = {},
            contentWindowInsets = WindowInsets(0),
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        }
    }
}

@Preview(showBackground = true, name = "SessionBookingEditor — shell (Dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSessionBookingEditorShellDark() {
    GymTheme(darkTheme = true) {
        GScaffold(
            topBar = {
                GTopBar(
                    title = stringResource(R.string.exercise_library_booking_title),
                    windowInsets = WindowInsets(0),
                    navigationIcon = { GTopBarBackIcon(onBack = {}) },
                )
            },
            bottomBar = {},
            contentWindowInsets = WindowInsets(0),
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        }
    }
}
