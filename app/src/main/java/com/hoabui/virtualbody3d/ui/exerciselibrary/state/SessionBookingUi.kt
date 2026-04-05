package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import android.content.Context
import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.model.exercise.parseCartDurationTotalSecondsForSummary
import com.hoabui.virtualbody3d.domain.model.exercise.parseCartStrengthSetsRepsForSummary
import com.hoabui.virtualbody3d.domain.model.exercise.InstantInterval
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_FIRST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_LAST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_PERIOD_AFTERNOON_START
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_PERIOD_MIDDAY_START
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_SLOT_STEP_MINUTES
import com.hoabui.virtualbody3d.domain.model.exercise.bookingSlotStartsForDay
import com.hoabui.virtualbody3d.domain.model.exercise.isContiguousThirtyMinuteChain
import com.hoabui.virtualbody3d.domain.model.exercise.isIntervalFreeForBooking
import com.hoabui.virtualbody3d.domain.model.exercise.SlotDensityKernel
import com.hoabui.virtualbody3d.domain.model.exercise.SlotDensityTier
import com.hoabui.virtualbody3d.domain.model.exercise.pruneSelectionAgainstBusy
import com.hoabui.virtualbody3d.domain.model.exercise.proposedVariableSessionInterval
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.ExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toExerciseLibraryCardImage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentSet
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class SessionBookingPeriodId {
    Morning,
    Midday,
    AfternoonEvening,
}

@Immutable
data class SessionBookingPeriodUiModel(
    val id: SessionBookingPeriodId,
    val labelResId: Int,
    val periodStartInclusive: LocalTime,
)

@Immutable
data class BookingExerciseSummaryUi(
    val id: String,
    val title: String,
    val image: ExerciseLibraryCardImage,
    val orderIndex: Int,
    /** Preformatted at sheet open (sets×reps or duration); empty when draft not displayable. */
    val parametersSummary: String,
)

@Immutable
data class SessionBookingInput(
    val selectedDateMillis: Long,
    val selectedLocationId: String,
    val selectedSlotStarts: PersistentSet<LocalTime>,
    val bookingExerciseSnapshot: ImmutableList<BookingExerciseSummaryUi>,
    val longSessionAcknowledged: Boolean,
    val pendingLongSessionWarning: Boolean,
    val isConfirming: Boolean,
    val showSlotConflict: Boolean,
)

@Immutable
data class TimeSlotCellUiModel(
    val slotStart: LocalTime,
    val label: String,
    val selected: Boolean,
    val densityTier: SlotDensityTier,
    val overCapacity: Boolean,
    val utilizationRatio: Float,
)

@Immutable
data class SessionBookingUiModel(
    val input: SessionBookingInput,
    val locations: ImmutableList<GymLocation>,
    val timeSlotCells: ImmutableList<TimeSlotCellUiModel>,
    val bookingPeriods: ImmutableList<SessionBookingPeriodUiModel>,
    val periodStartIndex: ImmutableMap<SessionBookingPeriodId, Int>,
    /** Precomputed in ViewModel: confirm rules + cart draft validity + not confirming. */
    val isBookingConfirmEnabled: Boolean = false,
    val selectedLocationDisplayName: String = "",
)

private val slotLabelFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

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
    busyIntervals: List<InstantInterval>,
    zoneId: ZoneId,
    isCartDraftValidForConfirm: Boolean,
    slotDensityKernels: List<SlotDensityKernel>,
): SessionBookingUiModel {
    val slotStarts = bookingSlotStartsForDay(
        firstSlot = SESSION_BOOKING_GRID_FIRST_SLOT,
        lastSlot = SESSION_BOOKING_GRID_LAST_SLOT,
        slotStepMinutes = SESSION_BOOKING_SLOT_STEP_MINUTES,
    )
    require(slotDensityKernels.size == slotStarts.size) {
        "slotDensityKernels must align with grid (${slotDensityKernels.size} != ${slotStarts.size})"
    }
    val kernelBySlot = slotDensityKernels.associateBy { it.slotStart }
    val periods = defaultBookingPeriods()
    val cells = slotStarts.map { slot ->
        val selected = slot in input.selectedSlotStarts
        val kernel = kernelBySlot[slot]
            ?: error("missing SlotDensityKernel for $slot")
        TimeSlotCellUiModel(
            slotStart = slot,
            label = slotLabelFormatter.format(slot),
            selected = selected,
            densityTier = kernel.densityTier,
            overCapacity = kernel.overCapacity,
            utilizationRatio = kernel.utilizationRatio,
        )
    }
    val locationDisplay = locations.find { it.id == input.selectedLocationId }?.displayName
        ?: input.selectedLocationId
    val baseModel = SessionBookingUiModel(
        input = input,
        locations = locations.toImmutableList(),
        timeSlotCells = cells.toImmutableList(),
        bookingPeriods = periods.toImmutableList(),
        periodStartIndex = computePeriodStartIndices(cells, periods),
        isBookingConfirmEnabled = false,
        selectedLocationDisplayName = locationDisplay,
    )
    val confirm = baseModel.confirmEnabled(zoneId, busyIntervals) &&
        isCartDraftValidForConfirm &&
        !input.isConfirming
    return baseModel.copy(isBookingConfirmEnabled = confirm)
}

fun ExerciseLibraryUiState.canOpenBooking(): Boolean =
    itemDrafts.isNotEmpty() && isCartDraftValidForSessionConfirm()

fun SessionBookingUiModel.confirmEnabled(
    zoneId: ZoneId,
    busyIntervals: List<InstantInterval>,
): Boolean {
    if (input.selectedSlotStarts.isEmpty()) return false
    if (input.selectedLocationId.isBlank()) return false
    val date = Instant.ofEpochMilli(input.selectedDateMillis).atZone(zoneId).toLocalDate()
    val ordered = input.selectedSlotStarts.sorted()
    if (!isContiguousThirtyMinuteChain(ordered)) return false
    val minS = ordered.first()
    val maxS = ordered.last()
    val proposed = proposedVariableSessionInterval(date, minS, maxS, zoneId)
    return isIntervalFreeForBooking(proposed, busyIntervals)
}

fun mergeBookingInputWithBusy(
    input: SessionBookingInput?,
    busy: List<InstantInterval>,
    zoneId: ZoneId,
): SessionBookingInput? {
    if (input == null) return null
    val date = Instant.ofEpochMilli(input.selectedDateMillis).atZone(zoneId).toLocalDate()
    val pruned = pruneSelectionAgainstBusy(
        input.selectedSlotStarts,
        busy,
        date,
        zoneId,
    ).toPersistentSet()
    if (pruned == input.selectedSlotStarts) return input
    return input.copy(
        selectedSlotStarts = pruned,
        longSessionAcknowledged = false,
        showSlotConflict = false,
    )
}

private fun formatBookingExerciseParametersSummary(
    context: Context,
    draft: ExerciseDraft,
    mode: ExerciseMeasurementMode,
): String =
    when (mode) {
        ExerciseMeasurementMode.Strength -> {
            val pair = parseCartStrengthSetsRepsForSummary(draft.sets, draft.reps) ?: return ""
            context.getString(
                R.string.exercise_library_booking_param_strength,
                pair.first,
                pair.second,
            )
        }
        ExerciseMeasurementMode.Duration -> {
            val total = parseCartDurationTotalSecondsForSummary(draft.sets, draft.reps) ?: return ""
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
