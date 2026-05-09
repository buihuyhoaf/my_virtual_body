package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryBookingManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.mapper.ExerciseLibraryUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.BookingPipelineRow
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.exerciseLibraryBookingPresentationKey
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingEditorPresentationState
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingInput
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.toLibraryCartDraft
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@ActivityRetainedScoped
class ObserveSessionBookingEditorUiStateUseCase @Inject constructor(
    private val observeExerciseLibraryUiStateUseCase: ObserveExerciseLibraryUiStateUseCase,
    private val observeGymLocationsUseCase: ObserveGymLocationsUseCase,
    private val bookingManager: ExerciseLibraryBookingManager,
    private val exerciseLibraryUiMapper: ExerciseLibraryUiMapper,
    private val canConfirmLibrarySessionBookingUseCase: CanConfirmLibrarySessionBookingUseCase,
) {

    private lateinit var gymLocationsStateFlow: StateFlow<ImmutableList<GymLocation>>
    private lateinit var mergedPresentationFlow: StateFlow<SessionBookingEditorPresentationState>

    fun observe(scope: CoroutineScope): StateFlow<SessionBookingEditorPresentationState> {
        if (::mergedPresentationFlow.isInitialized) {
            return mergedPresentationFlow
        }

        gymLocationsStateFlow = observeGymLocationsUseCase()
            .map { it.toImmutableList() }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = persistentListOf(),
            )

        val libraryFlow = observeExerciseLibraryUiStateUseCase.observe(scope)

        val bookingProjectionFlow = combine(
            libraryFlow,
            gymLocationsStateFlow,
            bookingManager.sessionBookingInput,
        ) { base, gymLocs, sessionInput ->
            val withMeasurement = base.copy(
                libraryList = base.libraryList.copy(
                    exerciseMeasurementById = base.libraryList.exerciseMeasurementById,
                ),
            )
            if (sessionInput == null) {
                BookingPipelineRow(
                    dedupeKey = null,
                    filtersWithMeasurement = withMeasurement,
                    gymLocations = gymLocs,
                    sessionBookingInput = null,
                )
            } else {
                val key = exerciseLibraryBookingPresentationKey(
                    filtersWithMeasurement = withMeasurement,
                    sessionBookingInput = sessionInput,
                    gymLocationsVersion = gymLocs,
                )
                BookingPipelineRow(
                    dedupeKey = key,
                    filtersWithMeasurement = withMeasurement,
                    gymLocations = gymLocs,
                    sessionBookingInput = sessionInput,
                )
            }
        }
            .distinctUntilChanged { a, b -> a.dedupeKey == b.dedupeKey }
            .map { row ->
                if (row.dedupeKey == null || row.sessionBookingInput == null) {
                    null
                } else {
                    buildBookingUi(
                        filtersWithMeasurement = row.filtersWithMeasurement,
                        sessionBookingInput = row.sessionBookingInput,
                        gymLocations = row.gymLocations,
                    )
                }
            }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

        mergedPresentationFlow = combine(
            libraryFlow,
            bookingProjectionFlow,
            bookingManager.sessionBookingInput,
            bookingManager.sessionBookingWorkflowPhase,
        ) { lib, bookingUi, sessionInput, phase ->
            SessionBookingEditorPresentationState(
                libraryUi = lib,
                sessionBookingInput = sessionInput,
                sessionBookingUiModel = bookingUi,
                sessionBookingWorkflowPhase = phase,
            )
        }
            .distinctUntilChanged()
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SessionBookingEditorPresentationState(
                    libraryUi = ExerciseLibraryUiState(),
                    sessionBookingInput = null,
                    sessionBookingUiModel = null,
                    sessionBookingWorkflowPhase =
                        bookingManager.sessionBookingWorkflowPhase.value,
                ),
            )

        return mergedPresentationFlow
    }

    fun gymLocationsSnapshot(): ImmutableList<GymLocation> {
        check(::gymLocationsStateFlow.isInitialized) {
            "observe(scope) must be called before gym location snapshots"
        }
        return gymLocationsStateFlow.value
    }

    private fun buildBookingUi(
        filtersWithMeasurement: ExerciseLibraryUiState,
        sessionBookingInput: SessionBookingInput,
        gymLocations: ImmutableList<GymLocation>,
    ): SessionBookingUiModel =
        exerciseLibraryUiMapper.mapBookingPresentation(
            filtersWithMeasurement = filtersWithMeasurement,
            sessionBookingInput = sessionBookingInput,
            gymLocations = gymLocations,
            isBookingConfirmEnabled = computeCanConfirm(
                filtersWithMeasurement = filtersWithMeasurement,
                sessionBookingInput = sessionBookingInput,
            ),
        )

    private fun computeCanConfirm(
        filtersWithMeasurement: ExerciseLibraryUiState,
        sessionBookingInput: SessionBookingInput,
    ): Boolean =
        canConfirmLibrarySessionBookingUseCase(
            selectedSlotStarts = sessionBookingInput.selectedSlotStarts,
            selectedLocationId = sessionBookingInput.selectedLocationId,
            selectedDateMillis = sessionBookingInput.selectedDateMillis,
            cart = filtersWithMeasurement.toLibraryCartDraft(),
            exerciseMeasurementById = filtersWithMeasurement.libraryList.exerciseMeasurementById,
            isConfirming = sessionBookingInput.isConfirming,
        )
}
