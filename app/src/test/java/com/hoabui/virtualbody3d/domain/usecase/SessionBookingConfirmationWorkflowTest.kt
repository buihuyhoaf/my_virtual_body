package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.DEFAULT_SESSION_LOCATION_ID
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryCartDraft
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryExerciseLineDraft
import com.hoabui.virtualbody3d.domain.model.exercise.PendingSessionBooking
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import com.hoabui.virtualbody3d.domain.repository.BookWorkoutSessionResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalTime

class SessionBookingConfirmationWorkflowTest {

    @Test
    fun run_noOp_emitsPreparingThenNoOp() = runBlocking {
        val confirm = mockk<ConfirmExerciseLibrarySessionUseCase>()
        every {
            confirm.prepare(any(), any(), any(), any(), any(), any())
        } returns PrepareLibrarySessionConfirmResult.NoOp
        val wf = SessionBookingConfirmationWorkflow(confirm)
        val input = minimalInput()
        val events = wf.run(input).toList()
        assertEquals(
            listOf(
                BookingConfirmationStatus.Preparing,
                BookingConfirmationStatus.NoOp,
            ),
            events,
        )
    }

    @Test
    fun run_longSession_emitsAwaitingAck() = runBlocking {
        val confirm = mockk<ConfirmExerciseLibrarySessionUseCase>()
        every {
            confirm.prepare(any(), any(), any(), any(), any(), any())
        } returns PrepareLibrarySessionConfirmResult.LongSessionAcknowledgementRequired
        val wf = SessionBookingConfirmationWorkflow(confirm)
        val events = wf.run(minimalInput()).toList()
        assertEquals(
            listOf(
                BookingConfirmationStatus.Preparing,
                BookingConfirmationStatus.AwaitingLongSessionAck,
            ),
            events,
        )
    }

    @Test
    fun run_ready_emitsPendingCommitCommittingCompleted() = runBlocking {
        val session = WorkoutSession(
            id = "s",
            startInstant = Instant.EPOCH,
            endInstant = Instant.EPOCH.plusSeconds(1800),
            locationId = "loc",
        )
        val ready = PrepareLibrarySessionConfirmResult.Ready(
            session = session,
            lines = emptyList(),
            scheduledDateMillis = 1L,
            primaryExerciseTitle = "T",
            locationDisplayName = "Gym",
        )
        val confirm = mockk<ConfirmExerciseLibrarySessionUseCase>()
        every {
            confirm.prepare(any(), any(), any(), any(), any(), any())
        } returns ready
        coEvery {
            confirm.commit(any(), any(), any(), any(), any())
        } returns CommitLibrarySessionBookingResult.Success(
            scheduledCount = 2,
            session = session,
            scheduledDateMillis = 1L,
            primaryExerciseTitle = "T",
            locationDisplayName = "Gym",
            incrementFabBadgeBy = 2,
        )
        val wf = SessionBookingConfirmationWorkflow(confirm)
        val events = wf.run(minimalInput()).toList()
        assertEquals(BookingConfirmationStatus.Preparing, events[0])
        assertTrue(events[1] is BookingConfirmationStatus.PendingCommit)
        assertEquals(BookingConfirmationStatus.Committing, events[2])
        assertTrue(events[3] is BookingConfirmationStatus.Completed)
        val done = events[3] as BookingConfirmationStatus.Completed
        assertTrue(done.result is CommitLibrarySessionBookingResult.Success)
    }

    private fun minimalInput(): SessionBookingWorkflowInput =
        SessionBookingWorkflowInput(
            pending = PendingSessionBooking(
                selectedDateMillis = 0L,
                selectedLocationId = DEFAULT_SESSION_LOCATION_ID,
                selectedSlotStarts = listOf(LocalTime.of(10, 0)),
                longSessionAcknowledged = true,
                isConfirming = false,
            ),
            cart = LibraryCartDraft(
                draftOrder = listOf("ex1"),
                itemDrafts = mapOf("ex1" to LibraryExerciseLineDraft("3", "10")),
            ),
            exerciseMeasurementById = mapOf("ex1" to ExerciseMeasurementMode.Strength),
            exerciseSnapshotTitlesById = emptyMap(),
            exercisesById = emptyMap(),
            locationDisplayName = "Gym",
        )
}
