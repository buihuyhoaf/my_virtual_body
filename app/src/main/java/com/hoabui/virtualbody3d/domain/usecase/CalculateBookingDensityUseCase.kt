package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.SessionExerciseLine
import com.hoabui.virtualbody3d.domain.model.exercise.SlotDensityKernel
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.model.exercise.estimatedPlannedMinutesForSessionLine
import com.hoabui.virtualbody3d.domain.model.exercise.projectBookingSlotDensityKernels
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

/**
 * Projects slot density (utilization / tiers) for the booking grid from schedules and draft load.
 */
class CalculateBookingDensityUseCase @Inject constructor() {

    operator fun invoke(
        date: LocalDate,
        zoneId: ZoneId,
        locationId: String,
        slotStarts: List<LocalTime>,
        schedulesForDayAtLocation: List<WorkoutSchedule>,
        draftLines: List<SessionExerciseLine>,
        draftAnchorSlot: LocalTime?,
    ): List<SlotDensityKernel> {
        val draftMinutes = draftLines.sumOf { estimatedPlannedMinutesForSessionLine(it) }
        return projectBookingSlotDensityKernels(
            date = date,
            zoneId = zoneId,
            locationId = locationId,
            slotStarts = slotStarts,
            schedules = schedulesForDayAtLocation,
            draftTotalMinutes = draftMinutes,
            draftAnchorSlot = draftAnchorSlot,
        )
    }
}
