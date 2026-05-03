package com.hoabui.virtualbody3d.ui.exerciselibrary.state.reducer

import com.hoabui.virtualbody3d.domain.model.exercise.DEFAULT_SESSION_LOCATION_ID
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutExecutionStatus
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryChromeMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryCartState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SetRowDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibraryUpdate
import java.time.LocalDateTime
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseLibraryReducerSelectionBarTest {

    private val reducer = ExerciseLibraryReducer()

    private fun durationSchedule(rowId: Long): WorkoutSchedule = WorkoutSchedule(
        id = "sched-$rowId",
        rowId = rowId,
        exerciseId = "ex-duration",
        scheduledAt = LocalDateTime.of(2025, 4, 10, 8, 0),
        sets = 1,
        reps = 0,
        weightKg = 0.0,
        restSeconds = 0,
        notes = null,
        measurementMode = ExerciseMeasurementMode.Duration,
        durationSeconds = 300,
        executionStatus = WorkoutExecutionStatus.Scheduled,
        locationId = DEFAULT_SESSION_LOCATION_ID,
    )

    @Test
    fun selectionBarEditFromScheduleRowLoaded_setsIsolatedFlagAndMeasurementMode() {
        val schedule = durationSchedule(42L)
        val next = reducer.reduce(
            ExerciseLibraryUiState(),
            ExerciseLibraryUpdate.SelectionBarEditFromScheduleRowLoaded(
                scheduleRowId = 42L,
                schedule = schedule,
            ),
        )
        val mode = next.chrome.mode as ExerciseLibraryChromeMode.EditingScheduleRow
        assertTrue(mode.isIsolatedScheduleRowSelectionEdit)
        assertEquals(ExerciseMeasurementMode.Duration, mode.measurementMode)
        assertEquals(42L, mode.scheduleRowId)
        assertTrue(next.cart.itemDrafts.containsKey("ex-duration"))
    }

    @Test
    fun selectionBarEditFinished_clearsCart_whenIsolatedScheduleRowEdit() {
        val loaded = reducer.reduce(
            ExerciseLibraryUiState(),
            ExerciseLibraryUpdate.SelectionBarEditFromScheduleRowLoaded(
                scheduleRowId = 1L,
                schedule = durationSchedule(1L),
            ),
        )
        val finished = reducer.reduce(loaded, ExerciseLibraryUpdate.SelectionBarEditFinished)
        assertTrue(finished.cart.itemDrafts.isEmpty())
        assertTrue(finished.cart.draftOrder.isEmpty())
        assertEquals(null, finished.cart.activeExerciseId)
        assertTrue(finished.chrome.mode is ExerciseLibraryChromeMode.Idle)
    }

    @Test
    fun selectionBarEditCancelled_clearsCart_whenIsolatedScheduleRowEdit() {
        val loaded = reducer.reduce(
            ExerciseLibraryUiState(),
            ExerciseLibraryUpdate.SelectionBarEditFromScheduleRowLoaded(
                scheduleRowId = 2L,
                schedule = durationSchedule(2L),
            ),
        )
        val cancelled = reducer.reduce(loaded, ExerciseLibraryUpdate.SelectionBarEditCancelled)
        assertTrue(cancelled.cart.itemDrafts.isEmpty())
        assertTrue(cancelled.chrome.mode is ExerciseLibraryChromeMode.Idle)
    }

    @Test
    fun selectionBarEditCancelled_restoresBaseline_whenEditBeganFromLibraryCart() {
        val draft = ExerciseDraft(
            persistentListOf(SetRowDraft(reps = 10, weightKg = 20.0, minutes = 0, seconds = 0)),
        )
        val cart = LibraryCartState(
            itemDrafts = persistentMapOf("e1" to draft),
            draftOrder = persistentListOf("e1"),
            activeExerciseId = "e1",
            isCartExpanded = false,
        )
        val initial = ExerciseLibraryUiState(cart = cart)
        val began = reducer.reduce(initial, ExerciseLibraryUpdate.SelectionBarEditBegan(99L))
        val beganMode = began.chrome.mode as ExerciseLibraryChromeMode.EditingScheduleRow
        assertFalse(beganMode.isIsolatedScheduleRowSelectionEdit)
        val cancelled = reducer.reduce(began, ExerciseLibraryUpdate.SelectionBarEditCancelled)
        assertEquals(1, cancelled.cart.itemDrafts.size)
        assertEquals("e1", cancelled.cart.draftOrder.single())
        assertTrue(cancelled.chrome.mode is ExerciseLibraryChromeMode.Idle)
    }

    @Test
    fun selectionBarEditFinished_keepsCart_whenEditBeganFromLibraryCart() {
        val draft = ExerciseDraft(
            persistentListOf(SetRowDraft(reps = 8, weightKg = 15.0, minutes = 0, seconds = 0)),
        )
        val cart = LibraryCartState(
            itemDrafts = persistentMapOf("e1" to draft),
            draftOrder = persistentListOf("e1"),
            activeExerciseId = "e1",
        )
        val initial = ExerciseLibraryUiState(cart = cart)
        val began = reducer.reduce(initial, ExerciseLibraryUpdate.SelectionBarEditBegan(7L))
        val finished = reducer.reduce(began, ExerciseLibraryUpdate.SelectionBarEditFinished)
        assertEquals(1, finished.cart.itemDrafts.size)
        assertFalse(finished.cart.isCartExpanded)
        assertTrue(finished.chrome.mode is ExerciseLibraryChromeMode.Idle)
    }
}
