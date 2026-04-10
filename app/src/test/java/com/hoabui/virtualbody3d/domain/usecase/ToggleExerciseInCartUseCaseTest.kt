package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseLibraryCartSnapshot
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryExerciseLineDraft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ToggleExerciseInCartUseCaseTest {

    private val useCase = ToggleExerciseInCartUseCase()

    private fun snap(
        drafts: Map<String, LibraryExerciseLineDraft> = emptyMap(),
        order: List<String> = emptyList(),
        active: String? = null,
    ) = ExerciseLibraryCartSnapshot(drafts, order, active)

    @Test
    fun toggle_remove_emptyCart_returnsNullActive() {
        val s0 = snap(
            drafts = mapOf("a" to LibraryExerciseLineDraft("", "")),
            order = listOf("a"),
            active = "a",
        )
        val s1 = useCase(s0, ExerciseLibraryCartCommand.Toggle("a"))
        assertNull(s1.activeExerciseId)
        assertEquals(emptyList<String>(), s1.draftOrder)
    }

    @Test
    fun remove_keepsActiveWhenNotRemoved() {
        val s0 = snap(
            drafts = mapOf(
                "a" to LibraryExerciseLineDraft("", ""),
                "b" to LibraryExerciseLineDraft("", ""),
            ),
            order = listOf("a", "b"),
            active = "a",
        )
        val s1 = useCase(s0, ExerciseLibraryCartCommand.Remove("b"))
        assertEquals("a", s1.activeExerciseId)
    }

    @Test
    fun removeMiddle_shiftsActiveToNextInOrder() {
        val s0 = snap(
            drafts = mapOf(
                "a" to LibraryExerciseLineDraft("", ""),
                "b" to LibraryExerciseLineDraft("", ""),
                "c" to LibraryExerciseLineDraft("", ""),
            ),
            order = listOf("a", "b", "c"),
            active = "b",
        )
        val s1 = useCase(s0, ExerciseLibraryCartCommand.Toggle("b"))
        assertEquals("c", s1.activeExerciseId)
    }

    @Test
    fun removeLast_shiftsActiveToPrevious() {
        val s0 = snap(
            drafts = mapOf(
                "a" to LibraryExerciseLineDraft("", ""),
                "b" to LibraryExerciseLineDraft("", ""),
                "c" to LibraryExerciseLineDraft("", ""),
            ),
            order = listOf("a", "b", "c"),
            active = "c",
        )
        val s1 = useCase(s0, ExerciseLibraryCartCommand.Remove("c"))
        assertEquals("b", s1.activeExerciseId)
    }

    @Test
    fun removeFirst_shiftsActiveToNewFirst() {
        val s0 = snap(
            drafts = mapOf(
                "a" to LibraryExerciseLineDraft("", ""),
                "b" to LibraryExerciseLineDraft("", ""),
                "c" to LibraryExerciseLineDraft("", ""),
            ),
            order = listOf("a", "b", "c"),
            active = "a",
        )
        val s1 = useCase(s0, ExerciseLibraryCartCommand.Remove("a"))
        assertEquals("b", s1.activeExerciseId)
    }

    @Test
    fun removeOnlyItem_returnsNullActive() {
        val s0 = snap(
            drafts = mapOf("only" to LibraryExerciseLineDraft("", "")),
            order = listOf("only"),
            active = "only",
        )
        val s1 = useCase(s0, ExerciseLibraryCartCommand.Remove("only"))
        assertNull(s1.activeExerciseId)
    }
}
