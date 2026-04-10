package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import com.hoabui.virtualbody3d.domain.usecase.BuildLibraryBookingDensityKernelsUseCase
import com.hoabui.virtualbody3d.domain.usecase.CalculateBookingDensityUseCase
import com.hoabui.virtualbody3d.domain.usecase.CanConfirmLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.CanOpenExerciseLibrarySessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.ResolveNextSlotSelectionAfterToggleUseCase
import com.hoabui.virtualbody3d.domain.usecase.SessionBookingConfirmationWorkflow
import com.hoabui.virtualbody3d.domain.usecase.SyncSessionBookingWithBusyUseCase
import com.hoabui.virtualbody3d.domain.usecase.ToggleExerciseInCartUseCase
import com.hoabui.virtualbody3d.domain.usecase.UpdateExerciseDraftUseCase
import com.hoabui.virtualbody3d.domain.usecase.ValidateSessionBookingUseCase
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.CommitLibrarySessionBookingSuccessUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseLibraryCatalogUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mapper.ExerciseLibraryUiMapper
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
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.domain.usecase.MigrateLegacyWorkoutSchedulesUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveBusyIntervalsUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveGymLocationsUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveWorkoutSchedulesUseCase
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

/**
 * Smoke check: search-only intents do not invoke the booking workflow ([SessionBookingConfirmationWorkflow.run]).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseLibraryReducerDispatchTest {

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
        val appContext = mockk<Context>(relaxed = true)
        val mapper = ExerciseLibraryUiMapper(
            appContext,
            CanOpenExerciseLibrarySessionBookingUseCase(),
        )
        val catalogMapper = ExerciseLibraryCatalogUiMapper(appContext)
        val workflow = mockk<SessionBookingConfirmationWorkflow>()
        every { workflow.run(any()) } returns kotlinx.coroutines.flow.flowOf()
        val reducer = ExerciseLibraryReducer(CommitLibrarySessionBookingSuccessUiMapper())
        val vm = ExerciseLibraryViewModel(
            getLibrary,
            workflow,
            locations,
            busy,
            workoutSchedules,
            migrate,
            mapper,
            catalogMapper,
            reducer,
            ToggleExerciseInCartUseCase(),
            UpdateExerciseDraftUseCase(),
            BuildLibraryBookingDensityKernelsUseCase(CalculateBookingDensityUseCase()),
            CanConfirmLibrarySessionBookingUseCase(ValidateSessionBookingUseCase()),
            SyncSessionBookingWithBusyUseCase(),
            ResolveNextSlotSelectionAfterToggleUseCase(),
        )
        vm.updateSearchQuery("bench")
        verify(exactly = 0) { workflow.run(any()) }
    }
}
