package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryCartDraft
import com.hoabui.virtualbody3d.domain.model.exercise.PendingSessionBooking
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Input bundle for a single booking confirmation attempt (prepare + optional commit).
 */
data class SessionBookingWorkflowInput(
    val pending: PendingSessionBooking,
    val cart: LibraryCartDraft,
    val exerciseMeasurementById: Map<String, ExerciseMeasurementMode>,
    val exerciseSnapshotTitlesById: Map<String, String>,
    val exercisesById: Map<String, Exercise>,
    val locationDisplayName: String,
)

/**
 * Ordered statuses for observability and ViewModel effect handling.
 * [PendingCommit] is emitted before [Committing] so the UI can set [isConfirming] synchronously.
 */
sealed interface BookingConfirmationStatus {
    data object Preparing : BookingConfirmationStatus
    data object AwaitingLongSessionAck : BookingConfirmationStatus
    data class PendingCommit(val ready: PrepareLibrarySessionConfirmResult.Ready) : BookingConfirmationStatus
    data object Committing : BookingConfirmationStatus
    data class Completed(val result: CommitLibrarySessionBookingResult) : BookingConfirmationStatus
    data object NoOp : BookingConfirmationStatus
}

/**
 * Encapsulates prepare → optional commit orchestration for the exercise library booking sheet.
 */
class SessionBookingConfirmationWorkflow @Inject constructor(
    private val confirmExerciseLibrarySessionUseCase: ConfirmExerciseLibrarySessionUseCase,
) {

    fun run(input: SessionBookingWorkflowInput): Flow<BookingConfirmationStatus> = flow {
        emit(BookingConfirmationStatus.Preparing)
        when (
            val prep = confirmExerciseLibrarySessionUseCase.prepare(
                pending = input.pending,
                cart = input.cart,
                exerciseMeasurementById = input.exerciseMeasurementById,
                exerciseSnapshotTitlesById = input.exerciseSnapshotTitlesById,
                exercisesById = input.exercisesById,
                locationDisplayName = input.locationDisplayName,
            )
        ) {
            PrepareLibrarySessionConfirmResult.NoOp -> {
                emit(BookingConfirmationStatus.NoOp)
                return@flow
            }
            PrepareLibrarySessionConfirmResult.LongSessionAcknowledgementRequired -> {
                emit(BookingConfirmationStatus.AwaitingLongSessionAck)
                return@flow
            }
            is PrepareLibrarySessionConfirmResult.Ready -> {
                emit(BookingConfirmationStatus.PendingCommit(prep))
                emit(BookingConfirmationStatus.Committing)
                val result = confirmExerciseLibrarySessionUseCase.commit(
                    session = prep.session,
                    lines = prep.lines,
                    scheduledDateMillis = prep.scheduledDateMillis,
                    primaryExerciseTitle = prep.primaryExerciseTitle,
                    locationDisplayName = prep.locationDisplayName,
                )
                emit(BookingConfirmationStatus.Completed(result))
            }
        }
    }
}
