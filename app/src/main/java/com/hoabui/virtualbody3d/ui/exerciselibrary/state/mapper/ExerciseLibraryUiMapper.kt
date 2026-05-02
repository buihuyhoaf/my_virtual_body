package com.hoabui.virtualbody3d.ui.exerciselibrary.state.mapper

import android.content.Context
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.DEFAULT_SESSION_LOCATION_ID
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeExerciseLibraryQuery
import com.hoabui.virtualbody3d.domain.usecase.CanOpenExerciseLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.matchesLibrarySearch
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.toExerciseDetailSheetUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.toGExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.toLibraryCartDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.ExerciseLibraryBookingPresentationKey
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.ExerciseLibrarySectionRebuildKey
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.exerciseLibraryBookingPresentationKey
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.exerciseLibrarySectionRebuildKey
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.BookingExerciseSummaryUi
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryChromeMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryCatalogEntryUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibrarySectionRowUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryPresentationSlice
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingInput
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.defaultExerciseLibraryCartDateMillis
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import javax.inject.Inject

/**
 * UI projection: library sections/cards and booking sheet models from catalog + precomputed domain outputs.
 */
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
            state.cart.draftOrder,
            exercisesById,
            state.cart.itemDrafts,
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

    private var cachedSections: ImmutableList<ExerciseLibrarySectionRowUiModel> = persistentListOf()
    private var cachedMeasurementById: ImmutableMap<String, ExerciseMeasurementMode> = persistentMapOf()
    private var lastSectionRebuildKey: ExerciseLibrarySectionRebuildKey? = null

    private var cachedBookingUi: SessionBookingUiModel? = null
    private var lastBookingPresentationKey: ExerciseLibraryBookingPresentationKey? = null

    fun mapLibraryPresentation(
        filtersForUi: ExerciseLibraryUiState,
        exercisesById: Map<String, Exercise>,
    ): LibraryPresentationSlice {
        val catalog = filtersForUi.catalog
        val sectionKey = exerciseLibrarySectionRebuildKey(catalog, filtersForUi)
        val sections: ImmutableList<ExerciseLibrarySectionRowUiModel>
        val measurementById: ImmutableMap<String, ExerciseMeasurementMode>
        if (sectionKey == lastSectionRebuildKey) {
            sections = cachedSections
            measurementById = cachedMeasurementById
        } else {
            sections = buildSections(catalog.catalogGroupedByRegion, filtersForUi)
            measurementById = catalog.catalogGroupedByRegion.values
                .asSequence()
                .flatMap { it }
                .associate { it.id to it.measurementMode }
                .toImmutableMap()
            cachedSections = sections
            cachedMeasurementById = measurementById
            lastSectionRebuildKey = sectionKey
        }
        val selectedDetail = (filtersForUi.chrome.mode as? ExerciseLibraryChromeMode.DetailOpen)?.exerciseId?.let { id ->
            exercisesById[id]?.toExerciseDetailSheetUiModel(appContext)
        }
        return LibraryPresentationSlice(
            sections = sections,
            exerciseMeasurementById = measurementById,
            selectedExerciseForDetail = selectedDetail,
            isAddToSessionEnabled = canOpenExerciseLibrarySessionBooking(
                filtersForUi.toLibraryCartDraft(),
                measurementById,
            ),
        )
    }

    /**
     * Booking sheet projection; skipped when [ExerciseLibraryBookingPresentationKey] unchanged.
     */
    fun mapBookingPresentation(
        filtersWithMeasurement: ExerciseLibraryUiState,
        gymLocations: ImmutableList<GymLocation>,
        isBookingConfirmEnabled: Boolean,
    ): SessionBookingUiModel? {
        filtersWithMeasurement.sessionBooking.input ?: run {
            lastBookingPresentationKey = null
            cachedBookingUi = null
            return null
        }
        val bookingKey = exerciseLibraryBookingPresentationKey(
            filtersWithMeasurement = filtersWithMeasurement,
            gymLocationsVersion = gymLocations,
        )
        if (bookingKey == lastBookingPresentationKey) {
            return cachedBookingUi
        }
        val bookingUi = buildSessionBookingUiModel(
            input = filtersWithMeasurement.sessionBooking.input!!,
            locations = gymLocations,
            isBookingConfirmEnabled = isBookingConfirmEnabled,
        )
        lastBookingPresentationKey = bookingKey
        cachedBookingUi = bookingUi
        return bookingUi
    }

    private fun buildSections(
        grouped: PersistentMap<BodyRegion, ImmutableList<ExerciseLibraryCatalogEntryUiModel>>,
        filters: ExerciseLibraryUiState,
    ): ImmutableList<ExerciseLibrarySectionRowUiModel> {
        val query = normalizeExerciseLibraryQuery(filters.filters.searchQuery)
        val category = filters.filters.selectedExerciseCategory
        val equipment = filters.filters.selectedEquipment
        val cartIds = filters.cart.itemDrafts.keys
        val activeId = filters.cart.activeExerciseId
        return BodyRegion.entries.mapNotNull { region ->
            val items = grouped[region]
                ?.filter { entry ->
                    entry.matchesLibrarySearch(query) &&
                        (category == null || entry.category == category) &&
                        (equipment == null || entry.equipment == equipment)
                }
                ?.map { entry ->
                    entry.toGExerciseCardUiModel(appContext, cartIds, activeId)
                }
                ?: emptyList()
            if (items.isEmpty()) null else ExerciseLibrarySectionRowUiModel(region, items.toImmutableList())
        }.toImmutableList()
    }
}
