package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseDraft
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExerciseLibraryCartActiveSelectionTest {

    @Test
    fun emptyDrafts_returnsNull() {
        assertNull(
            resolveActiveExerciseAfterRemoval(
                removedId = "a",
                previousActive = "a",
                newDrafts = persistentMapOf<String, ExerciseDraft>(),
                orderBeforeRemoval = persistentListOf("a"),
                newOrder = persistentListOf(),
            ),
        )
    }

    @Test
    fun removedWasNotActive_keepsActive() {
        val active = resolveActiveExerciseAfterRemoval(
            removedId = "b",
            previousActive = "a",
            newDrafts = persistentMapOf("a" to ExerciseDraft()),
            orderBeforeRemoval = persistentListOf("a", "b"),
            newOrder = persistentListOf("a"),
        )
        assertEquals("a", active)
    }

    @Test
    fun removedActive_middle_shiftsToNextInBarOrder() {
        val active = resolveActiveExerciseAfterRemoval(
            removedId = "b",
            previousActive = "b",
            newDrafts = persistentMapOf(
                "a" to ExerciseDraft(),
                "c" to ExerciseDraft(),
            ),
            orderBeforeRemoval = persistentListOf("a", "b", "c"),
            newOrder = persistentListOf("a", "c"),
        )
        assertEquals("c", active)
    }

    @Test
    fun removedActive_last_shiftsToPrevious() {
        val active = resolveActiveExerciseAfterRemoval(
            removedId = "c",
            previousActive = "c",
            newDrafts = persistentMapOf(
                "a" to ExerciseDraft(),
                "b" to ExerciseDraft(),
            ),
            orderBeforeRemoval = persistentListOf("a", "b", "c"),
            newOrder = persistentListOf("a", "b"),
        )
        assertEquals("b", active)
    }

    @Test
    fun removedActive_first_shiftsToNewFirst() {
        val active = resolveActiveExerciseAfterRemoval(
            removedId = "a",
            previousActive = "a",
            newDrafts = persistentMapOf("b" to ExerciseDraft(), "c" to ExerciseDraft()),
            orderBeforeRemoval = persistentListOf("a", "b", "c"),
            newOrder = persistentListOf("b", "c"),
        )
        assertEquals("b", active)
    }

    @Test
    fun removedSingleItem_returnsNull() {
        assertNull(
            resolveActiveExerciseAfterRemoval(
                removedId = "only",
                previousActive = "only",
                newDrafts = persistentMapOf<String, ExerciseDraft>(),
                orderBeforeRemoval = persistentListOf("only"),
                newOrder = persistentListOf(),
            ),
        )
    }
}
