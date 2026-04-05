package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hoabui.virtualbody3d.core.base.UiState
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.repository.BookWorkoutSessionResult
import com.hoabui.virtualbody3d.domain.usecase.BookWorkoutSessionUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.domain.usecase.MigrateLegacyWorkoutSchedulesUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveBusyIntervalsUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveGymLocationsUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveWorkoutSchedulesUseCase
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
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
import com.hoabui.virtualbody3d.domain.model.exercise.halfOpenInstantIntervalDurationMinutes
import java.time.LocalTime

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

    private fun sampleExercise(id: String = "ex1") = Exercise(
        id = id,
        name = "Sample",
        image = ImageSource.LocalResource("placeholder"),
        category = ExerciseCategory.Strength,
        bodyRegion = BodyRegion.Chest,
        description = "",
        equipment = EquipmentType.Barbell,
        safetyNotes = "",
        measurementMode = ExerciseMeasurementMode.Strength,
    )

    private fun createViewModel(
        exercises: Map<BodyRegion, List<Exercise>> = mapOf(BodyRegion.Chest to listOf(sampleExercise())),
    ): ExerciseLibraryViewModel {
        val getLibrary = mockk<GetExerciseLibraryUseCase>()
        every { getLibrary() } returns flowOf(exercises)
        val book = mockk<BookWorkoutSessionUseCase>()
        coEvery { book(any(), any(), any()) } returns BookWorkoutSessionResult.Success(1)
        val locations = mockk<ObserveGymLocationsUseCase>()
        every { locations() } returns flow {
            emit(listOf(GymLocation(id = "default", displayName = "Default")))
            awaitCancellation()
        }
        val busy = mockk<ObserveBusyIntervalsUseCase>()
        every { busy(any(), any(), any()) } returns flow {
            emit(emptyList())
            awaitCancellation()
        }
        val migrate = mockk<MigrateLegacyWorkoutSchedulesUseCase>()
        coEvery { migrate(any()) } returns Unit
        val workoutSchedules = mockk<ObserveWorkoutSchedulesUseCase>()
        every { workoutSchedules() } returns flow {
            emit(emptyList())
            awaitCancellation()
        }
        val context = mockk<Context>(relaxed = true)
        return ExerciseLibraryViewModel(
            getLibrary,
            book,
            locations,
            busy,
            workoutSchedules,
            migrate,
            context,
        )
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
        val vm = createViewModel(
            mapOf(
                BodyRegion.Chest to listOf(
                    sampleExercise("a"),
                    sampleExercise("b"),
                    sampleExercise("c"),
                ),
            ),
        )
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
        vm.toggleExerciseInCartFromList("ex1")
        vm.removeFromCart("ex1")
        assertTrue(vm.successData().itemDrafts.isEmpty())
        assertNull(vm.successData().activeExerciseId)
    }

    @Test
    fun ensureInCartFromDetail_whenAlreadyInCart_doesNotRemove() {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("ex1")
        vm.ensureInCartAndFocusFromDetail("ex1")
        assertTrue(vm.successData().itemDrafts.containsKey("ex1"))
        assertEquals("ex1", vm.successData().activeExerciseId)
    }

    @Test
    fun setActiveCartExercise_onlyChangesFocus() {
        val vm = createViewModel(
            mapOf(
                BodyRegion.Chest to listOf(sampleExercise("one"), sampleExercise("two")),
            ),
        )
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
        vm.toggleExerciseInCartFromList("ex1")
        vm.removeFromCart("nope")
        assertNotNull(vm.successData().itemDrafts["ex1"])
    }

    @Test
    fun openSessionBooking_thenDismiss_clearsInput() = runBlocking {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("ex1")
        vm.updateActiveDraft("3", "10")
        vm.openSessionBooking()
        assertNotNull(vm.successData().sessionBookingInput)
        vm.dismissSessionBooking()
        assertNull(vm.successData().sessionBookingInput)
    }

    @Test
    fun bookingSlotToggle_reusesCachedSections() = runBlocking {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("ex1")
        vm.updateActiveDraft("3", "10")
        vm.openSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)
        val beforeSections = vm.successData().sections
        vm.onBookingSlotToggled(LocalTime.of(10, 0))
        delay(WAIT_FOR_COMBINE_MS)
        assertTrue(beforeSections === vm.successData().sections)
    }

    @Test
    fun searchQueryChange_rebuildsSections() = runBlocking {
        val vm = createViewModel()
        delay(WAIT_FOR_COMBINE_MS)
        val beforeSections = vm.successData().sections
        vm.updateSearchQuery("___no_match_xyz___")
        delay(WAIT_FOR_COMBINE_MS)
        assertFalse(beforeSections === vm.successData().sections)
    }

    @Test
    fun isAddToSessionEnabled_falseWhenCartEmpty() = runBlocking {
        val vm = createViewModel()
        delay(WAIT_FOR_COMBINE_MS)
        assertFalse(vm.successData().isAddToSessionEnabled)
    }

    @Test
    fun isAddToSessionEnabled_trueWhenCartValid() = runBlocking {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("ex1")
        vm.updateActiveDraft("3", "10")
        delay(WAIT_FOR_COMBINE_MS)
        assertTrue(vm.successData().isAddToSessionEnabled)
    }

    @Test
    fun confirmSessionBooking_success_clearsCartAndSetsSummary() = runBlocking {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("ex1")
        vm.updateActiveDraft("3", "10")
        vm.openSessionBooking()
        vm.onBookingSlotToggled(LocalTime.of(10, 0))
        delay(WAIT_FOR_COMBINE_MS)
        assertTrue(requireNotNull(vm.successData().sessionBooking).isBookingConfirmEnabled)
        vm.confirmSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)
        val d = vm.successData()
        assertTrue(d.itemDrafts.isEmpty())
        val summary = requireNotNull(d.addExerciseSuccess)
        assertEquals(1, summary.exerciseCount)
        assertEquals("Sample", summary.primaryExerciseTitle)
        assertEquals(
            30L,
            halfOpenInstantIntervalDurationMinutes(summary.sessionStartInstant, summary.sessionEndInstant),
        )
    }

    private companion object {
        const val WAIT_FOR_COMBINE_MS = 200L
    }
}
