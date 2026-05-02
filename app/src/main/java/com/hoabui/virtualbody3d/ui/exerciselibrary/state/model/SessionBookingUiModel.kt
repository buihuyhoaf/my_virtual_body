package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentSet
import java.time.LocalTime

@Immutable
data class SessionBookingUiModel(
    val selectedDateMillis: Long,
    val selectedSlotStarts: PersistentSet<LocalTime>,
    val locations: ImmutableList<GymLocation>,
    val timeSlotCells: ImmutableList<TimeSlotCellUiModel>,
    val bookingPeriods: ImmutableList<SessionBookingPeriodUiModel>,
    val periodStartIndex: ImmutableMap<SessionBookingPeriodId, Int>,
    /** Precomputed in ViewModel: confirm rules + cart draft validity + not confirming. */
    val isBookingConfirmEnabled: Boolean = false,
    val selectedLocationDisplayName: String = "",
)
