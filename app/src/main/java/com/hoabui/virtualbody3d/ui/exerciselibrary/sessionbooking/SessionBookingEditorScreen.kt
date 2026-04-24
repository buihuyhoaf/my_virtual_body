package com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.rememberActiveExerciseInfoFromLibraryState
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.rememberCartItemsFromLibraryState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.CartSetField
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.isCartDraftValidForSessionConfirm
import com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel.ExerciseLibraryViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalTime

@Composable
fun SessionBookingEditorScreen(
    onBack: () -> Unit,
    onNavigateToWorkoutCalendar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExerciseLibraryViewModel = hiltViewModel(),
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val data: ExerciseLibraryUiState? = (screenState as? UiState.Success)?.data

    LaunchedEffect(data?.sessionBooking?.input) {
        if (data != null && data.sessionBooking.input == null) {
            viewModel.openSessionBooking()
        }
    }

    BackHandler {
        viewModel.dismissSessionBooking()
        onBack()
    }

    when (val s = screenState) {
        is UiState.Success -> SessionBookingEditorScreenContent(
            modifier = modifier,
            data = s.data,
            onBack = {
                viewModel.dismissSessionBooking()
                onBack()
            },
            onNavigateToWorkoutCalendar = onNavigateToWorkoutCalendar,
            onDismissAddExerciseSuccess = viewModel::dismissAddExerciseSuccess,
            onConfirmSessionBooking = viewModel::confirmSessionBooking,
            onDismissSessionBooking = viewModel::dismissSessionBooking,
            onLongSessionEdit = viewModel::onLongSessionEdit,
            onLongSessionProceedAnyway = viewModel::onLongSessionProceedAnyway,
            onBookingDateSelected = viewModel::onBookingDateSelected,
            onBookingLocationSelected = viewModel::onBookingLocationSelected,
            onBookingSlotToggled = viewModel::onBookingSlotToggled,
            onBookingClearTimeSelection = viewModel::onBookingClearTimeSelection,
            onSelectCartItem = viewModel::setActiveCartExercise,
            onRemoveCartItem = viewModel::removeFromCart,
            onClearCart = viewModel::clearAll,
            onStepCartField = viewModel::stepCartField,
            onSetCartFieldManual = viewModel::setCartFieldManual,
        )

        else -> Box(modifier = modifier.fillMaxSize())
    }
}

@Composable
private fun SessionBookingEditorScreenContent(
    data: ExerciseLibraryUiState,
    onBack: () -> Unit,
    onNavigateToWorkoutCalendar: () -> Unit,
    onDismissAddExerciseSuccess: () -> Unit,
    onConfirmSessionBooking: () -> Unit,
    onDismissSessionBooking: () -> Unit,
    onLongSessionEdit: () -> Unit,
    onLongSessionProceedAnyway: () -> Unit,
    onBookingDateSelected: (Long) -> Unit,
    onBookingLocationSelected: (String) -> Unit,
    onBookingSlotToggled: (LocalTime) -> Unit,
    onBookingClearTimeSelection: () -> Unit,
    onSelectCartItem: (String) -> Unit,
    onRemoveCartItem: (String) -> Unit,
    onClearCart: () -> Unit,
    onStepCartField: (String, Int, CartSetField, Int) -> Unit,
    onSetCartFieldManual: (String, Int, CartSetField, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val cartItems = rememberCartItemsFromLibraryState(data)
    val activeExerciseInfo = rememberActiveExerciseInfoFromLibraryState(data, cartItems)
    val scroll = rememberScrollState()
    val bookingUi = data.sessionBooking.uiModel
    val input = data.sessionBooking.input
    val confirmEnabled = data.isCartDraftValidForSessionConfirm() &&
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
                            onDismissSessionBooking()
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
                        onDismissSessionBooking()
                        onBack()
                    },
                    modifier = Modifier.weight(1f),
                    variant = GButtonVariant.Outlined,
                )
                GButton(
                    text = stringResource(R.string.exercise_library_booking_confirm),
                    onClick = onConfirmSessionBooking,
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
                    activeExerciseId = data.cart.activeExerciseId,
                    onSelectCartItem = onSelectCartItem,
                    onRemoveCartItem = onRemoveCartItem,
                    onClearAll = onClearCart,
                    showClearAllButton = false,
                    showRemoveOnThumbnail = false,
                    modifier = Modifier.fillMaxWidth(),
                )
                ActiveExerciseDraftEditorOrganism(
                    activeExerciseInfo = activeExerciseInfo,
                    onStepCartField = onStepCartField,
                    onSetCartFieldManual = onSetCartFieldManual,
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
                        onDateMillisSelected = onBookingDateSelected,
                        onLocationSelected = onBookingLocationSelected,
                        onSlotToggled = onBookingSlotToggled,
                        onClearTimeSelection = onBookingClearTimeSelection,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (input?.pendingLongSessionWarning == true) {
                LongSessionWarningDialog(
                    onDismissRequest = onLongSessionEdit,
                    onEditSession = onLongSessionEdit,
                    onProceedAnyway = onLongSessionProceedAnyway,
                )
            }
            data.chrome.addExerciseSuccess?.let { summary ->
                AddExerciseSuccessDialog(
                    summary = summary,
                    onDismiss = {
                        onDismissAddExerciseSuccess()
                        onBack()
                    },
                    onViewWorkoutPlan = {
                        onDismissAddExerciseSuccess()
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
