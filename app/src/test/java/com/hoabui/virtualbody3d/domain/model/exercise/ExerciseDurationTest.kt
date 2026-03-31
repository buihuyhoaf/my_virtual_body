package com.hoabui.virtualbody3d.domain.model.exercise

import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.isAnchoredAddEnabled
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

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
    fun isAnchoredAddEnabled_mixedCartStrengthAndDuration() {
        val modes = persistentMapOf(
            "a" to ExerciseMeasurementMode.Strength,
            "b" to ExerciseMeasurementMode.Duration,
        )
        val drafts = persistentMapOf(
            "a" to ExerciseDraft(sets = "3", reps = "10"),
            "b" to ExerciseDraft(sets = "1", reps = "30"),
        )
        val state = ExerciseLibraryUiState(
            itemDrafts = drafts,
            draftOrder = persistentListOf("a", "b"),
            selectedDate = 1L,
            selectedTime = LocalTime.NOON,
            exerciseMeasurementById = modes,
        )
        assertTrue(state.isAnchoredAddEnabled())
    }

    @Test
    fun isAnchoredAddEnabled_durationZeroTotal_disabled() {
        val modes = persistentMapOf("b" to ExerciseMeasurementMode.Duration)
        val drafts = persistentMapOf("b" to ExerciseDraft(sets = "0", reps = "0"))
        val state = ExerciseLibraryUiState(
            itemDrafts = drafts,
            draftOrder = persistentListOf("b"),
            selectedDate = 1L,
            selectedTime = LocalTime.NOON,
            exerciseMeasurementById = modes,
        )
        assertFalse(state.isAnchoredAddEnabled())
    }
}
