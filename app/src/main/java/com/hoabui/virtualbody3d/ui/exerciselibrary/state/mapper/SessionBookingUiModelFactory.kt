package com.hoabui.virtualbody3d.ui.exerciselibrary.state.mapper

import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_FIRST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_LAST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_PERIOD_AFTERNOON_START
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_PERIOD_MIDDAY_START
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_SLOT_STEP_MINUTES
import com.hoabui.virtualbody3d.domain.model.exercise.SlotDensityKernel
import com.hoabui.virtualbody3d.domain.model.exercise.bookingSlotStartsForDay
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingInput
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingPeriodId
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingPeriodUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.TimeSlotCellUiModel
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    slotDensityKernels: List<SlotDensityKernel>,
    isBookingConfirmEnabled: Boolean,
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
    return SessionBookingUiModel(
        input = input,
        locations = locations.toImmutableList(),
        timeSlotCells = cells.toImmutableList(),
        bookingPeriods = periods.toImmutableList(),
        periodStartIndex = computePeriodStartIndices(cells, periods),
        isBookingConfirmEnabled = isBookingConfirmEnabled,
        selectedLocationDisplayName = locationDisplay,
    )
}
