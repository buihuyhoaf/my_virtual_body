package com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentSet
import java.time.LocalTime

@Immutable
data class SessionBookingInput(
    val selectedDateMillis: Long,
    val selectedLocationId: String,
    val selectedSlotStarts: PersistentSet<LocalTime>,
    val bookingExerciseSnapshot: ImmutableList<BookingExerciseSummaryUi>,
    val longSessionAcknowledged: Boolean,
    val isConfirming: Boolean,
)

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

enum class TimeSlotSelectionRangeRole {
    None,
    Single,
    RangeStart,
    RangeMiddle,
    RangeEnd,
}

@Immutable
data class TimeSlotCellUiModel(
    val slotStart: LocalTime,
    val label: String,
    val selected: Boolean,
    val rangeRole: TimeSlotSelectionRangeRole,
)

@Immutable
data class SessionBookingUiModel(
    val selectedDateMillis: Long,
    val selectedSlotStarts: PersistentSet<LocalTime>,
    val locations: ImmutableList<GymLocation>,
    val timeSlotCells: ImmutableList<TimeSlotCellUiModel>,
    val bookingPeriods: ImmutableList<SessionBookingPeriodUiModel>,
    val periodStartIndex: ImmutableMap<SessionBookingPeriodId, Int>,
    val isBookingConfirmEnabled: Boolean = false,
    val selectedLocationDisplayName: String = "",
)
