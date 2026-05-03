package com.hoabui.virtualbody3d.ui.exerciselibrary.mapper

import android.content.Context
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.DEFAULT_SESSION_LOCATION_ID
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_FIRST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_LAST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_PERIOD_AFTERNOON_START
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_PERIOD_MIDDAY_START
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_SLOT_STEP_MINUTES
import com.hoabui.virtualbody3d.domain.model.exercise.bookingSlotStartsForDay
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeDurationMinutesSeconds
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeExerciseLibraryQuery
import com.hoabui.virtualbody3d.domain.usecase.CanOpenExerciseLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.matchesLibrarySearch
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.toGExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.toLibraryCartDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.ExerciseLibraryCatalogEntryUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.ExerciseLibrarySectionRowUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.LibraryPresentationSlice
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.toExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.BookingExerciseSummaryUi
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingInput
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingPeriodId
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingPeriodUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.TimeSlotCellUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.TimeSlotSelectionRangeRole
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.defaultExerciseLibraryCartDateMillis
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class ExerciseLibraryUiMapper @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val canOpenExerciseLibrarySessionBooking: CanOpenExerciseLibrarySessionBookingUseCase,
) {

    fun buildBookingExerciseSnapshotForOpen(
        state: ExerciseLibraryUiState,
        exercisesById: Map<String, Exercise>,
    ): ImmutableList<BookingExerciseSummaryUi> =
        buildBookingExerciseSnapshot(
            appContext,
            state.draftOrder,
            exercisesById,
            state.itemDrafts,
            state.libraryList.exerciseMeasurementById,
        )

    fun initialSessionBookingInput(
        state: ExerciseLibraryUiState,
        exercisesById: Map<String, Exercise>,
    ): SessionBookingInput =
        SessionBookingInput(
            selectedDateMillis = defaultExerciseLibraryCartDateMillis(),
            selectedLocationId = DEFAULT_SESSION_LOCATION_ID,
            selectedSlotStarts = persistentSetOf(),
            bookingExerciseSnapshot = buildBookingExerciseSnapshotForOpen(state, exercisesById),
            longSessionAcknowledged = false,
            isConfirming = false,
        )

    fun mapLibraryPresentation(
        filtersForUi: ExerciseLibraryUiState,
    ): LibraryPresentationSlice {
        val catalogGrouped = filtersForUi.catalogGroupedByRegion
        val sections = buildSections(catalogGrouped, filtersForUi)
        val measurementById = catalogGrouped.values
            .asSequence()
            .flatMap { it }
            .associate { it.id to it.measurementMode }
            .toImmutableMap()
        return LibraryPresentationSlice(
            sections = sections,
            exerciseMeasurementById = measurementById,
            isAddToSessionEnabled = canOpenExerciseLibrarySessionBooking(
                filtersForUi.toLibraryCartDraft(),
                measurementById,
            ),
        )
    }

    fun mapBookingPresentation(
        filtersWithMeasurement: ExerciseLibraryUiState,
        gymLocations: ImmutableList<GymLocation>,
        isBookingConfirmEnabled: Boolean,
    ): SessionBookingUiModel? {
        val input = filtersWithMeasurement.sessionBookingInput ?: return null
        return buildSessionBookingUiModel(
            input = input,
            locations = gymLocations,
            isBookingConfirmEnabled = isBookingConfirmEnabled,
        )
    }

    private fun buildSections(
        grouped: PersistentMap<BodyRegion, ImmutableList<ExerciseLibraryCatalogEntryUiModel>>,
        filters: ExerciseLibraryUiState,
    ): ImmutableList<ExerciseLibrarySectionRowUiModel> {
        val query = normalizeExerciseLibraryQuery(filters.searchQuery)
        val category = filters.selectedExerciseCategory
        val equipment = filters.selectedEquipment
        val cartIds = filters.itemDrafts.keys
        val activeId = filters.activeExerciseId
        val regions = filters.selectedBodyRegions
        return BodyRegion.entries.mapNotNull { region ->
            val items = grouped[region]
                ?.filter { entry ->
                    entry.matchesLibrarySearch(query) &&
                        (category == null || entry.category == category) &&
                        (equipment == null || entry.equipment == equipment) &&
                        (regions == null || entry.bodyRegion in regions)
                }
                ?.map { entry ->
                    entry.toGExerciseCardUiModel(appContext, cartIds, activeId)
                }
                ?: emptyList()
            if (items.isEmpty()) null else ExerciseLibrarySectionRowUiModel(region, items.toImmutableList())
        }.toImmutableList()
    }
}

fun buildBookingExerciseSnapshot(
    context: Context,
    draftOrder: List<String>,
    exercisesById: Map<String, Exercise>,
    itemDrafts: ImmutableMap<String, ExerciseDraft>,
    exerciseMeasurementById: ImmutableMap<String, ExerciseMeasurementMode>,
): ImmutableList<BookingExerciseSummaryUi> =
    draftOrder.mapIndexedNotNull { index, id ->
        val ex = exercisesById[id] ?: return@mapIndexedNotNull null
        val draft = itemDrafts[id] ?: ExerciseDraft()
        val mode = exerciseMeasurementById[id] ?: ex.measurementMode
        val parametersSummary = formatBookingExerciseParametersSummary(context, draft, mode)
        BookingExerciseSummaryUi(
            id = ex.id,
            title = ex.name,
            image = ex.image.toExerciseLibraryCardImage(),
            orderIndex = index,
            parametersSummary = parametersSummary,
        )
    }.toImmutableList()

private fun formatBookingExerciseParametersSummary(
    context: Context,
    draft: ExerciseDraft,
    mode: ExerciseMeasurementMode,
): String =
    when (mode) {
        ExerciseMeasurementMode.Strength -> {
            val sets = draft.setRows.size
            val reps = draft.setRows.firstOrNull()?.reps ?: 0
            if (sets <= 0 || reps <= 0) ""
            else context.getString(R.string.exercise_library_booking_param_strength, sets, reps)
        }
        ExerciseMeasurementMode.Duration -> {
            val row = draft.setRows.firstOrNull() ?: ExerciseDraft().setRows.first()
            val total = normalizeDurationMinutesSeconds(row.minutes, row.seconds)
            val minutes = total / 60
            val seconds = total % 60
            if (seconds == 0) {
                context.resources.getQuantityString(
                    R.plurals.exercise_library_booking_param_duration_minutes,
                    minutes,
                    minutes,
                )
            } else {
                context.getString(
                    R.string.exercise_library_booking_param_duration_min_sec,
                    minutes,
                    seconds,
                )
            }
        }
    }

private fun defaultBookingPeriods(): List<SessionBookingPeriodUiModel> = listOf(
    SessionBookingPeriodUiModel(
        id = SessionBookingPeriodId.Morning,
        labelResId = R.string.exercise_library_booking_period_morning,
        periodStartInclusive = SESSION_BOOKING_GRID_FIRST_SLOT,
    ),
    SessionBookingPeriodUiModel(
        id = SessionBookingPeriodId.Midday,
        labelResId = R.string.exercise_library_booking_period_midday,
        periodStartInclusive = SESSION_BOOKING_PERIOD_MIDDAY_START,
    ),
    SessionBookingPeriodUiModel(
        id = SessionBookingPeriodId.AfternoonEvening,
        labelResId = R.string.exercise_library_booking_period_afternoon,
        periodStartInclusive = SESSION_BOOKING_PERIOD_AFTERNOON_START,
    ),
)

private fun computePeriodStartIndices(
    cells: List<TimeSlotCellUiModel>,
    periods: List<SessionBookingPeriodUiModel>,
): ImmutableMap<SessionBookingPeriodId, Int> =
    periods.associate { p ->
        val idx = cells.indexOfFirst { !it.slotStart.isBefore(p.periodStartInclusive) }
        p.id to if (idx < 0) cells.lastIndex.coerceAtLeast(0) else idx
    }.toImmutableMap()

fun buildSessionBookingUiModel(
    input: SessionBookingInput,
    locations: List<GymLocation>,
    isBookingConfirmEnabled: Boolean,
): SessionBookingUiModel {
    val slotStarts = bookingSlotStartsForDay(
        firstSlot = SESSION_BOOKING_GRID_FIRST_SLOT,
        lastSlot = SESSION_BOOKING_GRID_LAST_SLOT,
        slotStepMinutes = SESSION_BOOKING_SLOT_STEP_MINUTES,
    )
    val periods = defaultBookingPeriods()
    val slotLabelFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    val orderedSelected = input.selectedSlotStarts.sorted()
    val rangeRoleForSlot: (LocalTime) -> TimeSlotSelectionRangeRole = { slot ->
        when {
            slot !in input.selectedSlotStarts -> TimeSlotSelectionRangeRole.None
            orderedSelected.size == 1 -> TimeSlotSelectionRangeRole.Single
            slot == orderedSelected.first() -> TimeSlotSelectionRangeRole.RangeStart
            slot == orderedSelected.last() -> TimeSlotSelectionRangeRole.RangeEnd
            else -> TimeSlotSelectionRangeRole.RangeMiddle
        }
    }
    val cells = slotStarts.map { slot ->
        val selected = slot in input.selectedSlotStarts
        TimeSlotCellUiModel(
            slotStart = slot,
            label = slotLabelFormatter.format(slot),
            selected = selected,
            rangeRole = if (selected) rangeRoleForSlot(slot) else TimeSlotSelectionRangeRole.None,
        )
    }
    val locationDisplay = locations.find { it.id == input.selectedLocationId }?.displayName
        ?: input.selectedLocationId
    return SessionBookingUiModel(
        selectedDateMillis = input.selectedDateMillis,
        selectedSlotStarts = input.selectedSlotStarts,
        locations = locations.toImmutableList(),
        timeSlotCells = cells.toImmutableList(),
        bookingPeriods = periods.toImmutableList(),
        periodStartIndex = computePeriodStartIndices(cells, periods),
        isBookingConfirmEnabled = isBookingConfirmEnabled,
        selectedLocationDisplayName = locationDisplay,
    )
}
