package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.InstantInterval
import com.hoabui.virtualbody3d.domain.model.exercise.isContiguousThirtyMinuteChain
import com.hoabui.virtualbody3d.domain.model.exercise.isIntervalFreeForBooking
import com.hoabui.virtualbody3d.domain.model.exercise.proposedVariableSessionInterval
import com.hoabui.virtualbody3d.domain.model.exercise.shouldWarnLongSession
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

/**
 * Result of validating whether the user can proceed from "Confirm" to persisting a session.
 * [BookWorkoutSessionUseCase] should only run when this returns [ReadyToPersist].
 */
sealed class EvaluateBookingConfirmResult {
    data class ReadyToPersist(val proposedInterval: InstantInterval) : EvaluateBookingConfirmResult()

    /** Valid selection and cart, but UI must collect long-session acknowledgment first. */
    data object NeedsLongSessionAcknowledgment : EvaluateBookingConfirmResult()

    data object NotReady : EvaluateBookingConfirmResult()
}

/**
 * Encapsulates booking validation for confirm (contiguous slots, busy conflict, long-session gate).
 */
class EvaluateBookingConfirmUseCase @Inject constructor() {

    operator fun invoke(
        selectedDateMillis: Long,
        selectedSlotStarts: Set<LocalTime>,
        busyIntervals: List<InstantInterval>,
        zoneId: ZoneId,
        isCartDraftValid: Boolean,
        isConfirming: Boolean,
        longSessionAcknowledged: Boolean,
    ): EvaluateBookingConfirmResult {
        if (isConfirming || !isCartDraftValid) return EvaluateBookingConfirmResult.NotReady
        if (selectedSlotStarts.isEmpty()) return EvaluateBookingConfirmResult.NotReady
        val date = Instant.ofEpochMilli(selectedDateMillis).atZone(zoneId).toLocalDate()
        val orderedSlots = selectedSlotStarts.sorted()
        if (!isContiguousThirtyMinuteChain(orderedSlots)) return EvaluateBookingConfirmResult.NotReady
        val minSlot = orderedSlots.first()
        val maxSlot = orderedSlots.last()
        val proposedInterval = proposedVariableSessionInterval(
            date = date,
            minSlot = minSlot,
            maxSlot = maxSlot,
            zoneId = zoneId,
        )
        if (!isIntervalFreeForBooking(proposedInterval, busyIntervals)) {
            return EvaluateBookingConfirmResult.NotReady
        }
        if (shouldWarnLongSession(selectedSlotStarts.size) && !longSessionAcknowledged) {
            return EvaluateBookingConfirmResult.NeedsLongSessionAcknowledgment
        }
        return EvaluateBookingConfirmResult.ReadyToPersist(proposedInterval)
    }
}
