package com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hoabui.virtualbody3d.core.base.UiState
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.model.exercise.Muscle
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import com.hoabui.virtualbody3d.domain.repository.BookWorkoutSessionResult
import com.hoabui.virtualbody3d.domain.usecase.BookWorkoutSessionUseCase
import com.hoabui.virtualbody3d.domain.usecase.CanConfirmLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.ClearCartUseCase
import com.hoabui.virtualbody3d.domain.usecase.CommitLibrarySessionBookingResult
import com.hoabui.virtualbody3d.domain.usecase.ConfirmExerciseLibrarySessionUseCase
import com.hoabui.virtualbody3d.domain.usecase.DismissSessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseCatalogUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseLibraryUiStateUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveGymLocationsUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveSessionBookingEditorUiStateUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnBookingClearTimeSelectionUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnBookingDateSelectedUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnBookingLocationSelectedUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnBookingSlotToggledUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnLongSessionEditUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnLongSessionProceedAnywayUseCase
import com.hoabui.virtualbody3d.domain.usecase.OpenSessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.PrepareLibrarySessionConfirmResult
import com.hoabui.virtualbody3d.domain.usecase.RemoveCartItemUseCase
import com.hoabui.virtualbody3d.domain.usecase.ResolveNextSlotSelectionAfterToggleUseCase
import com.hoabui.virtualbody3d.domain.usecase.RunExerciseLibraryBookingConfirmationUseCase
import com.hoabui.virtualbody3d.domain.usecase.SelectCartItemUseCase
import com.hoabui.virtualbody3d.domain.usecase.SessionBookingConfirmationWorkflow
import com.hoabui.virtualbody3d.domain.usecase.SetCartFieldManualUseCase
import com.hoabui.virtualbody3d.domain.usecase.StepCartFieldUseCase
import com.hoabui.virtualbody3d.domain.usecase.ToggleExerciseInCartUseCase
import com.hoabui.virtualbody3d.domain.usecase.ValidateSessionBookingUseCase
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryBookingManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibrarySearchManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.mapper.ExerciseLibraryUiMapper
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class SessionBookingEditorViewModelTest {

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
        focusMuscles = listOf(Muscle.UPPER_PECTORALIS),
        description = "",
        equipment = EquipmentType.Barbell,
        safetyNotes = "",
        measurementMode = ExerciseMeasurementMode.Strength,
    )

    private data class Fixture(
        val viewModel: SessionBookingEditorViewModel,
        val cartManager: ExerciseLibraryCartManager,
        val observeUiState: ObserveExerciseLibraryUiStateUseCase,
    )

    private fun createFixture(
        canConfirmLibrarySessionBookingUseCase: CanConfirmLibrarySessionBookingUseCase =
            CanConfirmLibrarySessionBookingUseCase(ValidateSessionBookingUseCase()),
        confirmUseCase: ConfirmExerciseLibrarySessionUseCase = defaultConfirmUseCase(),
    ): Fixture {
        val getLibrary = mockk<GetExerciseLibraryUseCase>()
        every { getLibrary() } returns flowOf(mapOf(BodyRegion.Chest to listOf(sampleExercise())))
        val locations = mockk<ObserveGymLocationsUseCase>()
        every { locations() } returns flow {
            emit(listOf(GymLocation(id = "default", displayName = "Default")))
            awaitCancellation()
        }
        val mapper = ExerciseLibraryUiMapper(mockk<Context>(relaxed = true))
        val search = ExerciseLibrarySearchManager()
        val cart = ExerciseLibraryCartManager(ToggleExerciseInCartUseCase())
        val workflow = SessionBookingConfirmationWorkflow(confirmUseCase)
        val booking = ExerciseLibraryBookingManager(
            ResolveNextSlotSelectionAfterToggleUseCase(),
            workflow,
            canConfirmLibrarySessionBookingUseCase,
            mapper,
            cart,
        )
        val observeCatalog = ObserveExerciseCatalogUseCase(getLibrary)
        val observeUiState = ObserveExerciseLibraryUiStateUseCase(
            observeCatalog,
            search,
            cart,
            mapper,
        )
        val observeSessionBooking = ObserveSessionBookingEditorUiStateUseCase(
            observeUiState,
            locations,
            booking,
            mapper,
            canConfirmLibrarySessionBookingUseCase,
        )
        val viewModel = SessionBookingEditorViewModel(
            observeExerciseCatalogUseCase = observeCatalog,
            observeExerciseLibraryUiStateUseCase = observeUiState,
            observeSessionBookingEditorUiStateUseCase = observeSessionBooking,
            openSessionBookingUseCase = OpenSessionBookingUseCase(observeUiState, observeCatalog, booking),
            dismissSessionBookingUseCase = DismissSessionBookingUseCase(booking),
            onBookingDateSelectedUseCase = OnBookingDateSelectedUseCase(booking),
            onBookingLocationSelectedUseCase = OnBookingLocationSelectedUseCase(booking),
            onBookingSlotToggledUseCase = OnBookingSlotToggledUseCase(booking),
            onBookingClearTimeSelectionUseCase = OnBookingClearTimeSelectionUseCase(booking),
            runExerciseLibraryBookingConfirmationUseCase =
                RunExerciseLibraryBookingConfirmationUseCase(
                    observeUiState,
                    observeCatalog,
                    observeSessionBooking,
                    booking,
                ),
            onLongSessionEditUseCase = OnLongSessionEditUseCase(booking),
            onLongSessionProceedAnywayUseCase = OnLongSessionProceedAnywayUseCase(booking),
            selectCartItemUseCase = SelectCartItemUseCase(cart),
            removeCartItemUseCase = RemoveCartItemUseCase(cart),
            clearCartUseCase = ClearCartUseCase(cart, booking),
            stepCartFieldUseCase = StepCartFieldUseCase(cart),
            setCartFieldManualUseCase = SetCartFieldManualUseCase(cart),
        )
        return Fixture(viewModel, cart, observeUiState)
    }

    private fun defaultConfirmUseCase(): ConfirmExerciseLibrarySessionUseCase {
        val confirmUseCase = mockk<ConfirmExerciseLibrarySessionUseCase>()
        every {
            confirmUseCase.prepare(any(), any(), any(), any(), any(), any())
        } returns PrepareLibrarySessionConfirmResult.NoOp
        coEvery {
            confirmUseCase.commit(any(), any(), any(), any(), any())
        } returns CommitLibrarySessionBookingResult.InvalidDraft
        return confirmUseCase
    }

    private fun SessionBookingEditorViewModel.successData(): SessionBookingEditorPresentationState {
        val s = state.value
        assertTrue(s is UiState.Success)
        return (s as UiState.Success).data
    }

    private fun Fixture.addValidCartExercise() {
        cartManager.toggleCardSelection(observeUiState.snapshotForCartActions(), "ex1")
        viewModel.setCartFieldManual("ex1", 0, CartSetField.SETS, "3")
        viewModel.setCartFieldManual("ex1", 0, CartSetField.REPS, "10")
    }

    @Test
    fun openSessionBooking_thenDismiss_clearsInput() {
        val fixture = createFixture()
        val vm = fixture.viewModel
        fixture.addValidCartExercise()

        vm.openSessionBooking()
        assertNotNull(vm.successData().sessionBookingInput)

        vm.dismissSessionBooking()
        assertNull(vm.successData().sessionBookingInput)
    }

    @Test
    fun bookingSlotToggle_reusesCachedSections() = runBlocking {
        val fixture = createFixture()
        val vm = fixture.viewModel
        fixture.addValidCartExercise()
        vm.openSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)

        val beforeSections = vm.successData().libraryUi.libraryList.sections
        vm.onBookingSlotToggled(LocalTime.of(10, 0))
        delay(WAIT_FOR_COMBINE_MS)

        assertTrue(beforeSections === vm.successData().libraryUi.libraryList.sections)
    }

    @Test
    fun bookingSlotToggle_afterSearch_reinvokesValidate() = runBlocking {
        val validate = spyk(CanConfirmLibrarySessionBookingUseCase(ValidateSessionBookingUseCase()))
        val fixture = createFixture(canConfirmLibrarySessionBookingUseCase = validate)
        val vm = fixture.viewModel
        fixture.addValidCartExercise()
        vm.openSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)
        clearMocks(validate, answers = false, recordedCalls = true)

        vm.onBookingSlotToggled(LocalTime.of(10, 0))
        delay(WAIT_FOR_COMBINE_MS)

        verify(atLeast = 1) {
            validate(any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun confirmSessionBooking_success_clearsCartAndEmitsSummaryEffect() = runBlocking {
        val bookInner = mockk<BookWorkoutSessionUseCase>()
        val resolvedFromRepo = WorkoutSession(
            id = "booked-session",
            startInstant = Instant.parse("1970-01-01T10:00:00Z"),
            endInstant = Instant.parse("1970-01-01T11:00:00Z"),
            locationId = "default",
        )
        coEvery { bookInner(any(), any()) } returns BookWorkoutSessionResult.Success(1, resolvedFromRepo)
        val confirmUseCase = ConfirmExerciseLibrarySessionUseCase(bookInner, ValidateSessionBookingUseCase())
        val fixture = createFixture(confirmUseCase = confirmUseCase)
        val vm = fixture.viewModel
        fixture.addValidCartExercise()
        vm.openSessionBooking()
        vm.onBookingSlotToggled(LocalTime.of(10, 0))
        delay(WAIT_FOR_COMBINE_MS)
        vm.onBookingSlotToggled(LocalTime.of(10, 30))
        delay(WAIT_FOR_COMBINE_MS)
        assertTrue(requireNotNull(vm.successData().sessionBookingUiModel).isBookingConfirmEnabled)
        var successEvent: SessionBookingEvent.ShowAddExerciseSuccess? = null
        val collectJob = launch {
            vm.events.collect { event ->
                if (event is SessionBookingEvent.ShowAddExerciseSuccess) {
                    successEvent = event
                    return@collect
                }
            }
        }

        vm.confirmSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)

        assertTrue(vm.successData().libraryUi.itemDrafts.isEmpty())
        val summary = requireNotNull(successEvent).summary
        assertEquals(1, summary.exerciseCount)
        assertEquals("Sample", summary.primaryExerciseTitle)
        collectJob.cancel()
    }

    private companion object {
        const val WAIT_FOR_COMBINE_MS = 200L
    }
}
