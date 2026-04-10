package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.computeNextSlotSelectionAfterToggle
import java.time.LocalTime
import javax.inject.Inject

/**
 * Thin domain entry point for slot grid toggles (testable, keeps ViewModel coordinating-only).
 */
class ResolveNextSlotSelectionAfterToggleUseCase @Inject constructor() {

    operator fun invoke(
        current: Set<LocalTime>,
        tapped: LocalTime,
        gridSlotStarts: List<LocalTime>,
    ): Set<LocalTime>? = computeNextSlotSelectionAfterToggle(
        current = current,
        tapped = tapped,
        gridSlotStarts = gridSlotStarts,
    )
}
