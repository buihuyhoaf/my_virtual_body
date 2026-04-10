package com.hoabui.virtualbody3d.ui.exerciselibrary.presentation

import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.model.exercise.InstantInterval
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import java.time.Instant
import java.time.ZoneId
import kotlinx.collections.immutable.ImmutableList

/**
 * Cache key for booking sheet + density projection. Invalidates when booking inputs, busy
 * intervals, or schedule rows affecting the selected day/location change.
 *
 * [schedulesSignature] uses only schedules for the selected local date and location—the same subset
 * as in [com.hoabui.virtualbody3d.domain.usecase.BuildLibraryBookingDensityKernelsUseCase]. Search
 * query is omitted so search-only edits do not invalidate this key.
 *
 * ViewModel `bookingProjectionFlow` applies `distinctUntilChanged` on this key, then `buildBookingUi`
 * invokes `BuildLibraryBookingDensityKernelsUseCase`, which delegates to `CalculateBookingDensityUseCase`.
 */
@Immutable
internal data class ExerciseLibraryBookingPresentationKey(
    val bookingInputSignature: String,
    val cartDraftSignature: String,
    val measurementSignature: String,
    val busySignature: String,
    val schedulesSignature: String,
    /** Sorted gym ids and display names; stable when the underlying gyms are unchanged. */
    val gymLocationsContentSignature: String,
)

internal fun exerciseLibraryBookingPresentationKey(
    filtersWithMeasurement: ExerciseLibraryUiState,
    busy: ImmutableList<InstantInterval>,
    schedulesVersion: ImmutableList<WorkoutSchedule>,
    gymLocationsVersion: ImmutableList<GymLocation>,
    zoneId: ZoneId,
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
    val busySignature = busy.joinToString(separator = ";") { "${it.start}|${it.end}" }
    val selectedDate = Instant.ofEpochMilli(inp.selectedDateMillis).atZone(zoneId).toLocalDate()
    val daySchedulesAtLocation = schedulesVersion.filter { sch ->
        sch.locationId == inp.selectedLocationId &&
            sch.scheduledAt.atZone(zoneId).toLocalDate() == selectedDate
    }
    val schedulesSignature = daySchedulesAtLocation
        .sortedBy { it.id }
        .joinToString(separator = ";") { sch ->
            "${sch.id}|${sch.locationId}|${sch.scheduledAt}|${sch.exerciseId}"
        }
    val gymLocationsContentSignature = gymLocationsVersion
        .sortedBy { it.id }
        .joinToString(separator = ";") { loc -> "${loc.id}|${loc.displayName}" }
    return ExerciseLibraryBookingPresentationKey(
        bookingInputSignature = bookingInputSignature,
        cartDraftSignature = cartDraftSignature,
        measurementSignature = measurementSignature,
        busySignature = busySignature,
        schedulesSignature = schedulesSignature,
        gymLocationsContentSignature = gymLocationsContentSignature,
    )
}
