package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.InstantInterval
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryCartDraft
import com.hoabui.virtualbody3d.domain.model.exercise.PendingSessionBooking
import com.hoabui.virtualbody3d.domain.model.exercise.SessionExerciseLine
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import com.hoabui.virtualbody3d.domain.model.exercise.buildSessionExerciseLinesFromLibraryCart
import com.hoabui.virtualbody3d.domain.model.exercise.proposedVariableSessionInterval
import com.hoabui.virtualbody3d.domain.model.exercise.shouldWarnLongSession
import com.hoabui.virtualbody3d.domain.repository.BookWorkoutSessionResult
import kotlinx.collections.immutable.ImmutableList
import java.time.Instant
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

sealed interface PrepareLibrarySessionConfirmResult {
    data object NoOp : PrepareLibrarySessionConfirmResult
    data object LongSessionAcknowledgementRequired : PrepareLibrarySessionConfirmResult
    data class Ready(
        val session: WorkoutSession,
        val lines: List<SessionExerciseLine>,
        val scheduledDateMillis: Long,
        val primaryExerciseTitle: String?,
        val locationDisplayName: String,
    ) : PrepareLibrarySessionConfirmResult
}

sealed interface CommitLibrarySessionBookingResult {
    data object Conflict : CommitLibrarySessionBookingResult
    data object InvalidDraft : CommitLibrarySessionBookingResult
    data class Success(
        val scheduledCount: Int,
        val session: WorkoutSession,
        val scheduledDateMillis: Long,
        val primaryExerciseTitle: String?,
        val locationDisplayName: String,
        val incrementFabBadgeBy: Int,
    ) : CommitLibrarySessionBookingResult
}

/**
 * Validates library cart + slot selection, then (from the ViewModel after UI sets [isConfirming])
 * persists via [BookWorkoutSessionUseCase].
 */
class ConfirmExerciseLibrarySessionUseCase @Inject constructor(
    private val bookWorkoutSessionUseCase: BookWorkoutSessionUseCase,
    private val validateSessionBookingUseCase: ValidateSessionBookingUseCase,
) {

    fun prepare(
        pending: PendingSessionBooking,
        cart: LibraryCartDraft,
        exerciseMeasurementById: Map<String, ExerciseMeasurementMode>,
        exerciseSnapshotTitlesById: Map<String, String>,
        busyIntervals: ImmutableList<InstantInterval>,
        exercisesById: Map<String, Exercise>,
        zoneId: ZoneId,
        locationDisplayName: String,
    ): PrepareLibrarySessionConfirmResult {
        if (!validateSessionBookingUseCase.canEnableConfirm(
                selectedSlotStarts = pending.selectedSlotStarts.toSet(),
                selectedLocationId = pending.selectedLocationId,
                selectedDateMillis = pending.selectedDateMillis,
                cart = cart,
                exerciseMeasurementById = exerciseMeasurementById,
                busyIntervals = busyIntervals,
                zoneId = zoneId,
                isConfirming = pending.isConfirming,
            )
        ) {
            return PrepareLibrarySessionConfirmResult.NoOp
        }
        if (shouldWarnLongSession(pending.selectedSlotStarts.size) && !pending.longSessionAcknowledged) {
            return PrepareLibrarySessionConfirmResult.LongSessionAcknowledgementRequired
        }

        val date = Instant.ofEpochMilli(pending.selectedDateMillis).atZone(zoneId).toLocalDate()
        val orderedSlots = pending.selectedSlotStarts.sorted()
        val minSlot = orderedSlots.first()
        val maxSlot = orderedSlots.last()
        val proposedInterval = proposedVariableSessionInterval(
            date = date,
            minSlot = minSlot,
            maxSlot = maxSlot,
            zoneId = zoneId,
        )

        val lines = buildSessionExerciseLinesFromLibraryCart(
            cart = cart,
            exercisesById = exercisesById,
        )
        if (lines.isEmpty()) return PrepareLibrarySessionConfirmResult.NoOp

        val session = WorkoutSession(
            id = UUID.randomUUID().toString(),
            startInstant = proposedInterval.start,
            endInstant = proposedInterval.end,
            locationId = pending.selectedLocationId,
        )

        val firstId = cart.draftOrder.firstOrNull()
        val primaryTitle = firstId?.let { id ->
            exerciseSnapshotTitlesById[id]?.takeIf { it.isNotBlank() }
                ?: exercisesById[id]?.name?.takeIf { it.isNotBlank() }
        }

        return PrepareLibrarySessionConfirmResult.Ready(
            session = session,
            lines = lines,
            scheduledDateMillis = pending.selectedDateMillis,
            primaryExerciseTitle = primaryTitle,
            locationDisplayName = locationDisplayName,
        )
    }

    suspend fun commit(
        session: WorkoutSession,
        lines: List<SessionExerciseLine>,
        zoneId: ZoneId,
        scheduledDateMillis: Long,
        primaryExerciseTitle: String?,
        locationDisplayName: String,
    ): CommitLibrarySessionBookingResult =
        when (val result = bookWorkoutSessionUseCase(session, lines, zoneId)) {
            BookWorkoutSessionResult.Conflict -> CommitLibrarySessionBookingResult.Conflict
            BookWorkoutSessionResult.InvalidDraft -> CommitLibrarySessionBookingResult.InvalidDraft
            is BookWorkoutSessionResult.Success -> CommitLibrarySessionBookingResult.Success(
                scheduledCount = result.scheduledCount,
                session = session,
                scheduledDateMillis = scheduledDateMillis,
                primaryExerciseTitle = primaryExerciseTitle,
                locationDisplayName = locationDisplayName,
                incrementFabBadgeBy = result.scheduledCount,
            )
        }
}
