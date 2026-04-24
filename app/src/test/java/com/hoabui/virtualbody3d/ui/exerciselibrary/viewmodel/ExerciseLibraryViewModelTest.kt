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
import com.hoabui.virtualbody3d.domain.model.exercise.RegionGroup
import com.hoabui.virtualbody3d.domain.model.exercise.TestMuscleDictionary
import com.hoabui.virtualbody3d.domain.usecase.BookWorkoutSessionUseCase
import com.hoabui.virtualbody3d.domain.usecase.CommitLibrarySessionBookingResult
import com.hoabui.virtualbody3d.domain.usecase.ConfirmExerciseLibrarySessionUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.domain.usecase.MigrateLegacyWorkoutSchedulesUseCase
import com.hoabui.virtualbody3d.domain.model.calendar.ExerciseLibraryWeeklyDayItem
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseLibraryWeeklySummaryUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveGymLocationsUseCase
import com.hoabui.virtualbody3d.domain.usecase.PrepareLibrarySessionConfirmResult
import com.hoabui.virtualbody3d.domain.usecase.CanConfirmLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.CanOpenExerciseLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.SessionBookingConfirmationWorkflow
import com.hoabui.virtualbody3d.domain.usecase.ResolveNextSlotSelectionAfterToggleUseCase
import com.hoabui.virtualbody3d.domain.usecase.ToggleExerciseInCartUseCase
import com.hoabui.virtualbody3d.domain.usecase.UpdateExerciseDraftUseCase
import com.hoabui.virtualbody3d.domain.usecase.UpdateWorkoutScheduleFromCartDraftUseCase
import com.hoabui.virtualbody3d.domain.usecase.ValidateSessionBookingUseCase
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.CommitLibrarySessionBookingSuccessUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseLibraryCatalogUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mapper.ExerciseLibraryUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.reducer.ExerciseLibraryReducer
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
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
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseLibraryViewModelTest {
    private val testMuscleDictionary = TestMuscleDictionary()

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
        val observeWeeklySummary = mockk<ObserveExerciseLibraryWeeklySummaryUseCase>()
        every { observeWeeklySummary(any()) } returns flow {
            emit(
                (0L..6L).map { offset ->
                    ExerciseLibraryWeeklyDayItem(
                        date = LocalDate.of(2020, 1, 6).plusDays(offset),
                        sessionCount = 0,
                        isToday = offset == 0L,
                    )
                },
            )
            awaitCancellation()
        }
        val appContext = mockk<Context>(relaxed = true)
        val mapper = ExerciseLibraryUiMapper(
            appContext,
            CanOpenExerciseLibrarySessionBookingUseCase(),
        )
        val catalogMapper = ExerciseLibraryCatalogUiMapper(appContext)
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
        val reducer = ExerciseLibraryReducer(
            commitSuccessUiMapper = CommitLibrarySessionBookingSuccessUiMapper(),
            muscleDictionary = testMuscleDictionary,
        )
        val updateSchedule = mockk<UpdateWorkoutScheduleFromCartDraftUseCase>(relaxed = true)
        val scheduleRepo = mockk<WorkoutScheduleRepository>(relaxed = true)
        return ExerciseLibraryViewModel(
            getLibrary,
            workflow,
            locations,
            observeWeeklySummary,
            migrate,
            mapper,
            catalogMapper,
            reducer,
            ToggleExerciseInCartUseCase(),
            UpdateExerciseDraftUseCase(),
            CanConfirmLibrarySessionBookingUseCase(ValidateSessionBookingUseCase()),
            ResolveNextSlotSelectionAfterToggleUseCase(),
            updateSchedule,
            scheduleRepo,
            testMuscleDictionary,
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
        val observeWeeklySummary = mockk<ObserveExerciseLibraryWeeklySummaryUseCase>()
        every { observeWeeklySummary(any()) } returns flow {
            emit(
                (0L..6L).map { offset ->
                    ExerciseLibraryWeeklyDayItem(
                        date = LocalDate.of(2020, 1, 6).plusDays(offset),
                        sessionCount = 0,
                        isToday = offset == 0L,
                    )
                },
            )
            awaitCancellation()
        }
        val appContext = mockk<Context>(relaxed = true)
        val mapper = ExerciseLibraryUiMapper(
            appContext,
            CanOpenExerciseLibrarySessionBookingUseCase(),
        )
        val catalogMapper = ExerciseLibraryCatalogUiMapper(appContext)
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
        val reducer = ExerciseLibraryReducer(
            commitSuccessUiMapper = CommitLibrarySessionBookingSuccessUiMapper(),
            muscleDictionary = testMuscleDictionary,
        )
        val updateSchedule = mockk<UpdateWorkoutScheduleFromCartDraftUseCase>(relaxed = true)
        val scheduleRepo = mockk<WorkoutScheduleRepository>(relaxed = true)
        return ExerciseLibraryViewModel(
            getLibrary,
            workflow,
            locations,
            observeWeeklySummary,
            migrate,
            mapper,
            catalogMapper,
            reducer,
            ToggleExerciseInCartUseCase(),
            UpdateExerciseDraftUseCase(),
            canConfirmLibrarySessionBookingUseCase,
            ResolveNextSlotSelectionAfterToggleUseCase(),
            updateSchedule,
            scheduleRepo,
            testMuscleDictionary,
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
        assertTrue(vm.successData().cart.itemDrafts.containsKey("ex1"))
        assertEquals("ex1", vm.successData().cart.activeExerciseId)
        vm.toggleExerciseInCartFromList("ex1")
        assertTrue(vm.successData().cart.itemDrafts.isEmpty())
        assertTrue(vm.successData().cart.draftOrder.isEmpty())
        assertNull(vm.successData().cart.activeExerciseId)
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
        assertEquals("b", vm.successData().cart.activeExerciseId)
        vm.toggleExerciseInCartFromList("b")
        val d = vm.successData()
        assertFalse(d.cart.itemDrafts.containsKey("b"))
        assertEquals("c", d.cart.activeExerciseId)
    }

    @Test
    fun removeFromCart_sameAsToggleOff() {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("ex1")
        vm.removeFromCart("ex1")
        assertTrue(vm.successData().cart.itemDrafts.isEmpty())
        assertNull(vm.successData().cart.activeExerciseId)
    }

    @Test
    fun ensureInCartFromDetail_whenAlreadyInCart_doesNotRemove() {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("ex1")
        vm.ensureInCartAndFocusFromDetail("ex1")
        assertTrue(vm.successData().cart.itemDrafts.containsKey("ex1"))
        assertEquals("ex1", vm.successData().cart.activeExerciseId)
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
        val sizeBefore = vm.successData().cart.itemDrafts.size
        vm.setActiveCartExercise("two")
        assertEquals(sizeBefore, vm.successData().cart.itemDrafts.size)
        assertEquals("two", vm.successData().cart.activeExerciseId)
    }

    @Test
    fun focusStripQuadrantTap_togglesRegionGroupSelectionAndUpdatesStrip() {
        val vm = createViewModel()
        vm.onFocusStripQuadrantTapped(1)
        val selected = vm.successData()
        assertTrue(selected.focusStripClickSelection.containsKey(RegionGroup.UpperBack))
        assertEquals("back_full_back", selected.focusMusclesStrip[1])

        vm.onFocusStripQuadrantTapped(1)
        val cleared = vm.successData()
        assertFalse(cleared.focusStripClickSelection.containsKey(RegionGroup.UpperBack))
    }

    @Test
    fun removeFromCart_unknownId_noop() {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("ex1")
        vm.removeFromCart("nope")
        assertNotNull(vm.successData().cart.itemDrafts["ex1"])
    }

    @Test
    fun openSessionBooking_thenDismiss_clearsInput() = runBlocking {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("ex1")
        vm.updateActiveDraft("3", "10")
        vm.openSessionBooking()
        assertNotNull(vm.successData().sessionBooking.input)
        vm.dismissSessionBooking()
        assertNull(vm.successData().sessionBooking.input)
    }

    @Test
    fun bookingSlotToggle_reusesCachedSections() = runBlocking {
        val vm = createViewModel()
        vm.toggleExerciseInCartFromList("ex1")
        vm.updateActiveDraft("3", "10")
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
        vm.updateSearchQuery("___no_match_xyz___")
        delay(WAIT_FOR_COMBINE_MS)
        assertFalse(beforeSections === vm.successData().libraryList.sections)
    }

    @Test
    fun searchQueryChange_whileBookingOpen_doesNotInvokeValidate() = runBlocking {
        val validate = spyk(CanConfirmLibrarySessionBookingUseCase(ValidateSessionBookingUseCase()))

        val vm = createViewModelWithBookingUseCaseMocks(
            canConfirmLibrarySessionBookingUseCase = validate,
        )
        vm.toggleExerciseInCartFromList("ex1")
        vm.updateActiveDraft("3", "10")
        vm.openSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)

        clearMocks(validate, answers = false, recordedCalls = true)

        vm.updateSearchQuery("needle")
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
        vm.toggleExerciseInCartFromList("ex1")
        vm.updateActiveDraft("3", "10")
        vm.openSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)
        vm.updateSearchQuery("ignore")
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
        vm.toggleExerciseInCartFromList("ex1")
        vm.updateActiveDraft("3", "10")
        vm.openSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)
        assertNotNull(vm.successData().sessionBooking.uiModel)

        vm.updateSearchQuery("filter-text")
        delay(WAIT_FOR_COMBINE_MS)

        val data = vm.successData()
        assertEquals("filter-text", data.filters.searchQuery)
        assertNotNull(data.sessionBooking.uiModel)
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
        vm.toggleExerciseInCartFromList("ex1")
        vm.updateActiveDraft("3", "10")
        delay(WAIT_FOR_COMBINE_MS)
        assertTrue(vm.successData().libraryList.isAddToSessionEnabled)
    }

    @Test
    fun confirmSessionBooking_success_clearsCartAndSetsSummary() = runBlocking {
        val getLibrary = mockk<GetExerciseLibraryUseCase>()
        every { getLibrary() } returns flowOf(mapOf(BodyRegion.Chest to listOf(sampleExercise())))
        val locations = mockk<ObserveGymLocationsUseCase>()
        every { locations() } returns flow {
            emit(listOf(GymLocation(id = "default", displayName = "Default")))
            awaitCancellation()
        }
        val migrate = mockk<MigrateLegacyWorkoutSchedulesUseCase>()
        coEvery { migrate() } returns Unit
        val observeWeeklySummary = mockk<ObserveExerciseLibraryWeeklySummaryUseCase>()
        every { observeWeeklySummary(any()) } returns flow {
            emit(
                (0L..6L).map { offset ->
                    ExerciseLibraryWeeklyDayItem(
                        date = LocalDate.of(2020, 1, 6).plusDays(offset),
                        sessionCount = 0,
                        isToday = offset == 0L,
                    )
                },
            )
            awaitCancellation()
        }
        val appContext = mockk<Context>(relaxed = true)
        val mapper = ExerciseLibraryUiMapper(
            appContext,
            CanOpenExerciseLibrarySessionBookingUseCase(),
        )
        val catalogMapper = ExerciseLibraryCatalogUiMapper(appContext)
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
        val reducer = ExerciseLibraryReducer(
            commitSuccessUiMapper = CommitLibrarySessionBookingSuccessUiMapper(),
            muscleDictionary = testMuscleDictionary,
        )
        val updateSchedule = mockk<UpdateWorkoutScheduleFromCartDraftUseCase>(relaxed = true)
        val scheduleRepo = mockk<WorkoutScheduleRepository>(relaxed = true)
        val vm = ExerciseLibraryViewModel(
            getLibrary,
            workflow,
            locations,
            observeWeeklySummary,
            migrate,
            mapper,
            catalogMapper,
            reducer,
            ToggleExerciseInCartUseCase(),
            UpdateExerciseDraftUseCase(),
            CanConfirmLibrarySessionBookingUseCase(ValidateSessionBookingUseCase()),
            ResolveNextSlotSelectionAfterToggleUseCase(),
            updateSchedule,
            scheduleRepo,
            testMuscleDictionary,
        )
        vm.toggleExerciseInCartFromList("ex1")
        vm.updateActiveDraft("3", "10")
        vm.openSessionBooking()
        vm.onBookingSlotToggled(LocalTime.of(10, 0))
        delay(WAIT_FOR_COMBINE_MS)
        vm.onBookingSlotToggled(LocalTime.of(10, 30))
        delay(WAIT_FOR_COMBINE_MS)
        assertTrue(requireNotNull(vm.successData().sessionBooking.uiModel).isBookingConfirmEnabled)
        vm.confirmSessionBooking()
        delay(WAIT_FOR_COMBINE_MS)
        val d = vm.successData()
        assertTrue(d.cart.itemDrafts.isEmpty())
        val summary = requireNotNull(d.chrome.addExerciseSuccess)
        assertEquals(1, summary.exerciseCount)
        assertEquals("Sample", summary.primaryExerciseTitle)
        assertEquals(
            60L,
            halfOpenInstantIntervalDurationMinutes(summary.sessionStartInstant, summary.sessionEndInstant),
        )
    }

    private companion object {
        const val WAIT_FOR_COMBINE_MS = 200L
    }
}
