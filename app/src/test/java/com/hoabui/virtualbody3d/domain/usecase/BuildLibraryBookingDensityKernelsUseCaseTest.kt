package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryCartDraft
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryExerciseLineDraft
import com.hoabui.virtualbody3d.domain.model.exercise.bookingSlotStartsForDay
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_FIRST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_GRID_LAST_SLOT
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_SLOT_STEP_MINUTES
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId

class BuildLibraryBookingDensityKernelsUseCaseTest {

    private val zoneId = ZoneId.of("UTC")
    private val grid = bookingSlotStartsForDay(
        firstSlot = SESSION_BOOKING_GRID_FIRST_SLOT,
        lastSlot = SESSION_BOOKING_GRID_LAST_SLOT,
        slotStepMinutes = SESSION_BOOKING_SLOT_STEP_MINUTES,
    )

    @Test
    fun invoke_emptySchedules_returnsKernelPerGridSlot() {
        val useCase = BuildLibraryBookingDensityKernelsUseCase(CalculateBookingDensityUseCase())
        val cart = LibraryCartDraft(
            draftOrder = listOf("e1"),
            itemDrafts = mapOf("e1" to LibraryExerciseLineDraft("1", "10")),
        )
        val exercise = Exercise(
            id = "e1",
            name = "A",
            image = ImageSource.LocalResource("x"),
            category = ExerciseCategory.Strength,
            bodyRegion = BodyRegion.Chest,
            description = "",
            equipment = EquipmentType.Barbell,
            safetyNotes = "",
        )
        val kernels = useCase(
            selectedDateMillis = 0L,
            selectedLocationId = "loc",
            selectedSlotStarts = emptySet(),
            cart = cart,
            exercisesById = mapOf("e1" to exercise),
            schedules = emptyList(),
            zoneId = zoneId,
            bookingGridSlotStarts = grid,
        )
        assertEquals(grid.size, kernels.size)
    }
}
