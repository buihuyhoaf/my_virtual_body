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
import com.hoabui.virtualbody3d.domain.model.exercise.Muscle
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.usecase.BookWorkoutSessionUseCase
import com.hoabui.virtualbody3d.domain.usecase.CommitLibrarySessionBookingResult
import com.hoabui.virtualbody3d.domain.usecase.ConfirmExerciseLibrarySessionUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.domain.usecase.MigrateLegacyWorkoutSchedulesUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveGymLocationsUseCase
import com.hoabui.virtualbody3d.domain.usecase.PrepareLibrarySessionConfirmResult
import com.hoabui.virtualbody3d.domain.usecase.CanConfirmLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.CanOpenExerciseLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.SessionBookingConfirmationWorkflow
import com.hoabui.virtualbody3d.domain.usecase.ResolveNextSlotSelectionAfterToggleUseCase
import com.hoabui.virtualbody3d.domain.usecase.ToggleExerciseInCartUseCase
import com.hoabui.virtualbody3d.domain.usecase.UpdateWorkoutScheduleFromCartDraftUseCase
import com.hoabui.virtualbody3d.domain.usecase.ValidateSessionBookingUseCase
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryBookingManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryChromeManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibrarySearchManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.mapper.ExerciseLibraryUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryChromeMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiEffect
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.hoabui.virtualbody3d.domain.model.exercise.halfOpenInstantIntervalDurationMinutes
import com.hoabui.virtualbody3d.domain.repository.BookWorkoutSessionResult
import java.time.Instant
import java.time.LocalDateTime
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
        focusMuscles = listOf(Muscle.UPPER_PECTORALIS),
        description = "",
        equipment = EquipmentType.Barbell,
        safetyNotes = "",
        measurementMode = ExerciseMeasurementMode.Strength,
    )

    private fun exerciseLibraryManagers(
        workflow: SessionBookingConfirmationWorkflow,
        canConfirmLibrarySessionBookingUseCase: CanConfirmLibrarySessionBookingUseCase,
        mapper: ExerciseLibraryUiMapper,
    ): ExerciseLibraryManagers {
        val search = ExerciseLibrarySearchManager()
        val chrome = ExerciseLibraryChromeManager()
        val cart = ExerciseLibraryCartManager(ToggleExerciseInCartUseCase())
        val booking = ExerciseLibraryBookingManager(
            ResolveNextSlotSelectionAfterToggleUseCase(),
            workflow,
            canConfirmLibrarySessionBookingUseCase,
            mapper,
            cart,
            chrome,
        )
        return ExerciseLibraryManagers(search, cart, booking, chrome)
    }

    private data class ExerciseLibraryManagers(
        val search: ExerciseLibrarySearchManager,
        val cart: ExerciseLibraryCartManager,
        val booking: ExerciseLibraryBookingManager,
        val chrome: ExerciseLibraryChromeManager,
    )

    private fun createViewModel(
        exercises: Map<BodyRegion, List<Exercise>> = mapOf(BodyRegion.Chest to listOf(sampleExercise())),
    ): ExerciseLibraryViewModel {
        val getLibrary = mockk<GetExerciseLibraryUseCase>()
        every { getLibrary() } returns flowOf(exercises)
        val locations = mockk<ObserveGymLocationsUseCase>()
        every { locations() } returns flow {
            emit(listOf(GymLocation(id = "default", displayName = "Default")))
            awaitCancellation()
        }
        val migrate = mockk<MigrateLegacyWorkoutSchedulesUseCase>()
        coEvery { migrate() } returns Unit
        val appContext = mockk<Context>(relaxed = true)
        val mapper = ExerciseLibraryUiMapper(
            appContext,
            CanOpenExerciseLibrarySessionBookingUseCase(),
        )
        val confirmUseCase = mockk<ConfirmExerciseLibrarySessionUseCase>()
        every {
            confirmUseCase.prepare(any(), any(), any(), any(), any(), any())
        } answers {
            PrepareLibrarySessionConfirmResult.NoOp
        }
        coEvery {
            confirmUseCase.commit(any(), any(), any(), any(), any())
        } returns CommitLibrarySessionBookingResult.Success(
            scheduledCount = 1,
            session = WorkoutSession(
                id = "sid",
                startInstant = Instant.parse("2020-01-01T10:00:00Z"),
                endInstant = Instant.parse("2020-01-01T10:30:00Z"),
                locationId = "default",
            ),
            scheduledDateMillis = 0L,
            primaryExerciseTitle = "Sample",
            locationDisplayName = "Default",
            incrementFabBadgeBy = 1,
        )
        val workflow = SessionBookingConfirmationWorkflow(confirmUseCase)
        val updateSchedule = mockk<UpdateWorkoutScheduleFromCartDraftUseCase>(relaxed = true)
        val scheduleRepo = mockk<WorkoutScheduleRepository>(relaxed = true)
        val canConfirm = CanConfirmLibrarySessionBookingUseCase(ValidateSessionBookingUseCase())
        val m = exerciseLibraryManagers(workflow, canConfirm, mapper)
        return ExerciseLibraryViewModel(
            getLibrary,
            locations,
            migrate,
            mapper,
            canConfirm,
            updateSchedule,
            scheduleRepo,
            m.search,
            m.cart,
            m.booking,
            m.chrome,
        )
    }

    private fun createViewModelWithBookingUseCaseMocks(
        exercises: Map<BodyRegion, List<Exercise>> = mapOf(BodyRegion.Chest to listOf(sampleExercise())),
        canConfirmLibrarySessionBookingUseCase: CanConfirmLibrarySessionBookingUseCase,
    ): ExerciseLibraryViewModel {
        val getLibrary = mockk<GetExerciseLibraryUseCase>()
        every { getLibrary() } returns flowOf(exercises)
        val locations = mockk<ObserveGymLocationsUseCase>()
        every { locations() } returns flow {
            emit(listOf(GymLocation(id = "default", displayName = "Default")))
            awaitCancellation()
        }
        val migrate = mockk<MigrateLegacyWorkoutSchedulesUseCase>()
        coEvery { migrate() } returns Unit
        val appContext = mockk<Context>(relaxed = true)
        val mapper = ExerciseLibraryUiMapper(
            appContext,
            CanOpenExerciseLibrarySessionBookingUseCase(),
        )
        val confirmUseCase = mockk<ConfirmExerciseLibrarySessionUseCase>()
        every {
            confirmUseCase.prepare(any(), any(), any(), any(), any(), any())
        } answers {
            PrepareLibrarySessionConfirmResult.NoOp
        }
        coEvery {
            confirmUseCase.commit(any(), any(), any(), any(), any())
        } returns CommitLibrarySessionBookingResult.Success(
            scheduledCount = 1,
            session = WorkoutSession(
                id = "sid",
                startInstant = Instant.parse("2020-01-01T10:00:00Z"),
                endInstant = Instant.parse("2020-01-01T10:30:00Z"),
                locationId = "default",
            ),
            scheduledDateMillis = 0L,
            primaryExerciseTitle = "Sample",
            locationDisplayName = "Default",
            incrementFabBadgeBy = 1,
        )
        val workflow = SessionBookingConfirmationWorkflow(confirmUseCase)
        val updateSchedule = mockk<UpdateWorkoutScheduleFromCartDraftUseCase>(relaxed = true)
        val scheduleRepo = mockk<WorkoutScheduleRepository>(relaxed = true)
        val mgr = exerciseLibraryManagers(workflow, canConfirmLibrarySessionBookingUseCase, mapper)
        return ExerciseLibraryViewModel(
            getLibrary,
            locations,
            migrate,
            mapper,
            canConfirmLibrarySessionBookingUseCase,
            updateSchedule,
            scheduleRepo,
            mgr.search,
            mgr.cart,
            mgr.booking,
            mgr.chrome,
        )
    }

    private fun ExerciseLibraryViewModel.successData(): ExerciseLibraryUiState {
        val s = state.value
        assertTrue(s is UiState.Success)
        return (s as UiState.Success).data
    }

    private fun createViewModelForSelectionBarEdit(
        updateSchedule: UpdateWorkoutScheduleFromCartDraftUseCase,
        scheduleRepo: WorkoutScheduleRepository,
    ): ExerciseLibraryViewModel {
        val getLibrary = mockk<GetExerciseLibraryUseCase>()
        every { getLibrary() } returns flowOf(mapOf(BodyRegion.Chest to listOf(sampleExercise())))
        val locations = mockk<ObserveGymLocationsUseCase>()
        every { locations() } returns flow {
            emit(listOf(GymLocation(id = "default", displayName = "Default")))
            awaitCancellation()
        }
        val migrate = mockk<MigrateLegacyWorkoutSchedulesUseCase>()
        coEvery { migrate() } returns Unit
        val appContext = mockk<Context>(relaxed = true)
        val mapper = ExerciseLibraryUiMapper(
            appContext,
            CanOpenExerciseLibrarySessionBookingUseCase(),
        )
        val confirmUseCase = mockk<ConfirmExerciseLibrarySessionUseCase>()
        every {
            confirmUseCase.prepare(any(), any(), any(), any(), any(), any())
        } returns PrepareLibrarySessionConfirmResult.NoOp
        coEvery {
            confirmUseCase.commit(any(), any(), any(), any(), any())
        } returns CommitLibrarySessionBookingResult.InvalidDraft
        val workflow = SessionBookingConfirmationWorkflow(confirmUseCase)
        val canConfirm = CanConfirmLibrarySessionBookingUseCase(ValidateSessionBookingUseCase())
        val mgr = exerciseLibraryManagers(workflow, canConfirm, mapper)
        return ExerciseLibraryViewModel(
            getLibrary,
            locations,
            migrate,
            mapper,
            canConfirm,
            updateSchedule,
            scheduleRepo,
            mgr.search,
            mgr.cart,
            mgr.booking,
            mgr.chrome,
        )
    }

    @Test
    fun toggle_addThenRemove_emptiesCartAndClearsActive() {
        val vm = createViewModel()
        vm.toggleCardSelection("ex1")
        assertTrue(vm.successData().itemDrafts.containsKey("ex1"))
        assertEquals("ex1", vm.successData().activeExerciseId)
        vm.toggleCardSelection("ex1")
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
        vm.toggleCardSelection("a")
        vm.toggleCardSelection("b")
        vm.toggleCardSelection("c")
        vm.selectCartItem("b")
        assertEquals("b", vm.successData().activeExerciseId)
        vm.toggleCardSelection("b")
        val d = vm.successData()
        assertFalse(d.itemDrafts.containsKey("b"))
        assertEquals("c", d.activeExerciseId)
    }

    @Test
    fun removeFromCart_sameAsToggleOff() {
        val vm = createViewModel()
        vm.toggleCardSelection("ex1")
        vm.removeCartItem("ex1")
        assertTrue(vm.successData().itemDrafts.isEmpty())
        assertNull(vm.successData().activeExerciseId)
    }

    @Test
    fun selectCartItem_whenAlreadyInCart_doesNotRemove() {
        val vm = createViewModel()
        vm.toggleCardSelection("ex1")
        vm.selectCartItem("ex1")
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
        vm.toggleCardSelection("one")
        vm.toggleCardSelection("two")
        val sizeBefore = vm.successData().itemDrafts.size
        vm.selectCartItem("two")
        assertEquals(sizeBefore, vm.successData().itemDrafts.size)
        assertEquals("two", vm.successData().activeExerciseId)
    }

    @Test
    fun removeFromCart_unknownId_noop() {
        val vm = createViewModel()
        vm.toggleCardSelection("ex1")
        vm.removeCartItem("nope")
        assertNotNull(vm.successData().itemDrafts["ex1"])
    }

    @Test
    fun openSessionBooking_thenDismiss_clearsInput() = runBlocking {
        val vm = createViewModel()
        vm.toggleCardSelection("ex1")
        vm.setCartFieldManual("ex1", 0, CartSetField.SETS, "3")
        vm.setCartFieldManual("ex1", 0, CartSetField.REPS, "10")
        vm.openSessionBooking()
        assertNotNull(vm.successData().sessionBookingInput)
        vm.dismissSessionBooking()
        assertNull(vm.successData().sessionBookingInput)
    }

    @Test
    fun bookingSlotToggle_reusesCachedSections() = runBlocking {
        val vm = createViewModel()
        vm.toggleCardSelection("ex1")
        vm.setCartFieldManual("ex1", 0, CartSetField.SETS, "3")
        vm.setCartFieldManual("ex1", 0, CartSetField.REPS, "10")
        vm.openSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)
        val beforeSections = vm.successData().libraryList.sections
        vm.onBookingSlotToggled(LocalTime.of(10, 0))
        delay(WAIT_FOR_COMBINE_MS)
        assertTrue(beforeSections === vm.successData().libraryList.sections)
    }

    @Test
    fun searchQueryChange_rebuildsSections() = runBlocking {
        val vm = createViewModel()
        delay(WAIT_FOR_COMBINE_MS)
        val beforeSections = vm.successData().libraryList.sections
        vm.setSearchQuery("___no_match_xyz___")
        delay(WAIT_FOR_COMBINE_MS)
        assertFalse(beforeSections === vm.successData().libraryList.sections)
    }

    @Test
    fun searchQueryChange_whileBookingOpen_doesNotInvokeValidate() = runBlocking {
        val validate = spyk(CanConfirmLibrarySessionBookingUseCase(ValidateSessionBookingUseCase()))

        val vm = createViewModelWithBookingUseCaseMocks(
            canConfirmLibrarySessionBookingUseCase = validate,
        )
        vm.toggleCardSelection("ex1")
        vm.setCartFieldManual("ex1", 0, CartSetField.SETS, "3")
        vm.setCartFieldManual("ex1", 0, CartSetField.REPS, "10")
        vm.openSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)

        clearMocks(validate, answers = false, recordedCalls = true)

        vm.setSearchQuery("needle")
        delay(WAIT_FOR_COMBINE_MS)

        verify(exactly = 0) {
            validate(any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun bookingSlotToggle_afterSearch_reinvokesValidate() = runBlocking {
        val validate = spyk(CanConfirmLibrarySessionBookingUseCase(ValidateSessionBookingUseCase()))

        val vm = createViewModelWithBookingUseCaseMocks(
            canConfirmLibrarySessionBookingUseCase = validate,
        )
        vm.toggleCardSelection("ex1")
        vm.setCartFieldManual("ex1", 0, CartSetField.SETS, "3")
        vm.setCartFieldManual("ex1", 0, CartSetField.REPS, "10")
        vm.openSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)
        vm.setSearchQuery("ignore")
        delay(WAIT_FOR_COMBINE_MS)

        clearMocks(validate, answers = false, recordedCalls = true)

        vm.onBookingSlotToggled(LocalTime.of(10, 0))
        delay(WAIT_FOR_COMBINE_MS)

        verify(atLeast = 1) {
            validate(any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun searchQueryChange_whileBookingOpen_updatesSearchAndKeepsSessionBooking() = runBlocking {
        val vm = createViewModel()
        vm.toggleCardSelection("ex1")
        vm.setCartFieldManual("ex1", 0, CartSetField.SETS, "3")
        vm.setCartFieldManual("ex1", 0, CartSetField.REPS, "10")
        vm.openSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)
        assertNotNull(vm.successData().sessionBookingUiModel)

        vm.setSearchQuery("filter-text")
        delay(WAIT_FOR_COMBINE_MS)

        val data = vm.successData()
        assertEquals("filter-text", data.searchQuery)
        assertNotNull(data.sessionBookingUiModel)
    }

    @Test
    fun isAddToSessionEnabled_falseWhenCartEmpty() = runBlocking {
        val vm = createViewModel()
        delay(WAIT_FOR_COMBINE_MS)
        assertFalse(vm.successData().libraryList.isAddToSessionEnabled)
    }

    @Test
    fun isAddToSessionEnabled_trueWhenCartValid() = runBlocking {
        val vm = createViewModel()
        vm.toggleCardSelection("ex1")
        vm.setCartFieldManual("ex1", 0, CartSetField.SETS, "3")
        vm.setCartFieldManual("ex1", 0, CartSetField.REPS, "10")
        delay(WAIT_FOR_COMBINE_MS)
        assertTrue(vm.successData().libraryList.isAddToSessionEnabled)
    }

    @Test
    fun confirmSessionBooking_success_clearsCartAndEmitsSummaryEffect() = runBlocking {
        val getLibrary = mockk<GetExerciseLibraryUseCase>()
        every { getLibrary() } returns flowOf(mapOf(BodyRegion.Chest to listOf(sampleExercise())))
        val locations = mockk<ObserveGymLocationsUseCase>()
        every { locations() } returns flow {
            emit(listOf(GymLocation(id = "default", displayName = "Default")))
            awaitCancellation()
        }
        val migrate = mockk<MigrateLegacyWorkoutSchedulesUseCase>()
        coEvery { migrate() } returns Unit
        val appContext = mockk<Context>(relaxed = true)
        val mapper = ExerciseLibraryUiMapper(
            appContext,
            CanOpenExerciseLibrarySessionBookingUseCase(),
        )
        val bookInner = mockk<BookWorkoutSessionUseCase>()
        val resolvedFromRepo = WorkoutSession(
            id = "booked-session",
            startInstant = Instant.parse("1970-01-01T10:00:00Z"),
            endInstant = Instant.parse("1970-01-01T11:00:00Z"),
            locationId = "default",
        )
        coEvery { bookInner(any(), any()) } returns BookWorkoutSessionResult.Success(1, resolvedFromRepo)
        val confirmUseCase = ConfirmExerciseLibrarySessionUseCase(bookInner, ValidateSessionBookingUseCase())
        val workflow = SessionBookingConfirmationWorkflow(confirmUseCase)
        val updateSchedule = mockk<UpdateWorkoutScheduleFromCartDraftUseCase>(relaxed = true)
        val scheduleRepo = mockk<WorkoutScheduleRepository>(relaxed = true)
        val canConfirm = CanConfirmLibrarySessionBookingUseCase(ValidateSessionBookingUseCase())
        val mgr = exerciseLibraryManagers(workflow, canConfirm, mapper)
        val vm = ExerciseLibraryViewModel(
            getLibrary,
            locations,
            migrate,
            mapper,
            canConfirm,
            updateSchedule,
            scheduleRepo,
            mgr.search,
            mgr.cart,
            mgr.booking,
            mgr.chrome,
        )
        vm.toggleCardSelection("ex1")
        vm.setCartFieldManual("ex1", 0, CartSetField.SETS, "3")
        vm.setCartFieldManual("ex1", 0, CartSetField.REPS, "10")
        vm.openSessionBooking()
        vm.onBookingSlotToggled(LocalTime.of(10, 0))
        delay(WAIT_FOR_COMBINE_MS)
        vm.onBookingSlotToggled(LocalTime.of(10, 30))
        delay(WAIT_FOR_COMBINE_MS)
        assertTrue(requireNotNull(vm.successData().sessionBookingUiModel).isBookingConfirmEnabled)
        var successEffect: ExerciseLibraryUiEffect.ShowAddExerciseSuccess? = null
        val collectJob = launch {
            vm.uiEffects.collect { effect ->
                if (effect is ExerciseLibraryUiEffect.ShowAddExerciseSuccess) {
                    successEffect = effect
                    return@collect
                }
            }
        }
        vm.confirmSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)
        val d = vm.successData()
        assertTrue(d.itemDrafts.isEmpty())
        val summary = requireNotNull(successEffect).summary
        assertEquals(1, summary.exerciseCount)
        assertEquals("Sample", summary.primaryExerciseTitle)
        assertEquals(
            60L,
            halfOpenInstantIntervalDurationMinutes(summary.sessionStartInstant, summary.sessionEndInstant),
        )
        collectJob.cancel()
    }

    @Test
    fun intentOnly_selectionBarEdit_confirmPersistsAndExitsEditMode() = runBlocking {
        val updateSchedule = mockk<UpdateWorkoutScheduleFromCartDraftUseCase>()
        coEvery {
            updateSchedule.invoke(
                rowId = any(),
                exerciseId = any(),
                measurementMode = any(),
                sets = any(),
                reps = any(),
                weightKg = any(),
                durationSeconds = any(),
            )
        } returns true
        val scheduleRepo = mockk<WorkoutScheduleRepository>()
        coEvery { scheduleRepo.getWorkoutScheduleByRowId(42L) } returns WorkoutSchedule(
            id = "sch-42",
            rowId = 42L,
            exerciseId = "ex1",
            scheduledAt = LocalDateTime.of(2020, 1, 1, 10, 0),
            sets = 3,
            reps = 10,
            weightKg = 50.0,
            restSeconds = 60,
            notes = null,
            measurementMode = ExerciseMeasurementMode.Strength,
        )
        val vm = createViewModelForSelectionBarEdit(updateSchedule, scheduleRepo)

        vm.startSelectionBarEditFromScheduleRow(42L)
        delay(WAIT_FOR_COMBINE_MS)
        assertTrue(vm.successData().chromeMode is ExerciseLibraryChromeMode.EditingScheduleRow)

        vm.confirmSelectionBarEdit()
        delay(WAIT_FOR_COMBINE_MS)

        coVerify(exactly = 1) {
            updateSchedule.invoke(
                rowId = 42L,
                exerciseId = "ex1",
                measurementMode = ExerciseMeasurementMode.Strength,
                sets = any(),
                reps = any(),
                weightKg = any(),
                durationSeconds = null,
            )
        }
        assertFalse(vm.successData().chromeMode is ExerciseLibraryChromeMode.EditingScheduleRow)
    }

    @Test
    fun intentOnly_selectionBarEdit_cancelExitsWithoutPersist() = runBlocking {
        val updateSchedule = mockk<UpdateWorkoutScheduleFromCartDraftUseCase>(relaxed = true)
        val scheduleRepo = mockk<WorkoutScheduleRepository>()
        coEvery { scheduleRepo.getWorkoutScheduleByRowId(77L) } returns WorkoutSchedule(
            id = "sch-77",
            rowId = 77L,
            exerciseId = "ex1",
            scheduledAt = LocalDateTime.of(2020, 1, 1, 12, 0),
            sets = 2,
            reps = 8,
            weightKg = 40.0,
            restSeconds = 60,
            notes = null,
            measurementMode = ExerciseMeasurementMode.Strength,
        )
        val vm = createViewModelForSelectionBarEdit(updateSchedule, scheduleRepo)

        vm.startSelectionBarEditFromScheduleRow(77L)
        delay(WAIT_FOR_COMBINE_MS)
        vm.cancelSelectionBarEdit()
        delay(WAIT_FOR_COMBINE_MS)

        coVerify(exactly = 0) {
            updateSchedule.invoke(any(), any(), any(), any(), any(), any(), any())
        }
        assertFalse(vm.successData().chromeMode is ExerciseLibraryChromeMode.EditingScheduleRow)
    }

    private companion object {
        const val WAIT_FOR_COMBINE_MS = 200L
    }
}
