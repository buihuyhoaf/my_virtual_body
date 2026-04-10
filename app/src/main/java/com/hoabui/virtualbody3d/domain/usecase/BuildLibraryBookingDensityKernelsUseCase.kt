package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryCartDraft
import com.hoabui.virtualbody3d.domain.model.exercise.SlotDensityKernel
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.model.exercise.buildSessionExerciseLinesFromLibraryCart
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

/**
 * Builds slot density kernels for the exercise-library booking grid from schedules, cart drafts,
 * and the selected day/location (orchestration around [CalculateBookingDensityUseCase]).
 */
class BuildLibraryBookingDensityKernelsUseCase @Inject constructor(
    private val calculateBookingDensityUseCase: CalculateBookingDensityUseCase,
) {
    operator fun invoke(
        selectedDateMillis: Long,
        selectedLocationId: String,
        selectedSlotStarts: Set<LocalTime>,
        cart: LibraryCartDraft,
        exercisesById: Map<String, Exercise>,
        schedules: List<WorkoutSchedule>,
        zoneId: ZoneId,
        bookingGridSlotStarts: List<LocalTime>,
    ): List<SlotDensityKernel> {
        val date = Instant.ofEpochMilli(selectedDateMillis).atZone(zoneId).toLocalDate()
        val daySchedules = schedules.filter { sch ->
            sch.locationId == selectedLocationId &&
                sch.scheduledAt.atZone(zoneId).toLocalDate() == date
        }
        val draftLines = buildSessionExerciseLinesFromLibraryCart(
            cart = cart,
            exercisesById = exercisesById,
        )
        val anchor = selectedSlotStarts.minOrNull()
        return calculateBookingDensityUseCase(
            date = date,
            zoneId = zoneId,
            locationId = selectedLocationId,
            slotStarts = bookingGridSlotStarts,
            schedulesForDayAtLocation = daySchedules,
            draftLines = draftLines,
            draftAnchorSlot = anchor,
        )
    }
}
