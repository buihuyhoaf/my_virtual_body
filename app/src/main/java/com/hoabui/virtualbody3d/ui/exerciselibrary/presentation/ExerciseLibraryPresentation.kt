package com.hoabui.virtualbody3d.ui.exerciselibrary.presentation

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeExerciseLibraryQuery
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.ExerciseLibraryCatalogEntryUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.LibraryPresentationSlice
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentMap

/** Inputs for booking UI projection after dedupe on [ExerciseLibraryBookingPresentationKey]. */
@Immutable
internal data class BookingPipelineRow(
    val dedupeKey: ExerciseLibraryBookingPresentationKey?,
    val filtersWithMeasurement: ExerciseLibraryUiState,
    val gymLocations: ImmutableList<GymLocation>,
)

@Immutable
internal data class ExerciseLibraryBookingPresentationKey(
    val bookingInputSignature: String,
    val cartDraftSignature: String,
    val measurementSignature: String,
    val gymLocationsContentSignature: String,
)

internal fun exerciseLibraryBookingPresentationKey(
    filtersWithMeasurement: ExerciseLibraryUiState,
    gymLocationsVersion: ImmutableList<GymLocation>,
): ExerciseLibraryBookingPresentationKey {
    val inp = filtersWithMeasurement.sessionBookingInput!!
    val bookingInputSignature = buildString {
        append(inp.selectedDateMillis)
        append('|')
        append(inp.selectedLocationId)
        append('|')
        append(inp.selectedSlotStarts.sorted().joinToString(","))
        append('|')
        append(inp.longSessionAcknowledged)
        append('|')
        append(inp.isConfirming)
    }
    val cartDraftSignature = buildString {
        filtersWithMeasurement.draftOrder.forEach { append(it).append(',') }
        append('|')
        filtersWithMeasurement.itemDrafts.forEach { (k, d) ->
            val sets = d.setRows.size.toString()
            val reps = (d.setRows.firstOrNull()?.reps ?: 0).toString()
            append(k).append('=').append(sets).append('/').append(reps).append(';')
        }
    }
    val measurementSignature = buildString {
        filtersWithMeasurement.libraryList.exerciseMeasurementById.forEach { (k, m) ->
            append(k).append('=').append(m.name).append(';')
        }
    }
    val gymLocationsContentSignature = gymLocationsVersion
        .sortedBy { it.id }
        .joinToString(separator = ";") { loc -> "${loc.id}|${loc.displayName}" }
    return ExerciseLibraryBookingPresentationKey(
        bookingInputSignature = bookingInputSignature,
        cartDraftSignature = cartDraftSignature,
        measurementSignature = measurementSignature,
        gymLocationsContentSignature = gymLocationsContentSignature,
    )
}

internal fun mergeExerciseLibraryPresentation(
    base: ExerciseLibraryUiState,
    library: LibraryPresentationSlice,
    sessionBookingUiModel: SessionBookingUiModel?,
): ExerciseLibraryUiState =
    base.copy(
        libraryList = base.libraryList.copy(
            sections = library.sections,
            exerciseMeasurementById = library.exerciseMeasurementById,
            isAddToSessionEnabled = library.isAddToSessionEnabled,
        ),
        sessionBookingUiModel = sessionBookingUiModel,
    )

@Immutable
internal data class ExerciseLibrarySectionRebuildKey(
    val normalizedQuery: String,
    val category: ExerciseCategory?,
    val bodyRegionsSignature: String,
    val equipment: EquipmentType?,
    val cartKeySignature: String,
    val catalogContentSignature: String,
)

internal fun exerciseLibrarySectionRebuildKey(
    catalogGroupedByRegion: PersistentMap<BodyRegion, ImmutableList<ExerciseLibraryCatalogEntryUiModel>>,
    filters: ExerciseLibraryUiState,
): ExerciseLibrarySectionRebuildKey {
    val cartSig = buildString {
        filters.draftOrder.forEach { append(it).append(',') }
        append('|')
        filters.itemDrafts.forEach { (k, d) ->
            val sets = d.setRows.size.toString()
            val reps = (d.setRows.firstOrNull()?.reps ?: 0).toString()
            append(k).append('=').append(sets).append('/').append(reps).append(';')
        }
        append('|')
        append(filters.activeExerciseId ?: "")
    }
    return ExerciseLibrarySectionRebuildKey(
        normalizedQuery = normalizeExerciseLibraryQuery(filters.searchQuery),
        category = filters.selectedExerciseCategory,
        bodyRegionsSignature = filters.selectedBodyRegions
            ?.sortedBy { it.ordinal }
            ?.joinToString(separator = ",") { it.name }
            .orEmpty(),
        equipment = filters.selectedEquipment,
        cartKeySignature = cartSig,
        catalogContentSignature = catalogGroupedContentSignature(catalogGroupedByRegion),
    )
}

private fun catalogGroupedContentSignature(
    catalogGroupedByRegion: PersistentMap<BodyRegion, ImmutableList<ExerciseLibraryCatalogEntryUiModel>>,
): String = buildString {
    for (region in catalogGroupedByRegion.keys.sortedBy { it.name }) {
        val ids = catalogGroupedByRegion[region].orEmpty().map { it.id }.sorted()
        append(region.name).append(':').append(ids.joinToString(",")).append('|')
    }
}
