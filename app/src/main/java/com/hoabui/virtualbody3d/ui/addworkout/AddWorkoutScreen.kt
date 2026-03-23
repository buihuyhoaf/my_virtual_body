package com.hoabui.virtualbody3d.ui.addworkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import com.hoabui.virtualbody3d.ui.addworkout.components.BottomActionButtons
import com.hoabui.virtualbody3d.ui.addworkout.components.ConfirmWorkoutDialog
import com.hoabui.virtualbody3d.ui.addworkout.components.ExerciseHeader
import com.hoabui.virtualbody3d.ui.addworkout.components.NotesField
import com.hoabui.virtualbody3d.ui.addworkout.components.ScheduleSection
import com.hoabui.virtualbody3d.ui.addworkout.components.SuccessOverlay
import com.hoabui.virtualbody3d.ui.addworkout.components.WorkoutSetupSection
import com.hoabui.virtualbody3d.ui.addworkout.state.AddWorkoutUiState
import com.hoabui.virtualbody3d.ui.addworkout.viewmodel.AddWorkoutViewModel
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBar
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBarBackIcon
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import kotlinx.coroutines.delay

@Composable
fun AddWorkoutScreen(
    modifier: Modifier = Modifier,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AddWorkoutViewModel = hiltViewModel()
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val events = viewModel.events

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is AddWorkoutEvent.Saved -> onSaved()
                is AddWorkoutEvent.Cancel -> onCancel()
            }
        }
    }

    UiStateContent(
        state = screenState,
        modifier = modifier.fillMaxSize(),
        successContent = { mod, data ->
            Box(modifier = mod.fillMaxSize()) {
                AddWorkoutContent(
                    modifier = Modifier.fillMaxSize(),
                    data = data,
                    onCancel = viewModel::onCancel,
                    onAddWorkout = viewModel::onAddWorkout,
                    onDateChange = viewModel::updateDate,
                    onTimeChange = viewModel::updateTime,
                    onSetsChange = viewModel::updateSets,
                    onRepsChange = viewModel::updateReps,
                    onWeightChange = viewModel::updateWeight,
                    onRestChange = viewModel::updateRestSeconds,
                    onNotesChange = viewModel::updateNotes
                )
                if (data.showConfirmDialog) {
                    ConfirmWorkoutDialog(
                        state = data,
                        onConfirm = viewModel::confirmAddWorkout,
                        onDismiss = viewModel::dismissConfirmDialog
                    )
                }
                if (data.isWorkoutAdded) {
                    SuccessOverlay()
                }
                LaunchedEffect(data.isWorkoutAdded) {
                    if (data.isWorkoutAdded) {
                        delay(2000)
                        onSaved()
                    }
                }
            }
        }
    )
}

@Composable
private fun AddWorkoutContent(
    modifier: Modifier = Modifier,
    data: AddWorkoutUiState,
    onCancel: () -> Unit,
    onAddWorkout: () -> Unit,
    onDateChange: (java.time.LocalDate) -> Unit,
    onTimeChange: (java.time.LocalTime) -> Unit,
    onSetsChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit,
    onWeightChange: (Double) -> Unit,
    onRestChange: (Int) -> Unit,
    onNotesChange: (String) -> Unit
) {
    GScaffold(
        modifier = modifier,
        topBar = {
            GTopBar(
                title = stringResource(R.string.add_workout_title),
                windowInsets = WindowInsets(0),
                navigationIcon = { GTopBarBackIcon(onBack = onCancel) }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(GymTheme.token.spacing.md),
                verticalArrangement = Arrangement.spacedBy(GymTheme.token.spacing.lg)
            ) {
                data.exercise?.let { exercise ->
                    ExerciseHeader(exercise = exercise)
                    ScheduleSection(
                        selectedDate = data.selectedDate,
                        selectedTime = data.selectedTime,
                        onDateChange = onDateChange,
                        onTimeChange = onTimeChange
                    )
                    WorkoutSetupSection(
                        sets = data.sets,
                        reps = data.reps,
                        weightKg = data.weightKg,
                        restSeconds = data.restSeconds,
                        onSetsChange = onSetsChange,
                        onRepsChange = onRepsChange,
                        onWeightChange = onWeightChange,
                        onRestChange = onRestChange
                    )
                    NotesField(
                        notes = data.notes,
                        onNotesChange = onNotesChange
                    )
                }
            }
            BottomActionButtons(
                onCancel = onCancel,
                onPrimaryAction = onAddWorkout
            )
        }
    }
}
