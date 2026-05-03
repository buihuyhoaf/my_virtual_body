package com.hoabui.virtualbody3d.ui.exerciselibrary.manager

import android.util.Log
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.model.exercise.bookingSlotStartsForDay
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_FIRST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_LAST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_SLOT_STEP_MINUTES
import com.hoabui.virtualbody3d.domain.usecase.BookingConfirmationStatus
import com.hoabui.virtualbody3d.domain.usecase.CanConfirmLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.CommitLibrarySessionBookingResult
import com.hoabui.virtualbody3d.domain.usecase.ResolveNextSlotSelectionAfterToggleUseCase
import com.hoabui.virtualbody3d.domain.usecase.SessionBookingConfirmationWorkflow
import com.hoabui.virtualbody3d.domain.usecase.SessionBookingWorkflowInput
import com.hoabui.virtualbody3d.ui.exerciselibrary.mapper.ExerciseLibraryUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingInput
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingWorkflowPhase
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiEffect
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.toAddExerciseSuccessSummary
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.toLibraryCartDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.toPendingSessionBooking
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalTime
import javax.inject.Inject

@ViewModelScoped
class ExerciseLibraryBookingManager @Inject constructor(
    private val resolveNextSlotSelectionAfterToggleUseCase: ResolveNextSlotSelectionAfterToggleUseCase,
    private val sessionBookingConfirmationWorkflow: SessionBookingConfirmationWorkflow,
    private val canConfirmLibrarySessionBookingUseCase: CanConfirmLibrarySessionBookingUseCase,
    private val exerciseLibraryUiMapper: ExerciseLibraryUiMapper,
    private val cartManager: ExerciseLibraryCartManager,
    private val chromeManager: ExerciseLibraryChromeManager,
) {

    private val confirmBookingMutex = Mutex()

    private val bookingGridSlotStarts: List<LocalTime> = bookingSlotStartsForDay(
        firstSlot = SESSION_BOOKING_GRID_FIRST_SLOT,
        lastSlot = SESSION_BOOKING_GRID_LAST_SLOT,
        slotStepMinutes = SESSION_BOOKING_SLOT_STEP_MINUTES,
    )

    private val _sessionBookingInput = MutableStateFlow<SessionBookingInput?>(null)
    val sessionBookingInput: StateFlow<SessionBookingInput?> = _sessionBookingInput.asStateFlow()

    private val _sessionBookingWorkflowPhase =
        MutableStateFlow<SessionBookingWorkflowPhase>(SessionBookingWorkflowPhase.Idle)
    val sessionBookingWorkflowPhase: StateFlow<SessionBookingWorkflowPhase> =
        _sessionBookingWorkflowPhase.asStateFlow()

    fun openSessionBooking(mergedScreenState: ExerciseLibraryUiState, exercisesById: Map<String, Exercise>) {
        _sessionBookingInput.value = exerciseLibraryUiMapper.initialSessionBookingInput(
            mergedScreenState,
            exercisesById,
        )
        _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.Idle
    }

    fun dismissSessionBooking() {
        _sessionBookingInput.value = null
        _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.Idle
    }

    fun resetBookingAfterCartClear() {
        _sessionBookingInput.value = null
        _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.Idle
    }

    fun onBookingDateSelected(dateMillis: Long) {
        _sessionBookingInput.update { inp ->
            inp?.copy(
                selectedDateMillis = dateMillis,
            )
        }
        _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.Idle
    }

    fun onBookingLocationSelected(locationId: String) {
        _sessionBookingInput.update { inp ->
            inp?.copy(selectedLocationId = locationId)
        }
        _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.Idle
    }

    fun onBookingSlotToggled(slotStart: LocalTime) {
        val inp = _sessionBookingInput.value ?: return
        val next = resolveNextSlotSelectionAfterToggleUseCase(
            current = inp.selectedSlotStarts,
            tapped = slotStart,
            gridSlotStarts = bookingGridSlotStarts,
        ) ?: return
        val selectionChanged = next != inp.selectedSlotStarts
        _sessionBookingInput.update { i ->
            i?.copy(
                selectedSlotStarts = next.toPersistentSet(),
                longSessionAcknowledged = if (selectionChanged) false else i.longSessionAcknowledged,
            )
        }
        _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.Idle
    }

    fun onBookingClearTimeSelection() {
        val inp = _sessionBookingInput.value ?: return
        if (inp.selectedSlotStarts.isEmpty()) return
        _sessionBookingInput.update { i ->
            i?.copy(
                selectedSlotStarts = persistentSetOf(),
                longSessionAcknowledged = false,
            )
        }
        _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.Idle
    }

    fun onLongSessionEdit() {
        _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.Idle
    }

    fun onLongSessionProceedAnyway() {
        _sessionBookingInput.update { it?.copy(longSessionAcknowledged = true) }
        _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.Idle
    }

    suspend fun runBookingConfirmation(
        getMergedState: () -> ExerciseLibraryUiState,
        getCatalogExercisesById: () -> Map<String, Exercise>,
        getGymLocations: () -> ImmutableList<GymLocation>,
        emitUiEffect: suspend (ExerciseLibraryUiEffect) -> Unit,
    ) {
        confirmBookingMutex.withLock {
            val filters = getMergedState()
            val input = filters.sessionBookingInput
            if (input == null) {
                Log.d(BOOKING_LOG_TAG, "runBookingConfirmation: skip — sessionBookingInput is null")
                return@withLock
            }
            if (input.isConfirming) {
                Log.d(BOOKING_LOG_TAG, "runBookingConfirmation: skip — already confirming")
                return@withLock
            }
            val cartDraft = filters.toLibraryCartDraft()
            val canConfirm = canConfirmLibrarySessionBookingUseCase(
                selectedSlotStarts = input.selectedSlotStarts,
                selectedLocationId = input.selectedLocationId,
                selectedDateMillis = input.selectedDateMillis,
                cart = cartDraft,
                exerciseMeasurementById = filters.libraryList.exerciseMeasurementById,
                isConfirming = input.isConfirming,
            )
            Log.d(
                BOOKING_LOG_TAG,
                "runBookingConfirmation: start dateMillis=${input.selectedDateMillis} " +
                    "locationId=${input.selectedLocationId} slots=${input.selectedSlotStarts.sorted()} " +
                    "cartDraftOrder=${filters.draftOrder.size} exercisesById=${getCatalogExercisesById().size} " +
                    "longSessionAck=${input.longSessionAcknowledged} canConfirm=$canConfirm",
            )
            val exercisesById = getCatalogExercisesById()
            val locationDisplayName = getGymLocations()
                .find { it.id == input.selectedLocationId }
                ?.displayName
                ?.takeIf { it.isNotBlank() }
                ?: input.selectedLocationId
            val titlesById = input.bookingExerciseSnapshot.associate { it.id to it.title }
            val workflowInput = SessionBookingWorkflowInput(
                pending = input.toPendingSessionBooking(),
                cart = cartDraft,
                exerciseMeasurementById = filters.libraryList.exerciseMeasurementById,
                exerciseSnapshotTitlesById = titlesById,
                exercisesById = exercisesById,
                locationDisplayName = locationDisplayName,
            )
            sessionBookingConfirmationWorkflow.run(workflowInput).collect { status ->
                Log.d(BOOKING_LOG_TAG, "bookingConfirmation status=$status")
                when (status) {
                    BookingConfirmationStatus.Preparing,
                    BookingConfirmationStatus.Committing,
                    BookingConfirmationStatus.NoOp,
                    -> {
                        if (status == BookingConfirmationStatus.NoOp) {
                            Log.w(
                                BOOKING_LOG_TAG,
                                "prepare returned NoOp (validation failed, empty lines, or race). canConfirm was $canConfirm",
                            )
                        }
                    }
                    BookingConfirmationStatus.AwaitingLongSessionAck -> {
                        Log.d(BOOKING_LOG_TAG, "awaiting long session acknowledgement (proceed or edit)")
                        _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.AwaitingLongSessionAck
                    }
                    is BookingConfirmationStatus.PendingCommit -> {
                        Log.d(BOOKING_LOG_TAG, "pending commit (prepare Ready)")
                        _sessionBookingInput.update { inp ->
                            inp?.copy(isConfirming = true)
                        }
                        _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.Idle
                    }
                    is BookingConfirmationStatus.Completed -> {
                        when (val r = status.result) {
                            is CommitLibrarySessionBookingResult.Success -> {
                                Log.d(BOOKING_LOG_TAG, "commit Success scheduledCount=${r.scheduledCount}")
                                emitUiEffect(
                                    ExerciseLibraryUiEffect.ShowAddExerciseSuccess(
                                        summary = r.toAddExerciseSuccessSummary(),
                                    ),
                                )
                            }
                            CommitLibrarySessionBookingResult.Conflict ->
                                Log.w(BOOKING_LOG_TAG, "commit Conflict (DB error)")
                            CommitLibrarySessionBookingResult.InvalidDraft ->
                                Log.w(BOOKING_LOG_TAG, "commit InvalidDraft")
                        }
                        applyBookingCompleted(status.result)
                    }
                }
            }
        }
    }

    private fun applyBookingCompleted(result: CommitLibrarySessionBookingResult) {
        when (result) {
            CommitLibrarySessionBookingResult.Conflict -> {
                _sessionBookingInput.update { it?.copy(isConfirming = false) }
                _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.SlotConflict
            }
            CommitLibrarySessionBookingResult.InvalidDraft -> {
                _sessionBookingInput.update { it?.copy(isConfirming = false) }
                _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.Idle
            }
            is CommitLibrarySessionBookingResult.Success -> {
                cartManager.clearCartOnly()
                _sessionBookingInput.value = null
                _sessionBookingWorkflowPhase.value = SessionBookingWorkflowPhase.Idle
                chromeManager.setIdle()
            }
        }
    }

    private companion object {
        const val BOOKING_LOG_TAG = "ExerciseLibraryBooking"
    }
}
