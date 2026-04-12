package com.hoabui.virtualbody3d.domain.model.exercise

import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryListProjectionState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryCartState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SetRowDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.isCartDraftValidForSessionConfirm
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseDurationTest {

    @Test
    fun normalizeDuration_zeroSeconds_returnsZero() {
        assertEquals(0, normalizeDurationMinutesSeconds(0, 0))
    }

    @Test
    fun normalizeDuration_rollsSecondsIntoMinutes() {
        assertEquals(90, normalizeDurationMinutesSeconds(0, 90))
        assertEquals(60 + 30, normalizeDurationMinutesSeconds(0, 90))
        assertEquals(120 + 5, normalizeDurationMinutesSeconds(1, 65))
    }

    @Test
    fun normalizeDuration_negativeMinutes_clampedToZero() {
        assertEquals(45, normalizeDurationMinutesSeconds(-5, 45))
    }

    @Test
    fun normalizeDuration_negativeSeconds_clampedToZero() {
        assertEquals(120, normalizeDurationMinutesSeconds(2, -10))
    }

    @Test
    fun isCartDraftValid_mixedCartStrengthAndDuration() {
        val modes = persistentMapOf(
            "a" to ExerciseMeasurementMode.Strength,
            "b" to ExerciseMeasurementMode.Duration,
        )
        val drafts = persistentMapOf(
            // Strength: 3 sets × 10 reps — valid
            "a" to ExerciseDraft(
                setRows = persistentListOf(
                    SetRowDraft(reps = 10),
                    SetRowDraft(reps = 10),
                    SetRowDraft(reps = 10),
                ),
            ),
            // Duration: 1 min 30 sec — valid
            "b" to ExerciseDraft(setRows = persistentListOf(SetRowDraft(minutes = 1, seconds = 30))),
        )
        val state = ExerciseLibraryUiState(
            cart = LibraryCartState(
                itemDrafts = drafts,
                draftOrder = persistentListOf("a", "b"),
            ),
            libraryList = ExerciseLibraryListProjectionState(
                exerciseMeasurementById = modes,
            ),
        )
        assertTrue(state.isCartDraftValidForSessionConfirm())
    }

    @Test
    fun isCartDraftValid_durationZeroTotal_disabled() {
        val modes = persistentMapOf("b" to ExerciseMeasurementMode.Duration)
        val drafts = persistentMapOf("b" to ExerciseDraft(setRows = persistentListOf(SetRowDraft(minutes = 0, seconds = 0))))
        val state = ExerciseLibraryUiState(
            cart = LibraryCartState(
                itemDrafts = drafts,
                draftOrder = persistentListOf("b"),
            ),
            libraryList = ExerciseLibraryListProjectionState(
                exerciseMeasurementById = modes,
            ),
        )
        assertFalse(state.isCartDraftValidForSessionConfirm())
    }
}
