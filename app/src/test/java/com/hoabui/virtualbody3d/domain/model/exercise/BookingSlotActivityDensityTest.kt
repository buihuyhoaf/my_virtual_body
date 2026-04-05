package com.hoabui.virtualbody3d.domain.model.exercise

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode.Strength
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class BookingSlotActivityDensityTest {

    private val zone: ZoneId = ZoneId.of("Asia/Ho_Chi_Minh")
    private val date: LocalDate = LocalDate.of(2026, 6, 15)
    private val slot0600: LocalTime = LocalTime.of(6, 0)
    private val slots: List<LocalTime> = listOf(slot0600, LocalTime.of(6, 30))

    @Test
    fun estimatedPlannedMinutesForScheduleLine_strength_positiveSetsReps() {
        val sch = WorkoutSchedule(
            id = "1",
            exerciseId = "ex",
            scheduledAt = LocalDateTime.of(date, slot0600),
            sets = 3,
            reps = 10,
            weightKg = 0.0,
            restSeconds = 60,
            notes = null,
            measurementMode = Strength,
            durationSeconds = null,
            sessionId = "s1",
            locationId = "loc1",
        )
        val m = estimatedPlannedMinutesForScheduleLine(sch)
        assertTrue(m > 0)
    }

    @Test
    fun projectBookingSlotDensityKernels_sumsMinutesForCommitInBucket() {
        val sch = WorkoutSchedule(
            id = "1",
            exerciseId = "ex",
            scheduledAt = LocalDateTime.of(date, slot0600),
            sets = 2,
            reps = 5,
            weightKg = 0.0,
            restSeconds = 30,
            notes = null,
            measurementMode = Strength,
            durationSeconds = null,
            sessionId = "s1",
            locationId = "loc1",
        )
        val k = projectBookingSlotDensityKernels(
            date = date,
            zoneId = zone,
            locationId = "loc1",
            slotStarts = slots,
            schedules = listOf(sch),
            draftTotalMinutes = 0,
            draftAnchorSlot = null,
        )
        assertEquals(2, k.size)
        val first = k.first()
        assertTrue(first.totalPlannedMinutes > 0)
        assertEquals(SlotDensityTier.Empty, k[1].densityTier)
    }

    @Test
    fun projectBookingSlotDensityKernels_addsDraftMinutesOnAnchorSlotOnly() {
        val k = projectBookingSlotDensityKernels(
            date = date,
            zoneId = zone,
            locationId = "loc1",
            slotStarts = slots,
            schedules = emptyList(),
            draftTotalMinutes = 25,
            draftAnchorSlot = slots.first(),
        )
        assertEquals(25, k.first().totalPlannedMinutes)
        assertEquals(0, k[1].totalPlannedMinutes)
    }
}
