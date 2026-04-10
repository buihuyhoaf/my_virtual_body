package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.model.exercise.bookingSlotStartsForDay
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_FIRST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_LAST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_SLOT_STEP_MINUTES
import com.hoabui.virtualbody3d.domain.usecase.BookingConfirmationStatus
import com.hoabui.virtualbody3d.domain.usecase.CommitLibrarySessionBookingResult
import com.hoabui.virtualbody3d.domain.usecase.CanConfirmLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.ExerciseLibraryCartCommand
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.domain.usecase.MigrateLegacyWorkoutSchedulesUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveGymLocationsUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseLibraryMonthlySummaryUseCase
import com.hoabui.virtualbody3d.domain.usecase.ResolveNextSlotSelectionAfterToggleUseCase
import com.hoabui.virtualbody3d.domain.usecase.SessionBookingConfirmationWorkflow
import com.hoabui.virtualbody3d.domain.usecase.SessionBookingWorkflowInput
import com.hoabui.virtualbody3d.domain.usecase.ToggleExerciseInCartUseCase
import com.hoabui.virtualbody3d.domain.usecase.UpdateExerciseDraftUseCase
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseLibraryCatalogUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.toCartSnapshot
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.toLibraryCartDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.toPendingSessionBooking
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.BookingPipelineRow
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.LibraryPresentationSlice
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.exerciseLibraryBookingPresentationKey
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.exerciseLibrarySectionRebuildKey
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.mergeExerciseLibraryPresentation
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mapper.ExerciseLibraryUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryMonthlySummaryState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryCartState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibraryIntent
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibrarySideEffect
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibraryUpdate
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.reducer.ExerciseLibraryReducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val getExerciseLibraryUseCase: GetExerciseLibraryUseCase,
    private val sessionBookingConfirmationWorkflow: SessionBookingConfirmationWorkflow,
    private val observeGymLocationsUseCase: ObserveGymLocationsUseCase,
    private val observeExerciseLibraryMonthlySummaryUseCase: ObserveExerciseLibraryMonthlySummaryUseCase,
    private val migrateLegacyWorkoutSchedulesUseCase: MigrateLegacyWorkoutSchedulesUseCase,
    private val exerciseLibraryUiMapper: ExerciseLibraryUiMapper,
    private val exerciseLibraryCatalogUiMapper: ExerciseLibraryCatalogUiMapper,
    private val exerciseLibraryReducer: ExerciseLibraryReducer,
    private val toggleExerciseInCartUseCase: ToggleExerciseInCartUseCase,
    private val updateExerciseDraftUseCase: UpdateExerciseDraftUseCase,
    private val canConfirmLibrarySessionBookingUseCase: CanConfirmLibrarySessionBookingUseCase,
    private val resolveNextSlotSelectionAfterToggleUseCase: ResolveNextSlotSelectionAfterToggleUseCase,
) : UiStateViewModel<ExerciseLibraryUiState, Unit>() {

    private val confirmBookingMutex = Mutex()

    private val filterState = MutableStateFlow(ExerciseLibraryUiState())
    private val zoneId: ZoneId = ZoneId.systemDefault()

    private val bookingGridSlotStarts: List<LocalTime> = bookingSlotStartsForDay(
        firstSlot = SESSION_BOOKING_GRID_FIRST_SLOT,
        lastSlot = SESSION_BOOKING_GRID_LAST_SLOT,
        slotStepMinutes = SESSION_BOOKING_SLOT_STEP_MINUTES,
    )

    val cartStateFlow: Flow<LibraryCartState> = filterState.map { it.cart }.distinctUntilChanged()
    /**
     * Domain exercise index for booking and detail mapping; not part of [ExerciseLibraryUiState].
     * Updated only from [GetExerciseLibraryUseCase] alongside [ExerciseLibraryUpdate.CatalogLoaded].
     */
    private val catalogExercisesById = MutableStateFlow<Map<String, Exercise>>(emptyMap())

    private val gymLocationsStateFlow: StateFlow<ImmutableList<GymLocation>> =
        observeGymLocationsUseCase()
            .map { it.toImmutableList() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = persistentListOf(),
            )

    private val emptyLibrarySlice = LibraryPresentationSlice(
        sections = persistentListOf(),
        exerciseMeasurementById = persistentMapOf(),
        selectedExerciseForDetail = null,
        isAddToSessionEnabled = false,
    )

    /**
     * Library list + measurement + detail; only recomputes when section rebuild inputs or detail selection change.
     */
    private val librarySliceFlow: StateFlow<LibraryPresentationSlice> =
        combine(filterState, catalogExercisesById) { filters, exercisesById ->
            Triple(
                exerciseLibrarySectionRebuildKey(filters.catalog, filters),
                filters.chrome.detailExerciseId,
                Pair(filters, exercisesById),
            )
        }
            .distinctUntilChanged { a, b -> a.first == b.first && a.second == b.second }
            .map { (_, _, pair) ->
                val (filters, exercisesById) = pair
                exerciseLibraryUiMapper.mapLibraryPresentation(filters, exercisesById)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyLibrarySlice,
            )

    /**
     * Booking sheet projection. Rebuilds when [exerciseLibraryBookingPresentationKey] changes;
     * `distinctUntilChanged` skips redundant work (including search-only edits that do not affect the key).
     */
    private val bookingProjectionFlow: StateFlow<SessionBookingUiModel?> =
        combine(
            combine(filterState, librarySliceFlow) { filters, libSlice -> filters to libSlice },
            gymLocationsStateFlow,
        ) { fl, gymLocs ->
            val (filters, libSlice) = fl
            val filtersWithMeasurement = filters.copy(
                libraryList = filters.libraryList.copy(exerciseMeasurementById = libSlice.exerciseMeasurementById),
            )
            if (filtersWithMeasurement.sessionBooking.input == null) {
                BookingPipelineRow(
                    dedupeKey = null,
                    filtersWithMeasurement = filtersWithMeasurement,
                    gymLocations = gymLocs,
                )
            } else {
                val key = exerciseLibraryBookingPresentationKey(
                    filtersWithMeasurement = filtersWithMeasurement,
                    gymLocationsVersion = gymLocs,
                )
                BookingPipelineRow(
                    dedupeKey = key,
                    filtersWithMeasurement = filtersWithMeasurement,
                    gymLocations = gymLocs,
                )
            }
        }
            .distinctUntilChanged { a, b -> a.dedupeKey == b.dedupeKey }
            .map { row ->
                if (row.dedupeKey == null) {
                    null
                } else {
                    buildBookingUi(
                        filtersWithMeasurement = row.filtersWithMeasurement,
                        gymLocations = row.gymLocations,
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    private val mergedScreenStateFlow: StateFlow<ExerciseLibraryUiState> =
        combine(
            librarySliceFlow,
            filterState,
            bookingProjectionFlow,
        ) { librarySlice, base, sessionBookingUiModel ->
            mergeExerciseLibraryPresentation(
                base = base,
                library = librarySlice,
                sessionBookingUiModel = sessionBookingUiModel,
            )
        }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = mergeExerciseLibraryPresentation(
                    base = ExerciseLibraryUiState(),
                    library = emptyLibrarySlice,
                    sessionBookingUiModel = null,
                ),
            )

    private val sideEffects = Channel<ExerciseLibrarySideEffect>(capacity = Channel.UNLIMITED)

    init {
        viewModelScope.launch {
            migrateLegacyWorkoutSchedulesUseCase(zoneId)
        }

        getExerciseLibraryUseCase()
            .onEach { grouped ->
                catalogExercisesById.value = grouped.values.flatten().associateBy { it.id }
                dispatch(
                    ExerciseLibraryUpdate.CatalogLoaded(
                        exerciseLibraryCatalogUiMapper.mapGroupedToCatalogState(grouped),
                    ),
                )
            }
            .catch { setError(it.message ?: "Unknown error") }
            .launchIn(viewModelScope)

        observeExerciseLibraryMonthlySummaryUseCase(YearMonth.now(zoneId), zoneId)
            .onEach { domain ->
                dispatch(
                    ExerciseLibraryUpdate.MonthlySummaryLoaded(
                        LibraryMonthlySummaryState.Loaded(
                            yearMonth = domain.yearMonth,
                            workoutDayCount = domain.workoutDayCount,
                            restDayCount = domain.restDayCount,
                        ),
                    ),
                )
            }
            .catch { e ->
                dispatch(
                    ExerciseLibraryUpdate.MonthlySummaryLoaded(
                        LibraryMonthlySummaryState.Error(e.message.orEmpty()),
                    ),
                )
            }
            .launchIn(viewModelScope)

        observeSideEffects()

        mergedScreenStateFlow
            .onEach { setSuccess(it) }
            .launchIn(viewModelScope)
    }

    private fun buildBookingUi(
        filtersWithMeasurement: ExerciseLibraryUiState,
        gymLocations: ImmutableList<GymLocation>,
    ) = exerciseLibraryUiMapper.mapBookingPresentation(
        filtersWithMeasurement = filtersWithMeasurement,
        gymLocations = gymLocations,
        isBookingConfirmEnabled = computeCanConfirm(filtersWithMeasurement),
    )

    private fun computeCanConfirm(
        filtersWithMeasurement: ExerciseLibraryUiState,
    ): Boolean {
        val input = filtersWithMeasurement.sessionBooking.input ?: return false
        return canConfirmLibrarySessionBookingUseCase(
            selectedSlotStarts = input.selectedSlotStarts,
            selectedLocationId = input.selectedLocationId,
            selectedDateMillis = input.selectedDateMillis,
            cart = filtersWithMeasurement.toLibraryCartDraft(),
            exerciseMeasurementById = filtersWithMeasurement.libraryList.exerciseMeasurementById,
            zoneId = zoneId,
            isConfirming = input.isConfirming,
        )
    }

    private fun observeSideEffects() {
        viewModelScope.launch {
            for (effect in sideEffects) {
                when (effect) {
                    ExerciseLibrarySideEffect.RunBookingConfirmation -> runBookingConfirmationWorkflow()
                }
            }
        }
    }

    private fun dispatch(update: ExerciseLibraryUpdate) {
        filterState.update { current ->
            exerciseLibraryReducer.reduce(current, update)
        }
    }

    private fun dispatchIntent(intent: ExerciseLibraryIntent) {
        dispatch(ExerciseLibraryUpdate.UserIntent(intent))
        if (intent is ExerciseLibraryIntent.LongSessionProceedAnyway) {
            sideEffects.trySend(ExerciseLibrarySideEffect.RunBookingConfirmation)
        }
    }

    private fun runBookingConfirmationWorkflow() {
        launchSafely {
            confirmBookingMutex.withLock {
                val filters = filterState.value
                val input = filters.sessionBooking.input
                if (input == null) {
                    Log.d(BOOKING_LOG_TAG, "runBookingConfirmation: skip — sessionBooking.input is null")
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
                    zoneId = zoneId,
                    isConfirming = input.isConfirming,
                )
                Log.d(
                    BOOKING_LOG_TAG,
                    "runBookingConfirmation: start dateMillis=${input.selectedDateMillis} " +
                        "locationId=${input.selectedLocationId} slots=${input.selectedSlotStarts.sorted()} " +
                        "cartDraftOrder=${filters.cart.draftOrder.size} exercisesById=${catalogExercisesById.value.size} " +
                        "longSessionAck=${input.longSessionAcknowledged} canConfirm=$canConfirm",
                )
                val exercisesById = catalogExercisesById.value
                val locationDisplayName = gymLocationsStateFlow.value
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
                    zoneId = zoneId,
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
                            dispatch(ExerciseLibraryUpdate.BookingConfirmation.AwaitingLongSessionAck)
                        }
                        is BookingConfirmationStatus.PendingCommit -> {
                            Log.d(BOOKING_LOG_TAG, "pending commit (prepare Ready)")
                            dispatch(ExerciseLibraryUpdate.BookingConfirmation.PendingCommit)
                        }
                        is BookingConfirmationStatus.Completed -> {
                            when (val r = status.result) {
                                is CommitLibrarySessionBookingResult.Success ->
                                    Log.d(BOOKING_LOG_TAG, "commit Success scheduledCount=${r.scheduledCount}")
                                CommitLibrarySessionBookingResult.Conflict ->
                                    Log.w(BOOKING_LOG_TAG, "commit Conflict (DB error)")
                                CommitLibrarySessionBookingResult.InvalidDraft ->
                                    Log.w(BOOKING_LOG_TAG, "commit InvalidDraft")
                            }
                            dispatch(ExerciseLibraryUpdate.BookingConfirmation.Completed(status.result))
                        }
                    }
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        dispatchIntent(ExerciseLibraryIntent.SetSearchQuery(query))
    }

    fun selectQuickChip(chip: com.hoabui.virtualbody3d.ui.exerciselibrary.ExerciseLibraryQuickChip?) {
        dispatchIntent(ExerciseLibraryIntent.SelectQuickChip(chip))
    }

    fun toggleExerciseInCartFromList(exerciseId: String) {
        val snap = toggleExerciseInCartUseCase(
            filterState.value.toCartSnapshot(),
            ExerciseLibraryCartCommand.Toggle(exerciseId),
        )
        dispatch(ExerciseLibraryUpdate.CartFromDomain(snap))
    }

    fun ensureInCartAndFocusFromDetail(exerciseId: String) {
        val snap = toggleExerciseInCartUseCase(
            filterState.value.toCartSnapshot(),
            ExerciseLibraryCartCommand.EnsureInCartAndFocus(exerciseId),
        )
        dispatch(ExerciseLibraryUpdate.CartFromDomain(snap))
    }

    fun removeFromCart(exerciseId: String) {
        val snap = toggleExerciseInCartUseCase(
            filterState.value.toCartSnapshot(),
            ExerciseLibraryCartCommand.Remove(exerciseId),
        )
        dispatch(ExerciseLibraryUpdate.CartFromDomain(snap))
    }

    fun setActiveCartExercise(exerciseId: String) {
        val snap = toggleExerciseInCartUseCase(
            filterState.value.toCartSnapshot(),
            ExerciseLibraryCartCommand.SetActive(exerciseId),
        )
        dispatch(ExerciseLibraryUpdate.CartFromDomain(snap))
    }

    fun clearAll() {
        dispatch(ExerciseLibraryUpdate.LibraryCleared)
    }

    fun updateActiveDraft(sets: String, reps: String) {
        val snap = updateExerciseDraftUseCase(
            filterState.value.toCartSnapshot(),
            filterState.value.cart.activeExerciseId,
            sets,
            reps,
        )
        dispatch(ExerciseLibraryUpdate.CartFromDomain(snap))
    }

    fun openSessionBooking() {
        val input = exerciseLibraryUiMapper.initialSessionBookingInput(
            filterState.value,
            catalogExercisesById.value,
        )
        dispatch(ExerciseLibraryUpdate.SessionBookingOpened(input))
    }

    fun dismissSessionBooking() {
        dispatchIntent(ExerciseLibraryIntent.DismissSessionBooking)
    }

    fun onBookingDateSelected(dateMillis: Long) {
        dispatchIntent(ExerciseLibraryIntent.BookingDateSelected(dateMillis))
    }

    fun onBookingLocationSelected(locationId: String) {
        dispatchIntent(ExerciseLibraryIntent.BookingLocationSelected(locationId))
    }

    fun onBookingSlotToggled(slotStart: LocalTime) {
        val s = filterState.value
        val inp = s.sessionBooking.input ?: return
        val next = resolveNextSlotSelectionAfterToggleUseCase(
            current = inp.selectedSlotStarts,
            tapped = slotStart,
            gridSlotStarts = bookingGridSlotStarts,
        ) ?: return
        dispatch(
            ExerciseLibraryUpdate.SlotSelectionResolved(
                selectedSlotStarts = next,
                selectionChanged = next != inp.selectedSlotStarts,
            ),
        )
    }

    fun onBookingClearTimeSelection() {
        dispatchIntent(ExerciseLibraryIntent.BookingClearTimeSelection)
    }

    fun onLongSessionEdit() {
        dispatchIntent(ExerciseLibraryIntent.LongSessionEdit)
    }

    fun onLongSessionProceedAnyway() {
        dispatchIntent(ExerciseLibraryIntent.LongSessionProceedAnyway)
    }

    fun dismissAddExerciseSuccess() {
        dispatchIntent(ExerciseLibraryIntent.DismissAddExerciseSuccess)
    }

    fun selectExerciseForDetail(exerciseId: String) {
        dispatchIntent(ExerciseLibraryIntent.SelectExerciseForDetail(exerciseId))
    }

    fun clearExerciseDetail() {
        dispatchIntent(ExerciseLibraryIntent.ClearExerciseDetail)
    }

    fun confirmSessionBooking() {
        Log.d(BOOKING_LOG_TAG, "confirmSessionBooking: enqueue RunBookingConfirmation")
        sideEffects.trySend(ExerciseLibrarySideEffect.RunBookingConfirmation)
    }

    private companion object {
        const val BOOKING_LOG_TAG = "ExerciseLibraryBooking"
    }
}
