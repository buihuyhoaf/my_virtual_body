package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import com.hoabui.virtualbody3d.domain.usecase.CanConfirmLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.CanOpenExerciseLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.ResolveNextSlotSelectionAfterToggleUseCase
import com.hoabui.virtualbody3d.domain.usecase.SessionBookingConfirmationWorkflow
import com.hoabui.virtualbody3d.domain.usecase.ToggleExerciseInCartUseCase
import com.hoabui.virtualbody3d.domain.usecase.UpdateExerciseDraftUseCase
import com.hoabui.virtualbody3d.domain.usecase.UpdateWorkoutScheduleFromCartDraftUseCase
import com.hoabui.virtualbody3d.domain.usecase.ValidateSessionBookingUseCase
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.CommitLibrarySessionBookingSuccessUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseLibraryCatalogUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mapper.ExerciseLibraryUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibraryIntent
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.reducer.ExerciseLibraryReducer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.Muscle
import com.hoabui.virtualbody3d.domain.model.exercise.TestMuscleDictionary
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.domain.usecase.MigrateLegacyWorkoutSchedulesUseCase
import com.hoabui.virtualbody3d.domain.model.calendar.ExerciseLibraryWeeklyDayItem
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseLibraryWeeklySummaryUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveGymLocationsUseCase
import android.content.Context
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import io.mockk.coEvery
import org.junit.After
import org.junit.Before
import java.time.LocalDate

/**
 * Smoke check: search-only intents do not invoke the booking workflow ([SessionBookingConfirmationWorkflow.run]).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseLibraryReducerDispatchTest {
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

    @Test
    fun setSearchQuery_doesNotRunBookingWorkflow() = runTest {
        val ex = Exercise(
            id = "ex1",
            name = "Sample",
            image = ImageSource.LocalResource("p"),
            category = ExerciseCategory.Strength,
            bodyRegion = BodyRegion.Chest,
            focusMuscles = listOf(Muscle.UPPER_PECTORALIS),
            description = "",
            equipment = EquipmentType.Barbell,
            safetyNotes = "",
            measurementMode = ExerciseMeasurementMode.Strength,
        )
        val getLibrary = mockk<GetExerciseLibraryUseCase>()
        every { getLibrary() } returns flowOf(mapOf(BodyRegion.Chest to listOf(ex)))
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
        val workflow = mockk<SessionBookingConfirmationWorkflow>()
        every { workflow.run(any()) } returns kotlinx.coroutines.flow.flowOf()
        val reducer = ExerciseLibraryReducer(
            muscleDictionary = testMuscleDictionary,
        )
        val updateSchedule = mockk<UpdateWorkoutScheduleFromCartDraftUseCase>(relaxed = true)
        val scheduleRepo = mockk<WorkoutScheduleRepository>(relaxed = true)
        val vm = ExerciseLibraryViewModel(
            getLibrary,
            workflow,
            CommitLibrarySessionBookingSuccessUiMapper(),
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
        vm.onEvent(ExerciseLibraryIntent.SetSearchQuery("bench"))
        verify(exactly = 0) { workflow.run(any()) }
    }
}
