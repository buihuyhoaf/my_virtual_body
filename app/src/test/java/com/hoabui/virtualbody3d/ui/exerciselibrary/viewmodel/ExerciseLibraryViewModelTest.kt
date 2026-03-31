package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hoabui.virtualbody3d.core.base.UiState
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.usecase.AddWorkoutUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseLibraryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): ExerciseLibraryViewModel {
        val getLibrary = mockk<GetExerciseLibraryUseCase>()
        every { getLibrary() } returns flowOf(emptyMap<BodyRegion, List<com.hoabui.virtualbody3d.domain.model.exercise.Exercise>>())
        val addWorkout = mockk<AddWorkoutUseCase>()
        val context = mockk<Context>(relaxed = true)
        return ExerciseLibraryViewModel(getLibrary, addWorkout, context)
    }

    private fun ExerciseLibraryViewModel.successData(): ExerciseLibraryUiState {
        val s = state.value
        assertTrue(s is UiState.Success)
        return (s as UiState.Success).data
    }

    @Test
    fun toggle_addThenRemove_emptiesCartAndClearsActive() {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("ex1")
        assertTrue(vm.successData().itemDrafts.containsKey("ex1"))
        assertEquals("ex1", vm.successData().activeExerciseId)
        vm.toggleExerciseInCartFromList("ex1")
        assertTrue(vm.successData().itemDrafts.isEmpty())
        assertTrue(vm.successData().draftOrder.isEmpty())
        assertNull(vm.successData().activeExerciseId)
    }

    @Test
    fun toggle_removeMiddle_reassignsActiveNeighbor() {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("a")
        vm.toggleExerciseInCartFromList("b")
        vm.toggleExerciseInCartFromList("c")
        vm.setActiveCartExercise("b")
        assertEquals("b", vm.successData().activeExerciseId)
        vm.toggleExerciseInCartFromList("b")
        val d = vm.successData()
        assertFalse(d.itemDrafts.containsKey("b"))
        assertEquals("c", d.activeExerciseId)
    }

    @Test
    fun removeFromCart_sameAsToggleOff() {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("x")
        vm.removeFromCart("x")
        assertTrue(vm.successData().itemDrafts.isEmpty())
        assertNull(vm.successData().activeExerciseId)
    }

    @Test
    fun ensureInCartFromDetail_whenAlreadyInCart_doesNotRemove() {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("q")
        vm.ensureInCartAndFocusFromDetail("q")
        assertTrue(vm.successData().itemDrafts.containsKey("q"))
        assertEquals("q", vm.successData().activeExerciseId)
    }

    @Test
    fun setActiveCartExercise_onlyChangesFocus() {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("one")
        vm.toggleExerciseInCartFromList("two")
        val sizeBefore = vm.successData().itemDrafts.size
        vm.setActiveCartExercise("two")
        assertEquals(sizeBefore, vm.successData().itemDrafts.size)
        assertEquals("two", vm.successData().activeExerciseId)
    }

    @Test
    fun removeFromCart_unknownId_noop() {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("only")
        vm.removeFromCart("nope")
        assertNotNull(vm.successData().itemDrafts["only"])
    }
}
