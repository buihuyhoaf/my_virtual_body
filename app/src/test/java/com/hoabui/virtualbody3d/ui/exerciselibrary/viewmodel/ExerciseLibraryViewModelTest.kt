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
import com.hoabui.virtualbody3d.domain.model.exercise.Muscle
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.usecase.BookWorkoutSessionUseCase
import com.hoabui.virtualbody3d.domain.usecase.CommitLibrarySessionBookingResult
import com.hoabui.virtualbody3d.domain.usecase.ConfirmExerciseLibrarySessionUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.domain.usecase.PrepareLibrarySessionConfirmResult
import com.hoabui.virtualbody3d.domain.usecase.CancelSelectionBarEditUseCase
import com.hoabui.virtualbody3d.domain.usecase.CanConfirmLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.ClearCartUseCase
import com.hoabui.virtualbody3d.domain.usecase.ConfirmSelectionBarEditUseCase
import com.hoabui.virtualbody3d.domain.usecase.DismissSessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetWorkoutScheduleByRowUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseCatalogUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseLibraryChromeModeUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseLibraryUiStateUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnBookingClearTimeSelectionUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnBookingDateSelectedUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnBookingLocationSelectedUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnBookingSlotToggledUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnLongSessionEditUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnLongSessionProceedAnywayUseCase
import com.hoabui.virtualbody3d.domain.usecase.OpenSessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.RemoveCartItemUseCase
import com.hoabui.virtualbody3d.domain.usecase.RunExerciseLibraryBookingConfirmationUseCase
import com.hoabui.virtualbody3d.domain.usecase.SelectCartItemUseCase
import com.hoabui.virtualbody3d.domain.usecase.SessionBookingConfirmationWorkflow
import com.hoabui.virtualbody3d.domain.usecase.SetCartFieldManualUseCase
import com.hoabui.virtualbody3d.domain.usecase.SetInitialBodyRegionFilterUseCase
import com.hoabui.virtualbody3d.domain.usecase.SetInitialExerciseCategoryFilterUseCase
import com.hoabui.virtualbody3d.domain.usecase.SetSearchQueryUseCase
import com.hoabui.virtualbody3d.domain.usecase.StartSelectionBarEditFromScheduleRowUseCase
import com.hoabui.virtualbody3d.domain.usecase.StepCartFieldUseCase
import com.hoabui.virtualbody3d.domain.usecase.ToggleCardSelectionUseCase
import com.hoabui.virtualbody3d.domain.usecase.ToggleCartExpandedUseCase
import com.hoabui.virtualbody3d.domain.usecase.ResolveNextSlotSelectionAfterToggleUseCase
import com.hoabui.virtualbody3d.domain.usecase.ToggleExerciseInCartUseCase
import com.hoabui.virtualbody3d.domain.usecase.UpdateWorkoutScheduleFromCartDraftUseCase
import com.hoabui.virtualbody3d.domain.usecase.ValidateSessionBookingUseCase
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryBookingManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
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
import kotlinx.coroutines.delay
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
        val cart = ExerciseLibraryCartManager(ToggleExerciseInCartUseCase())
        val booking = ExerciseLibraryBookingManager(
            ResolveNextSlotSelectionAfterToggleUseCase(),
            workflow,
            canConfirmLibrarySessionBookingUseCase,
            mapper,
            cart,
        )
        return ExerciseLibraryManagers(search, cart, booking)
    }

    private data class ExerciseLibraryManagers(
        val search: ExerciseLibrarySearchManager,
        val cart: ExerciseLibraryCartManager,
        val booking: ExerciseLibraryBookingManager,
    )

    private fun exerciseLibraryViewModelFromDeps(
        getLibrary: GetExerciseLibraryUseCase,
        mapper: ExerciseLibraryUiMapper,
        updateSchedule: UpdateWorkoutScheduleFromCartDraftUseCase,
        scheduleRepo: WorkoutScheduleRepository,
        managers: ExerciseLibraryManagers,
    ): ExerciseLibraryViewModel {
        val observeCatalog = ObserveExerciseCatalogUseCase(getLibrary)
        val observeUiState = ObserveExerciseLibraryUiStateUseCase(
            observeCatalog,
            managers.search,
            managers.cart,
            mapper,
        )
        val observeChromeMode = ObserveExerciseLibraryChromeModeUseCase(managers.cart)
        val getScheduleRow = GetWorkoutScheduleByRowUseCase(scheduleRepo)
        return ExerciseLibraryViewModel(
            observeExerciseCatalogUseCase = observeCatalog,
            observeExerciseLibraryUiStateUseCase = observeUiState,
            observeExerciseLibraryChromeModeUseCase = observeChromeMode,
            setSearchQueryUseCase = SetSearchQueryUseCase(managers.search),
            setInitialExerciseCategoryFilterUseCase = SetInitialExerciseCategoryFilterUseCase(managers.search),
            setInitialBodyRegionFilterUseCase = SetInitialBodyRegionFilterUseCase(managers.search),
            toggleCardSelectionUseCase = ToggleCardSelectionUseCase(
                observeUiState,
                observeCatalog,
                managers.cart,
            ),
            selectCartItemUseCase = SelectCartItemUseCase(managers.cart),
            removeCartItemUseCase = RemoveCartItemUseCase(managers.cart),
            clearCartUseCase = ClearCartUseCase(managers.cart, managers.booking),
            stepCartFieldUseCase = StepCartFieldUseCase(managers.cart),
            setCartFieldManualUseCase = SetCartFieldManualUseCase(managers.cart),
            toggleCartExpandedUseCase = ToggleCartExpandedUseCase(managers.cart),
            startSelectionBarEditFromScheduleRowUseCase = StartSelectionBarEditFromScheduleRowUseCase(
                getScheduleRow,
                managers.cart,
            ),
            cancelSelectionBarEditUseCase = CancelSelectionBarEditUseCase(managers.cart),
            confirmSelectionBarEditUseCase = ConfirmSelectionBarEditUseCase(
                observeUiState,
                getScheduleRow,
                updateSchedule,
                managers.cart,
            ),
        )
    }

    private fun createViewModel(
        exercises: Map<BodyRegion, List<Exercise>> = mapOf(BodyRegion.Chest to listOf(sampleExercise())),
    ): ExerciseLibraryViewModel {
        val getLibrary = mockk<GetExerciseLibraryUseCase>()
        every { getLibrary() } returns flowOf(exercises)
        val appContext = mockk<Context>(relaxed = true)
        val mapper = ExerciseLibraryUiMapper(appContext)
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
        return exerciseLibraryViewModelFromDeps(
            getLibrary,
            mapper,
            updateSchedule,
            scheduleRepo,
            m,
        )
    }

    private fun createViewModelWithBookingUseCaseMocks(
        exercises: Map<BodyRegion, List<Exercise>> = mapOf(BodyRegion.Chest to listOf(sampleExercise())),
        canConfirmLibrarySessionBookingUseCase: CanConfirmLibrarySessionBookingUseCase,
    ): ExerciseLibraryViewModel {
        val getLibrary = mockk<GetExerciseLibraryUseCase>()
        every { getLibrary() } returns flowOf(exercises)
        val appContext = mockk<Context>(relaxed = true)
        val mapper = ExerciseLibraryUiMapper(appContext)
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
        return exerciseLibraryViewModelFromDeps(
            getLibrary,
            mapper,
            updateSchedule,
            scheduleRepo,
            mgr,
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
        val appContext = mockk<Context>(relaxed = true)
        val mapper = ExerciseLibraryUiMapper(appContext)
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
        return exerciseLibraryViewModelFromDeps(
            getLibrary,
            mapper,
            updateSchedule,
            scheduleRepo,
            mgr,
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
    fun searchQueryChange_rebuildsSections() = runBlocking {
        val vm = createViewModel()
        delay(WAIT_FOR_COMBINE_MS)
        val beforeSections = vm.successData().libraryList.sections
        vm.setSearchQuery("___no_match_xyz___")
        delay(WAIT_FOR_COMBINE_MS)
        assertFalse(beforeSections === vm.successData().libraryList.sections)
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
        assertTrue(vm.chromeMode.value is ExerciseLibraryChromeMode.EditingScheduleRow)

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
        assertFalse(vm.chromeMode.value is ExerciseLibraryChromeMode.EditingScheduleRow)
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
        assertFalse(vm.chromeMode.value is ExerciseLibraryChromeMode.EditingScheduleRow)
    }

    private companion object {
        const val WAIT_FOR_COMBINE_MS = 200L
    }
}
