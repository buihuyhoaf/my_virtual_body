package com.hoabui.virtualbody3d.ui.addworkout.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.hoabui.virtualbody3d.core.base.UiState
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.WorkoutSchedule
import com.hoabui.virtualbody3d.ui.addworkout.AddWorkoutEvent
import com.hoabui.virtualbody3d.ui.addworkout.state.AddWorkoutUiState
import com.hoabui.virtualbody3d.domain.usecase.AddWorkoutUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddWorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase,
    private val addWorkoutUseCase: AddWorkoutUseCase
) : UiStateViewModel<AddWorkoutUiState, AddWorkoutEvent>() {

    private val exerciseId: String = savedStateHandle.get<String>("exerciseId") ?: ""

    init {
        launchSafely {
            setLoading()
            getExerciseByIdUseCase(exerciseId).collectLatest { exercise ->
                if (exercise != null) {
                    setSuccess(
                        AddWorkoutUiState(
                            exercise = exercise,
                            weightKg = exercise.lastWeightKg ?: 0.0
                        )
                    )
                } else {
                    setError("Exercise not found")
                }
            }
        }
    }

    fun updateDate(date: LocalDate) {
        updateSuccess { copy(selectedDate = date) }
    }

    fun updateTime(time: LocalTime) {
        updateSuccess { copy(selectedTime = time) }
    }

    fun updateSets(sets: Int) {
        updateSuccess { copy(sets = sets) }
    }

    fun updateReps(reps: Int) {
        updateSuccess { copy(reps = reps) }
    }

    fun updateWeight(weightKg: Double) {
        updateSuccess { copy(weightKg = weightKg) }
    }

    fun updateRestSeconds(restSeconds: Int) {
        updateSuccess { copy(restSeconds = restSeconds) }
    }

    fun updateNotes(notes: String) {
        updateSuccess { copy(notes = notes) }
    }

    fun onCancel() {
        sendEvent(AddWorkoutEvent.Cancel)
    }

    /** Opens the confirmation dialog. */
    fun onAddWorkout() {
        updateSuccess { copy(showConfirmDialog = true) }
    }

    /** Closes the confirmation dialog without saving. */
    fun dismissConfirmDialog() {
        updateSuccess { copy(showConfirmDialog = false) }
    }

    /** Saves the workout and shows success state; called when user confirms in dialog. */
    fun confirmAddWorkout() {
        val uiState = (state.value as? UiState.Success)?.data ?: return
        val exercise = uiState.exercise ?: return
        launchSafely {
            val scheduledAt = LocalDateTime.of(uiState.selectedDate, uiState.selectedTime)
            val schedule = WorkoutSchedule(
                id = UUID.randomUUID().toString(),
                exerciseId = exercise.id,
                scheduledAt = scheduledAt,
                sets = uiState.sets,
                reps = uiState.reps,
                weightKg = uiState.weightKg,
                restSeconds = uiState.restSeconds,
                notes = uiState.notes.ifBlank { null }
            )
            addWorkoutUseCase(schedule)
            updateSuccess {
                copy(showConfirmDialog = false, isWorkoutAdded = true)
            }
        }
    }
}
