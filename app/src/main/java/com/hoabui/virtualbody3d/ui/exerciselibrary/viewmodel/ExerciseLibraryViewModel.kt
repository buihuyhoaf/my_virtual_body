package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.DEFAULT_SESSION_LOCATION_ID
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.InstantInterval
import com.hoabui.virtualbody3d.domain.model.exercise.SessionExerciseLine
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import com.hoabui.virtualbody3d.domain.model.exercise.bookingSlotStartsForDay
import com.hoabui.virtualbody3d.domain.model.exercise.computeNextSlotSelectionAfterToggle
import com.hoabui.virtualbody3d.domain.model.exercise.isContiguousThirtyMinuteChain
import com.hoabui.virtualbody3d.domain.model.exercise.isIntervalFreeForBooking
import com.hoabui.virtualbody3d.domain.model.exercise.matchesLibrarySearch
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeDurationMinutesSeconds
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeExerciseLibraryQuery
import com.hoabui.virtualbody3d.domain.model.exercise.proposedVariableSessionInterval
import com.hoabui.virtualbody3d.domain.model.exercise.shouldWarnLongSession
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.repository.BookWorkoutSessionResult
import com.hoabui.virtualbody3d.domain.usecase.BookWorkoutSessionUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.domain.usecase.MigrateLegacyWorkoutSchedulesUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveBusyIntervalsUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveGymLocationsUseCase
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_FIRST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_LAST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_SLOT_STEP_MINUTES
import com.hoabui.virtualbody3d.ui.exerciselibrary.ExerciseLibraryQuickChip
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.toLibraryCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.AddExerciseSuccessSummary
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseSectionUiItem
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.SessionBookingInput
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.buildBookingExerciseSnapshot
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.buildSessionBookingUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.canOpenBooking
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mergeBookingInputWithBusy
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.defaultExerciseLibraryCartDateMillis
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.isCartDraftValidForSessionConfirm
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val getExerciseLibraryUseCase: GetExerciseLibraryUseCase,
    private val bookWorkoutSessionUseCase: BookWorkoutSessionUseCase,
    private val observeGymLocationsUseCase: ObserveGymLocationsUseCase,
    private val observeBusyIntervalsUseCase: ObserveBusyIntervalsUseCase,
    private val migrateLegacyWorkoutSchedulesUseCase: MigrateLegacyWorkoutSchedulesUseCase,
    @ApplicationContext private val appContext: Context,
) : UiStateViewModel<ExerciseLibraryUiState, Unit>() {

    private val groupedExercises = MutableStateFlow<Map<BodyRegion, List<Exercise>>>(emptyMap())
    private val filterState = MutableStateFlow(ExerciseLibraryUiState())
    private val detailExerciseId = MutableStateFlow<String?>(null)
    private val zoneId: ZoneId = ZoneId.systemDefault()

    /** Busy intervals for the active booking context (same emission as [ExerciseLibraryUiState.bookingBusyIntervals]). */
    private var latestBookingBusy: ImmutableList<InstantInterval> = persistentListOf()

    private var cachedSections: ImmutableList<ExerciseSectionUiItem> = persistentListOf()
    private var cachedMeasurementById: ImmutableMap<String, ExerciseMeasurementMode> = persistentMapOf()
    private var lastSectionRebuildKey: SectionRebuildKey? = null

    private data class SectionRebuildKey(
        val normalizedQuery: String,
        val category: ExerciseCategory?,
        val equipment: EquipmentType?,
        val cartKeySignature: String,
        val libraryIdentity: Int,
    )

    private fun sectionRebuildKey(
        grouped: Map<BodyRegion, List<Exercise>>,
        filters: ExerciseLibraryUiState,
    ): SectionRebuildKey {
        val cartSig = buildString {
            filters.itemDrafts.keys.sorted().forEach { append(it).append(',') }
            append('|')
            append(filters.activeExerciseId ?: "")
        }
        return SectionRebuildKey(
            normalizedQuery = normalizeExerciseLibraryQuery(filters.searchQuery),
            category = filters.selectedExerciseCategory,
            equipment = filters.selectedEquipment,
            cartKeySignature = cartSig,
            libraryIdentity = System.identityHashCode(grouped),
        )
    }

    private val bookingGridSlotStarts: List<LocalTime> = bookingSlotStartsForDay(
        firstSlot = SESSION_BOOKING_GRID_FIRST_SLOT,
        lastSlot = SESSION_BOOKING_GRID_LAST_SLOT,
        slotStepMinutes = SESSION_BOOKING_SLOT_STEP_MINUTES,
    )

    init {
        viewModelScope.launch {
            migrateLegacyWorkoutSchedulesUseCase(zoneId)
        }

        getExerciseLibraryUseCase()
            .onEach { groupedExercises.value = it }
            .catch { setError(it.message ?: "Unknown error") }
            .launchIn(viewModelScope)

        val gymLocationsFlow = observeGymLocationsUseCase().map { it.toImmutableList() }
        val bookingBusyFlow = filterState.flatMapLatest { filters ->
            val inp = filters.sessionBookingInput
            if (inp == null) {
                flow {
                    emit(persistentListOf())
                    awaitCancellation()
                }
            } else {
                val date = Instant.ofEpochMilli(inp.selectedDateMillis).atZone(zoneId).toLocalDate()
                observeBusyIntervalsUseCase(date, zoneId, inp.selectedLocationId)
                    .map { intervals -> intervals.toPersistentList() }
            }
        }

        combine(
            groupedExercises,
            filterState,
            detailExerciseId,
            gymLocationsFlow,
            bookingBusyFlow,
        ) { grouped, filters, selectedId, gymLocs, busy ->
            latestBookingBusy = busy
            val bookingIn = filters.sessionBookingInput
            val filtersForUi = if (bookingIn == null) {
                filters
            } else {
                val synced = mergeBookingInputWithBusy(bookingIn, busy, zoneId)!!
                if (synced != bookingIn) {
                    filterState.update { s ->
                        if (s.sessionBookingInput == bookingIn) {
                            s.copy(sessionBookingInput = synced)
                        } else {
                            s
                        }
                    }
                    filters.copy(sessionBookingInput = synced)
                } else {
                    filters
                }
            }
            val sectionKey = sectionRebuildKey(grouped, filtersForUi)
            val sections: ImmutableList<ExerciseSectionUiItem>
            val measurementById: ImmutableMap<String, ExerciseMeasurementMode>
            if (sectionKey == lastSectionRebuildKey) {
                sections = cachedSections
                measurementById = cachedMeasurementById
            } else {
                sections = buildSections(grouped, filtersForUi)
                measurementById = grouped.values.flatten()
                    .associate { it.id to it.measurementMode }
                    .toImmutableMap()
                cachedSections = sections
                cachedMeasurementById = measurementById
                lastSectionRebuildKey = sectionKey
            }
            val selectedExercise = selectedId?.let { id ->
                grouped.values.flatten().find { it.id == id }
            }
            val filtersWithMeasurement = filtersForUi.copy(exerciseMeasurementById = measurementById)
            val bookingUi = filtersWithMeasurement.sessionBookingInput?.let { input ->
                buildSessionBookingUiModel(
                    input,
                    gymLocs,
                    busy,
                    zoneId,
                    filtersWithMeasurement.isCartDraftValidForSessionConfirm(),
                )
            }
            filtersForUi.copy(
                sections = sections,
                selectedExerciseForDetail = selectedExercise,
                exerciseMeasurementById = measurementById,
                sessionBooking = bookingUi,
                bookingBusyIntervals = busy,
                isAddToSessionEnabled = filtersWithMeasurement.canOpenBooking(),
            )
        }.onEach { fullState ->
            setSuccess(fullState)
        }.launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        filterState.update { it.copy(searchQuery = query) }
    }

    fun selectQuickChip(chip: ExerciseLibraryQuickChip?) {
        filterState.update { state ->
            when (chip) {
                null -> state.copy(selectedExerciseCategory = null, selectedEquipment = null)
                ExerciseLibraryQuickChip.Strength -> state.copy(
                    selectedExerciseCategory = ExerciseCategory.Strength,
                    selectedEquipment = null,
                )
                ExerciseLibraryQuickChip.Mobility -> state.copy(
                    selectedExerciseCategory = ExerciseCategory.Mobility,
                    selectedEquipment = null,
                )
                ExerciseLibraryQuickChip.Cardio -> state.copy(
                    selectedExerciseCategory = ExerciseCategory.Cardio,
                    selectedEquipment = null,
                )
                ExerciseLibraryQuickChip.Bodyweight -> state.copy(
                    selectedExerciseCategory = null,
                    selectedEquipment = EquipmentType.Bodyweight,
                )
            }
        }
    }

    /** Main list short-press: add to cart or remove if already selected ([itemDrafts]). */
    fun toggleExerciseInCartFromList(exerciseId: String) {
        filterState.update { state ->
            val base = state.itemDrafts.toPersistentMap()
            if (exerciseId !in base) {
                val nextDrafts = base.put(exerciseId, ExerciseDraft())
                val nextOrder = if (exerciseId in state.draftOrder) {
                    state.draftOrder
                } else {
                    state.draftOrder.toPersistentList().add(exerciseId)
                }
                state.copy(
                    itemDrafts = nextDrafts,
                    draftOrder = nextOrder,
                    activeExerciseId = exerciseId,
                )
            } else {
                val nextDrafts = base.remove(exerciseId)
                val orderBefore = state.draftOrder
                val nextOrder = state.draftOrder.filter { it != exerciseId }.toPersistentList()
                val nextActive = resolveActiveExerciseAfterRemoval(
                    removedId = exerciseId,
                    previousActive = state.activeExerciseId,
                    newDrafts = nextDrafts,
                    orderBeforeRemoval = orderBefore,
                    newOrder = nextOrder,
                )
                state.copy(
                    itemDrafts = nextDrafts,
                    draftOrder = nextOrder,
                    activeExerciseId = nextActive,
                )
            }
        }
    }

    /**
     * Detail sheet "Add": never removes. Inserts with empty draft if absent, else only focuses.
     */
    fun ensureInCartAndFocusFromDetail(exerciseId: String) {
        filterState.update { state ->
            val base = state.itemDrafts.toPersistentMap()
            if (exerciseId !in base) {
                val nextDrafts = base.put(exerciseId, ExerciseDraft())
                val nextOrder = if (exerciseId in state.draftOrder) {
                    state.draftOrder
                } else {
                    state.draftOrder.toPersistentList().add(exerciseId)
                }
                state.copy(
                    itemDrafts = nextDrafts,
                    draftOrder = nextOrder,
                    activeExerciseId = exerciseId,
                )
            } else {
                state.copy(activeExerciseId = exerciseId)
            }
        }
    }

    /** Cart thumbnail remove ([X]); does not run for thumbnail body (focus only). */
    fun removeFromCart(exerciseId: String) {
        filterState.update { state ->
            if (exerciseId !in state.itemDrafts) return@update state
            val base = state.itemDrafts.toPersistentMap()
            val nextDrafts = base.remove(exerciseId)
            val orderBefore = state.draftOrder
            val nextOrder = state.draftOrder.filter { it != exerciseId }.toPersistentList()
            val nextActive = resolveActiveExerciseAfterRemoval(
                removedId = exerciseId,
                previousActive = state.activeExerciseId,
                newDrafts = nextDrafts,
                orderBeforeRemoval = orderBefore,
                newOrder = nextOrder,
            )
            state.copy(
                itemDrafts = nextDrafts,
                draftOrder = nextOrder,
                activeExerciseId = nextActive,
            )
        }
    }

    fun setActiveCartExercise(exerciseId: String) {
        filterState.update { state ->
            if (exerciseId in state.itemDrafts) {
                state.copy(activeExerciseId = exerciseId)
            } else {
                state
            }
        }
    }

    fun clearAll() {
        filterState.update {
            it.copy(
                itemDrafts = persistentMapOf(),
                draftOrder = persistentListOf(),
                activeExerciseId = null,
                sessionBookingInput = null,
                addExerciseSuccess = null,
            )
        }
    }

    fun updateActiveDraft(sets: String, reps: String) {
        filterState.update { state ->
            val id = state.activeExerciseId ?: return@update state
            val draft = state.itemDrafts[id] ?: return@update state
            state.copy(
                itemDrafts = state.itemDrafts.toPersistentMap().put(
                    id,
                    draft.copy(sets = sets, reps = reps),
                ),
            )
        }
    }

    fun openSessionBooking() {
        filterState.update { s ->
            val exercisesById = groupedExercises.value.values.flatten().associateBy { it.id }
            val snapshot = buildBookingExerciseSnapshot(
                appContext,
                s.draftOrder,
                exercisesById,
                s.itemDrafts,
                s.exerciseMeasurementById,
            )
            s.copy(
                sessionBookingInput = SessionBookingInput(
                    selectedDateMillis = defaultExerciseLibraryCartDateMillis(),
                    selectedLocationId = DEFAULT_SESSION_LOCATION_ID,
                    selectedSlotStarts = persistentSetOf(),
                    bookingExerciseSnapshot = snapshot,
                    longSessionAcknowledged = false,
                    pendingLongSessionWarning = false,
                    isConfirming = false,
                    showSlotConflict = false,
                ),
            )
        }
    }

    fun dismissSessionBooking() {
        filterState.update {
            it.copy(sessionBookingInput = null)
        }
    }

    fun onBookingDateSelected(dateMillis: Long) {
        filterState.update { state ->
            val inp = state.sessionBookingInput ?: return@update state
            state.copy(
                sessionBookingInput = inp.copy(
                    selectedDateMillis = dateMillis,
                    showSlotConflict = false,
                ),
            )
        }
    }

    fun onBookingLocationSelected(locationId: String) {
        filterState.update { state ->
            val inp = state.sessionBookingInput ?: return@update state
            state.copy(
                sessionBookingInput = inp.copy(
                    selectedLocationId = locationId,
                    showSlotConflict = false,
                ),
            )
        }
    }

    fun onBookingSlotToggled(slotStart: LocalTime) {
        filterState.update { state ->
            val inp = state.sessionBookingInput ?: return@update state
            val date = Instant.ofEpochMilli(inp.selectedDateMillis).atZone(zoneId).toLocalDate()
            val next = computeNextSlotSelectionAfterToggle(
                current = inp.selectedSlotStarts,
                tapped = slotStart,
                busyIntervals = latestBookingBusy,
                date = date,
                zoneId = zoneId,
                gridSlotStarts = bookingGridSlotStarts,
            ) ?: return@update state
            val selectionChanged = next != inp.selectedSlotStarts
            state.copy(
                sessionBookingInput = inp.copy(
                    selectedSlotStarts = next.toPersistentSet(),
                    longSessionAcknowledged = if (selectionChanged) false else inp.longSessionAcknowledged,
                    pendingLongSessionWarning = false,
                    showSlotConflict = false,
                ),
            )
        }
    }

    fun onBookingClearTimeSelection() {
        filterState.update { state ->
            val inp = state.sessionBookingInput ?: return@update state
            if (inp.selectedSlotStarts.isEmpty()) return@update state
            state.copy(
                sessionBookingInput = inp.copy(
                    selectedSlotStarts = persistentSetOf(),
                    longSessionAcknowledged = false,
                    pendingLongSessionWarning = false,
                    showSlotConflict = false,
                ),
            )
        }
    }

    fun onLongSessionEdit() {
        filterState.update { state ->
            val inp = state.sessionBookingInput ?: return@update state
            state.copy(sessionBookingInput = inp.copy(pendingLongSessionWarning = false))
        }
    }

    fun onLongSessionProceedAnyway() {
        filterState.update { state ->
            val inp = state.sessionBookingInput ?: return@update state
            state.copy(
                sessionBookingInput = inp.copy(
                    pendingLongSessionWarning = false,
                    longSessionAcknowledged = true,
                ),
            )
        }
        confirmSessionBooking()
    }

    fun confirmSessionBooking() {
        launchSafely {
            val filters = filterState.value
            val input = filters.sessionBookingInput ?: return@launchSafely
            if (input.selectedSlotStarts.isEmpty()) return@launchSafely
            if (!filters.isCartDraftValidForSessionConfirm()) return@launchSafely
            val date = Instant.ofEpochMilli(input.selectedDateMillis).atZone(zoneId).toLocalDate()
            val orderedSlots = input.selectedSlotStarts.sorted()
            if (!isContiguousThirtyMinuteChain(orderedSlots)) return@launchSafely
            val minSlot = orderedSlots.first()
            val maxSlot = orderedSlots.last()
            val proposedInterval = proposedVariableSessionInterval(
                date = date,
                minSlot = minSlot,
                maxSlot = maxSlot,
                zoneId = zoneId,
            )
            if (!isIntervalFreeForBooking(proposedInterval, latestBookingBusy)) return@launchSafely
            if (shouldWarnLongSession(input.selectedSlotStarts.size) && !input.longSessionAcknowledged) {
                filterState.update { s ->
                    val inp = s.sessionBookingInput ?: return@update s
                    s.copy(sessionBookingInput = inp.copy(pendingLongSessionWarning = true))
                }
                return@launchSafely
            }

            filterState.update { s ->
                val inp = s.sessionBookingInput ?: return@update s
                s.copy(
                    sessionBookingInput = inp.copy(
                        isConfirming = true,
                        showSlotConflict = false,
                        pendingLongSessionWarning = false,
                    ),
                )
            }

            val exercisesById = groupedExercises.value.values.flatten().associateBy { it.id }
            val session = WorkoutSession(
                id = UUID.randomUUID().toString(),
                startInstant = proposedInterval.start,
                endInstant = proposedInterval.end,
                locationId = input.selectedLocationId,
            )
            val order = filters.draftOrder
            val lines = mutableListOf<SessionExerciseLine>()
            for ((idx, exerciseId) in order.withIndex()) {
                val draft = filters.itemDrafts[exerciseId] ?: continue
                val ex = exercisesById[exerciseId] ?: continue
                when (ex.measurementMode) {
                    ExerciseMeasurementMode.Strength -> {
                        val sets = draft.sets.trim().toIntOrNull() ?: continue
                        val reps = draft.reps.trim().toIntOrNull() ?: continue
                        if (sets <= 0 || reps <= 0) continue
                        lines.add(
                            SessionExerciseLine(
                                exerciseId = exerciseId,
                                sets = sets,
                                reps = reps,
                                weightKg = ex.lastWeightKg ?: 0.0,
                                restSeconds = 90,
                                notes = null,
                                measurementMode = ExerciseMeasurementMode.Strength,
                                durationSeconds = null,
                                orderIndex = idx,
                            ),
                        )
                    }
                    ExerciseMeasurementMode.Duration -> {
                        val minutes = draft.sets.trim().toIntOrNull() ?: 0
                        val seconds = draft.reps.trim().toIntOrNull() ?: 0
                        val total = normalizeDurationMinutesSeconds(minutes, seconds)
                        if (total <= 0) continue
                        lines.add(
                            SessionExerciseLine(
                                exerciseId = exerciseId,
                                sets = 1,
                                reps = 0,
                                weightKg = ex.lastWeightKg ?: 0.0,
                                restSeconds = 90,
                                notes = null,
                                measurementMode = ExerciseMeasurementMode.Duration,
                                durationSeconds = total,
                                orderIndex = idx,
                            ),
                        )
                    }
                }
            }
            if (lines.isEmpty()) {
                filterState.update { s ->
                    val inp = s.sessionBookingInput ?: return@update s
                    s.copy(sessionBookingInput = inp.copy(isConfirming = false))
                }
                return@launchSafely
            }

            when (val result = bookWorkoutSessionUseCase(session, lines, zoneId)) {
                BookWorkoutSessionResult.Conflict -> {
                    filterState.update { s ->
                        val inp = s.sessionBookingInput ?: return@update s
                        s.copy(
                            sessionBookingInput = inp.copy(
                                isConfirming = false,
                                showSlotConflict = true,
                            ),
                        )
                    }
                }
                BookWorkoutSessionResult.InvalidDraft -> {
                    filterState.update { s ->
                        val inp = s.sessionBookingInput ?: return@update s
                        s.copy(sessionBookingInput = inp.copy(isConfirming = false))
                    }
                }
                is BookWorkoutSessionResult.Success -> {
                    val snap = filterState.value
                    val bookingInput = snap.sessionBookingInput ?: input
                    val locLabel = snap.sessionBooking?.locations
                        ?.find { it.id == bookingInput.selectedLocationId }?.displayName
                        ?: bookingInput.selectedLocationId
                    val firstId = snap.draftOrder.firstOrNull()
                    val primaryTitle = firstId?.let { id ->
                        bookingInput.bookingExerciseSnapshot.find { it.id == id }?.title
                            ?.takeIf { it.isNotBlank() }
                            ?: exercisesById[id]?.name?.takeIf { it.isNotBlank() }
                    }
                    val summary = AddExerciseSuccessSummary(
                        sessionStartInstant = session.startInstant,
                        sessionEndInstant = session.endInstant,
                        scheduledDateMillis = bookingInput.selectedDateMillis,
                        exerciseCount = result.scheduledCount,
                        primaryExerciseTitle = primaryTitle,
                        locationDisplayName = locLabel,
                    )
                    filterState.update {
                        it.copy(
                            itemDrafts = persistentMapOf(),
                            draftOrder = persistentListOf(),
                            activeExerciseId = null,
                            sessionBookingInput = null,
                            addExerciseSuccess = summary,
                            workoutPlanFabBadgeCount = it.workoutPlanFabBadgeCount + result.scheduledCount,
                        )
                    }
                }
            }
        }
    }

    fun onWorkoutPlanFabClick() {
        Toast.makeText(
            appContext,
            appContext.getString(R.string.exercise_library_workout_plan_stub_toast),
            Toast.LENGTH_SHORT,
        ).show()
    }

    fun dismissAddExerciseSuccess() {
        filterState.update { it.copy(addExerciseSuccess = null) }
    }

    fun selectExerciseForDetail(exerciseId: String) {
        detailExerciseId.value = exerciseId
    }

    fun clearExerciseDetail() {
        detailExerciseId.value = null
    }

    private fun buildSections(
        grouped: Map<BodyRegion, List<Exercise>>,
        filters: ExerciseLibraryUiState,
    ): ImmutableList<ExerciseSectionUiItem> {
        val query = normalizeExerciseLibraryQuery(filters.searchQuery)
        val category = filters.selectedExerciseCategory
        val equipment = filters.selectedEquipment
        val cartIds = filters.itemDrafts.keys
        val activeId = filters.activeExerciseId
        return BodyRegion.entries.mapNotNull { region ->
            val items = grouped[region]
                ?.filter { ex ->
                    ex.matchesLibrarySearch(query) &&
                        (category == null || ex.category == category) &&
                        (equipment == null || ex.equipment == equipment)
                }
                ?.map { ex ->
                    ex.toLibraryCardUiModel(appContext, cartIds, activeId)
                }
                ?: emptyList()
            if (items.isEmpty()) null else ExerciseSectionUiItem(region, items.toImmutableList())
        }.toImmutableList()
    }
}
