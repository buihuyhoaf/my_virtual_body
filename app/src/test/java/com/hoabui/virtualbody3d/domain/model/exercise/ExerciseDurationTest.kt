package com.hoabui.virtualbody3d.domain.model.exercise

import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.SetRowDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.LibraryPresentationSlice
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryChromeMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.isCartDraftValidForSessionConfirm
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
            "a" to ExerciseDraft(
                setRows = persistentListOf(
                    SetRowDraft(reps = 10),
                    SetRowDraft(reps = 10),
                    SetRowDraft(reps = 10),
                ),
            ),
            "b" to ExerciseDraft(setRows = persistentListOf(SetRowDraft(minutes = 1, seconds = 30))),
        )
        val state = ExerciseLibraryUiState(
            itemDrafts = drafts,
            draftOrder = persistentListOf("a", "b"),
            libraryList = LibraryPresentationSlice(
                exerciseMeasurementById = modes,
            ),
        )
        assertTrue(state.isCartDraftValidForSessionConfirm(ExerciseLibraryChromeMode.Idle))
    }

    @Test
    fun isCartDraftValid_durationZeroTotal_disabled() {
        val modes = persistentMapOf("b" to ExerciseMeasurementMode.Duration)
        val drafts = persistentMapOf("b" to ExerciseDraft(setRows = persistentListOf(SetRowDraft(minutes = 0, seconds = 0))))
        val state = ExerciseLibraryUiState(
            itemDrafts = drafts,
            draftOrder = persistentListOf("b"),
            libraryList = LibraryPresentationSlice(
                exerciseMeasurementById = modes,
            ),
        )
        assertFalse(state.isCartDraftValidForSessionConfirm(ExerciseLibraryChromeMode.Idle))
    }
}
