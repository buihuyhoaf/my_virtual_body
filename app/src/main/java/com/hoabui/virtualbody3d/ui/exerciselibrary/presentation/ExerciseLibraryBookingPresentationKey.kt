package com.hoabui.virtualbody3d.ui.exerciselibrary.presentation

import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import kotlinx.collections.immutable.ImmutableList

/**
 * Cache key for booking sheet projection. Invalidates when booking inputs, cart drafts,
 * measurement modes, or gym list content change. Search query is omitted so search-only edits
 * do not invalidate this key.
 *
 * ViewModel `bookingProjectionFlow` applies `distinctUntilChanged` on this key before
 * `mapBookingPresentation` / `buildSessionBookingUiModel`.
 */
@Immutable
internal data class ExerciseLibraryBookingPresentationKey(
    val bookingInputSignature: String,
    val cartDraftSignature: String,
    val measurementSignature: String,
    /** Sorted gym ids and display names; stable when the underlying gyms are unchanged. */
    val gymLocationsContentSignature: String,
)

internal fun exerciseLibraryBookingPresentationKey(
    filtersWithMeasurement: ExerciseLibraryUiState,
    gymLocationsVersion: ImmutableList<GymLocation>,
): ExerciseLibraryBookingPresentationKey {
    val inp = filtersWithMeasurement.sessionBooking.input!!
    val bookingInputSignature = buildString {
        append(inp.selectedDateMillis)
        append('|')
        append(inp.selectedLocationId)
        append('|')
        append(inp.selectedSlotStarts.sorted().joinToString(","))
        append('|')
        append(inp.longSessionAcknowledged)
        append('|')
        append(inp.pendingLongSessionWarning)
        append('|')
        append(inp.isConfirming)
        append('|')
        append(inp.showSlotConflict)
    }
    val cartDraftSignature = buildString {
        filtersWithMeasurement.cart.draftOrder.forEach { append(it).append(',') }
        append('|')
        filtersWithMeasurement.cart.itemDrafts.forEach { (k, d) ->
            append(k).append('=').append(d.sets).append('/').append(d.reps).append(';')
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
