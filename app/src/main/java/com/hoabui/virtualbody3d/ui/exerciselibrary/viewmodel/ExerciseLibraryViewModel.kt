package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeDurationMinutesSeconds
import com.hoabui.virtualbody3d.domain.usecase.CanConfirmLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.domain.usecase.MigrateLegacyWorkoutSchedulesUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveGymLocationsUseCase
import com.hoabui.virtualbody3d.domain.usecase.UpdateWorkoutScheduleFromCartDraftUseCase
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryBookingManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryChromeManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibrarySearchManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.mapper.ExerciseLibraryUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.SelectionBarCartBaseline
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.ExerciseLibraryCatalogGrouped
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.LibraryPresentationSlice
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingInput
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryChromeMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiEffect
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.isCartDraftValidForSessionConfirm
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.mapGroupedToCatalogGrouped
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.toExerciseDraftForSelectionBarEdit
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.toLibraryCartDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.BookingPipelineRow
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.exerciseLibraryBookingPresentationKey
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.exerciseLibrarySectionRebuildKey
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.mergeExerciseLibraryPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val getExerciseLibraryUseCase: GetExerciseLibraryUseCase,
    private val observeGymLocationsUseCase: ObserveGymLocationsUseCase,
    private val migrateLegacyWorkoutSchedulesUseCase: MigrateLegacyWorkoutSchedulesUseCase,
    private val exerciseLibraryUiMapper: ExerciseLibraryUiMapper,
    private val canConfirmLibrarySessionBookingUseCase: CanConfirmLibrarySessionBookingUseCase,
    private val updateWorkoutScheduleFromCartDraftUseCase: UpdateWorkoutScheduleFromCartDraftUseCase,
    private val workoutScheduleRepository: WorkoutScheduleRepository,
    private val searchManager: ExerciseLibrarySearchManager,
    private val cartManager: ExerciseLibraryCartManager,
    private val bookingManager: ExerciseLibraryBookingManager,
    private val chromeManager: ExerciseLibraryChromeManager,
) : UiStateViewModel<ExerciseLibraryUiState, Unit>() {

    private val catalogGroupedByRegion = MutableStateFlow<ExerciseLibraryCatalogGrouped>(persistentMapOf())
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
        isAddToSessionEnabled = false,
    )

    private val filterCatalogSessionChrome = combine(
        combine(
            searchManager.searchQuery,
            searchManager.selectedExerciseCategory,
            searchManager.selectedBodyRegions,
            searchManager.selectedEquipment,
            cartManager.itemDrafts,
        ) { q, c, r, e, d -> FilterCatalogDraftPart(q, c, r, e, d) },
        combine(
            cartManager.draftOrder,
            cartManager.activeExerciseId,
            cartManager.isCartExpanded,
            catalogGroupedByRegion,
            bookingManager.sessionBookingInput,
        ) { o, a, exp, cat, sIn ->
            CartCatalogSessionPart(o, a, exp, cat, sIn)
        },
        combine(bookingManager.sessionBookingWorkflowPhase, chromeManager.chromeMode) { ph, ch -> ph to ch },
    ) { f, c, sc ->
        Triple(f, c, sc)
    }

    private val baseStateFlow: StateFlow<ExerciseLibraryUiState> = filterCatalogSessionChrome
        .map { triple ->
            val f = triple.first
            val c = triple.second
            val (phase, chrome) = triple.third
            ExerciseLibraryUiState(
                searchQuery = f.q,
                selectedExerciseCategory = f.cat,
                selectedBodyRegions = f.regions,
                selectedEquipment = f.equipment,
                itemDrafts = f.itemDrafts,
                draftOrder = c.order,
                activeExerciseId = c.activeId,
                isCartExpanded = c.expanded,
                catalogGroupedByRegion = c.catalog,
                libraryList = LibraryPresentationSlice(),
                sessionBookingInput = c.sessionIn,
                sessionBookingUiModel = null,
                sessionBookingWorkflowPhase = phase,
                chromeMode = chrome,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExerciseLibraryUiState(),
        )

    private val librarySliceFlow: StateFlow<LibraryPresentationSlice> =
        baseStateFlow
            .distinctUntilChanged { a, b ->
                exerciseLibrarySectionRebuildKey(a.catalogGroupedByRegion, a) ==
                    exerciseLibrarySectionRebuildKey(b.catalogGroupedByRegion, b)
            }
            .map { exerciseLibraryUiMapper.mapLibraryPresentation(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyLibrarySlice,
            )

    private val bookingProjectionFlow: StateFlow<SessionBookingUiModel?> =
        combine(
            combine(baseStateFlow, librarySliceFlow) { base, libSlice -> base to libSlice },
            gymLocationsStateFlow,
        ) { fl, gymLocs ->
            val (base, libSlice) = fl
            val withMeasurement = base.copy(
                libraryList = base.libraryList.copy(exerciseMeasurementById = libSlice.exerciseMeasurementById),
            )
            if (withMeasurement.sessionBookingInput == null) {
                BookingPipelineRow(
                    dedupeKey = null,
                    filtersWithMeasurement = withMeasurement,
                    gymLocations = gymLocs,
                )
            } else {
                val key = exerciseLibraryBookingPresentationKey(
                    filtersWithMeasurement = withMeasurement,
                    gymLocationsVersion = gymLocs,
                )
                BookingPipelineRow(
                    dedupeKey = key,
                    filtersWithMeasurement = withMeasurement,
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
            baseStateFlow,
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

    private val _uiEffects = Channel<ExerciseLibraryUiEffect>(capacity = Channel.BUFFERED)
    val uiEffects = _uiEffects.receiveAsFlow()

    init {
        viewModelScope.launch {
            migrateLegacyWorkoutSchedulesUseCase()
        }

        getExerciseLibraryUseCase()
            .onEach { grouped ->
                catalogExercisesById.value = grouped.values.flatten().associateBy { it.id }
                catalogGroupedByRegion.value = mapGroupedToCatalogGrouped(grouped)
            }
            .catch { setError(it.message ?: "Unknown error") }
            .launchIn(viewModelScope)

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
        val input = filtersWithMeasurement.sessionBookingInput ?: return false
        return canConfirmLibrarySessionBookingUseCase(
            selectedSlotStarts = input.selectedSlotStarts,
            selectedLocationId = input.selectedLocationId,
            selectedDateMillis = input.selectedDateMillis,
            cart = filtersWithMeasurement.toLibraryCartDraft(),
            exerciseMeasurementById = filtersWithMeasurement.libraryList.exerciseMeasurementById,
            isConfirming = input.isConfirming,
        )
    }

    private fun cartSnapshotUiState(): ExerciseLibraryUiState =
        ExerciseLibraryUiState(
            searchQuery = searchManager.searchQuery.value,
            selectedExerciseCategory = searchManager.selectedExerciseCategory.value,
            selectedBodyRegions = searchManager.selectedBodyRegions.value,
            selectedEquipment = searchManager.selectedEquipment.value,
            itemDrafts = cartManager.itemDrafts.value,
            draftOrder = cartManager.draftOrder.value,
            activeExerciseId = cartManager.activeExerciseId.value,
            isCartExpanded = cartManager.isCartExpanded.value,
            catalogGroupedByRegion = catalogGroupedByRegion.value,
            libraryList = LibraryPresentationSlice(
                exerciseMeasurementById = librarySliceFlow.value.exerciseMeasurementById,
            ),
            sessionBookingInput = bookingManager.sessionBookingInput.value,
            sessionBookingWorkflowPhase = bookingManager.sessionBookingWorkflowPhase.value,
            chromeMode = chromeManager.chromeMode.value,
        )

    private fun runBookingConfirmationWorkflow() {
        launchSafely {
            bookingManager.runBookingConfirmation(
                getMergedState = { mergedScreenStateFlow.value },
                getCatalogExercisesById = { catalogExercisesById.value },
                getGymLocations = { gymLocationsStateFlow.value },
            ) { effect -> _uiEffects.send(effect) }
        }
    }

    fun setSearchQuery(query: String) = searchManager.setSearchQuery(query)

    fun setInitialExerciseCategoryFilter(category: ExerciseCategory) =
        searchManager.setInitialExerciseCategoryFilter(category)

    fun setInitialBodyRegionFilter(regions: ImmutableSet<BodyRegion>) =
        searchManager.setInitialBodyRegionFilter(regions)

    fun toggleCardSelection(exerciseId: String) {
        val synthetic = cartSnapshotUiState()
        if (cartManager.toggleCardSelection(synthetic, exerciseId)) {
            cartManager.prefillFromHistory(exerciseId, catalogExercisesById.value)
        }
    }

    fun selectCartItem(exerciseId: String) {
        cartManager.selectCartItem(cartSnapshotUiState(), exerciseId)
    }

    fun removeCartItem(exerciseId: String) {
        cartManager.removeCartItem(cartSnapshotUiState(), exerciseId)
    }

    fun clearCart() {
        cartManager.clearCartOnly()
        chromeManager.setIdle()
        bookingManager.resetBookingAfterCartClear()
    }

    fun stepCartField(exerciseId: String, setIndex: Int, field: CartSetField, delta: Int) {
        cartManager.stepCartField(exerciseId, setIndex, field, delta)
    }

    fun setCartFieldManual(exerciseId: String, setIndex: Int, field: CartSetField, value: String) {
        cartManager.setCartFieldManual(exerciseId, setIndex, field, value)
    }

    fun toggleCartExpanded() = cartManager.toggleCartExpanded()

    fun openSessionBooking() {
        bookingManager.openSessionBooking(
            mergedScreenStateFlow.value,
            catalogExercisesById.value,
        )
    }

    fun dismissSessionBooking() = bookingManager.dismissSessionBooking()

    fun onBookingDateSelected(dateMillis: Long) = bookingManager.onBookingDateSelected(dateMillis)

    fun onBookingLocationSelected(locationId: String) =
        bookingManager.onBookingLocationSelected(locationId)

    fun onBookingSlotToggled(slotStart: LocalTime) = bookingManager.onBookingSlotToggled(slotStart)

    fun onBookingClearTimeSelection() = bookingManager.onBookingClearTimeSelection()

    fun confirmSessionBooking() {
        Log.d(BOOKING_LOG_TAG, "confirmSessionBooking: enqueue RunBookingConfirmation")
        viewModelScope.launch {
            runBookingConfirmationWorkflow()
        }
    }

    fun onLongSessionEdit() = bookingManager.onLongSessionEdit()

    fun onLongSessionProceedAnyway() {
        bookingManager.onLongSessionProceedAnyway()
        viewModelScope.launch {
            runBookingConfirmationWorkflow()
        }
    }

    fun dismissAddExerciseSuccess() {
        /* No reducer state; dialog is local. */
    }

    fun startSelectionBarEditFromScheduleRow(scheduleRowId: Long) {
        launchSafely {
            val schedule = workoutScheduleRepository.getWorkoutScheduleByRowId(scheduleRowId) ?: return@launchSafely
            val exerciseId = schedule.exerciseId
            val draft = schedule.toExerciseDraftForSelectionBarEdit()
            val newDrafts = persistentMapOf(exerciseId to draft)
            val newOrder = persistentListOf(exerciseId)
            cartManager.setSelectionBarEditCart(newDrafts, newOrder, exerciseId, isCartExpanded = true)
            val baseline = SelectionBarCartBaseline(
                itemDrafts = newDrafts,
                draftOrder = newOrder,
                activeExerciseId = exerciseId,
                isCartExpanded = true,
            )
            chromeManager.setChromeMode(
                ExerciseLibraryChromeMode.EditingScheduleRow(
                    scheduleRowId = scheduleRowId,
                    baselineCart = baseline,
                    isIsolatedScheduleRowSelectionEdit = true,
                    measurementMode = schedule.measurementMode,
                ),
            )
        }
    }

    fun cancelSelectionBarEdit() {
        val mode = chromeManager.chromeMode.value as? ExerciseLibraryChromeMode.EditingScheduleRow ?: return
        chromeManager.setIdle()
        if (mode.isIsolatedScheduleRowSelectionEdit) {
            cartManager.clearCartForIsolatedSelectionEdit()
        } else {
            cartManager.restoreCartFromBaseline(
                itemDrafts = mode.baselineCart.itemDrafts,
                draftOrder = mode.baselineCart.draftOrder,
                activeExerciseId = mode.baselineCart.activeExerciseId,
                isCartExpanded = false,
            )
        }
    }

    fun confirmSelectionBarEdit() {
        launchSafely {
            val s = mergedScreenStateFlow.value
            val editMode = s.chromeMode as? ExerciseLibraryChromeMode.EditingScheduleRow ?: return@launchSafely
            val rowId = editMode.scheduleRowId
            if (!s.isCartDraftValidForSessionConfirm()) return@launchSafely
            val exerciseId = s.activeExerciseId ?: s.draftOrder.firstOrNull() ?: return@launchSafely
            val draft = s.itemDrafts[exerciseId] ?: return@launchSafely
            val mode = s.libraryList.exerciseMeasurementById[exerciseId]
                ?: editMode.measurementMode
                ?: workoutScheduleRepository.getWorkoutScheduleByRowId(rowId)?.measurementMode
                ?: ExerciseMeasurementMode.Strength
            val sets: Int
            val reps: Int
            val weightKg: Double
            val durationSeconds: Int?
            when (mode) {
                ExerciseMeasurementMode.Strength -> {
                    if (draft.setRows.any { it.reps <= 0 }) return@launchSafely
                    sets = draft.setRows.size
                    reps = draft.setRows.first().reps
                    weightKg = draft.setRows.first().weightKg
                    durationSeconds = null
                }
                ExerciseMeasurementMode.Duration -> {
                    val row = draft.setRows.firstOrNull() ?: return@launchSafely
                    val sec = normalizeDurationMinutesSeconds(row.minutes, row.seconds)
                    if (sec <= 0) return@launchSafely
                    sets = 1
                    reps = 0
                    weightKg = 0.0
                    durationSeconds = sec
                }
            }
            val ok = updateWorkoutScheduleFromCartDraftUseCase(
                rowId = rowId,
                exerciseId = exerciseId,
                measurementMode = mode,
                sets = sets,
                reps = reps,
                weightKg = weightKg,
                durationSeconds = durationSeconds,
            )
            if (ok) {
                val m = chromeManager.chromeMode.value as? ExerciseLibraryChromeMode.EditingScheduleRow ?: return@launchSafely
                chromeManager.setIdle()
                if (m.isIsolatedScheduleRowSelectionEdit) {
                    cartManager.clearCartForIsolatedSelectionEdit()
                } else {
                    cartManager.setCartExpanded(false)
                }
            }
        }
    }

    private companion object {
        const val BOOKING_LOG_TAG = "ExerciseLibraryBooking"
    }
}

private data class FilterCatalogDraftPart(
    val q: String,
    val cat: ExerciseCategory?,
    val regions: ImmutableSet<BodyRegion>?,
    val equipment: EquipmentType?,
    val itemDrafts: ImmutableMap<String, ExerciseDraft>,
)

private data class CartCatalogSessionPart(
    val order: ImmutableList<String>,
    val activeId: String?,
    val expanded: Boolean,
    val catalog: ExerciseLibraryCatalogGrouped,
    val sessionIn: SessionBookingInput?,
)
