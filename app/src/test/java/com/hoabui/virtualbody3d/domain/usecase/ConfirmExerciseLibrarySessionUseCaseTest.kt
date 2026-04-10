package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.DEFAULT_SESSION_LOCATION_ID
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryCartDraft
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryExerciseLineDraft
import com.hoabui.virtualbody3d.domain.model.exercise.PendingSessionBooking
import com.hoabui.virtualbody3d.domain.repository.BookWorkoutSessionResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId

class ConfirmExerciseLibrarySessionUseCaseTest {

    private val zoneId: ZoneId = ZoneId.of("UTC")

    private val sampleCart = LibraryCartDraft(
        draftOrder = listOf("ex1"),
        itemDrafts = mapOf("ex1" to LibraryExerciseLineDraft(sets = "3", reps = "10")),
    )
    private val measurementById = mapOf("ex1" to ExerciseMeasurementMode.Strength)

    @Test
    fun prepare_emptySlots_returnsNoOp() {
        val useCase = ConfirmExerciseLibrarySessionUseCase(mockk(), ValidateSessionBookingUseCase())
        val pending = PendingSessionBooking(
            selectedDateMillis = 0L,
            selectedLocationId = DEFAULT_SESSION_LOCATION_ID,
            selectedSlotStarts = emptyList(),
            longSessionAcknowledged = false,
            isConfirming = false,
        )
        val r = useCase.prepare(
            pending,
            sampleCart,
            measurementById,
            emptyMap(),
            persistentListOf(),
            emptyMap(),
            zoneId,
            "Gym",
        )
        assertEquals(PrepareLibrarySessionConfirmResult.NoOp, r)
    }

    @Test
    fun commit_success_mapsRepositoryResult() {
        val book = mockk<BookWorkoutSessionUseCase>()
        coEvery { book(any(), any(), any()) } returns BookWorkoutSessionResult.Success(2)
        val useCase = ConfirmExerciseLibrarySessionUseCase(book, ValidateSessionBookingUseCase())
        val session = com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession(
            id = "s1",
            startInstant = java.time.Instant.EPOCH,
            endInstant = java.time.Instant.EPOCH.plusSeconds(3600),
            locationId = "loc",
        )
        val r = runBlocking {
            useCase.commit(
                session = session,
                lines = emptyList(),
                zoneId = zoneId,
                scheduledDateMillis = 100L,
                primaryExerciseTitle = "Squat",
                locationDisplayName = "West",
            )
        }
        assertTrue(r is CommitLibrarySessionBookingResult.Success)
        val s = r as CommitLibrarySessionBookingResult.Success
        assertEquals(2, s.scheduledCount)
        assertEquals(2, s.incrementFabBadgeBy)
        assertEquals("Squat", s.primaryExerciseTitle)
        assertEquals("West", s.locationDisplayName)
    }
}
