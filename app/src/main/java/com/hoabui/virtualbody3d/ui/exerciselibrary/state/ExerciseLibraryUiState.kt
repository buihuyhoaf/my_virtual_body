package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeDurationMinutesSeconds
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/** Epoch millis at start of today in the system default zone (default booking date). */
fun defaultExerciseLibraryCartDateMillis(): Long {
    val zone = ZoneId.systemDefault()
    return LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli()
}

fun defaultExerciseLibraryCartTime(): LocalTime {
    val zone = ZoneId.systemDefault()
    return LocalTime.now(zone)
}

/** Shown after cart commit succeeds; cleared when user dismisses the success dialog. */
@Immutable
data class AddExerciseSuccessSummary(
    val sessionStartInstant: Instant,
    val sessionEndInstant: Instant,
    val scheduledDateMillis: Long,
    val exerciseCount: Int,
    /** First exercise in cart order for "Squat and N others"; null/blank → generic count-only line in UI. */
    val primaryExerciseTitle: String?,
    val locationDisplayName: String,
)

/** Per-exercise manual entry while building the library cart ([sets]/[reps] as raw text for empty-friendly UI). */
@Immutable
data class ExerciseDraft(
    val sets: String = "",
    val reps: String = "",
)

/**
 * `true` when every cart draft is valid for booking confirm (strength: positive sets/reps;
 * duration: normalized total seconds > 0).
 */
fun ExerciseLibraryUiState.isCartDraftValidForSessionConfirm(): Boolean {
    if (itemDrafts.isEmpty()) return false
    return itemDrafts.all { (id, draft) ->
        val mode = exerciseMeasurementById[id] ?: ExerciseMeasurementMode.Strength
        when (mode) {
            ExerciseMeasurementMode.Strength -> {
                val sets = draft.sets.trim().toIntOrNull()
                val reps = draft.reps.trim().toIntOrNull()
                sets != null && reps != null && sets > 0 && reps > 0
            }
            ExerciseMeasurementMode.Duration -> {
                val minutes = draft.sets.trim().toIntOrNull() ?: 0
                val seconds = draft.reps.trim().toIntOrNull() ?: 0
                normalizeDurationMinutesSeconds(minutes, seconds) > 0
            }
        }
    }
}

/**
 * UI state for the Exercise Library screen.
 */
@Immutable
data class ExerciseLibraryUiState(
    val searchQuery: String = "",
    val selectedExerciseCategory: ExerciseCategory? = null,
    val selectedEquipment: EquipmentType? = null,
    /**
     * Draft lines keyed by exercise id (cart). Source of truth for membership.
     * Order of items in the bar is [draftOrder], not key iteration order.
     */
    val itemDrafts: ImmutableMap<String, ExerciseDraft> = persistentMapOf(),
    /**
     * Stable left-to-right cart order. Invariant: same multiset of ids as [itemDrafts].keys
     * (each id exactly once). Kept in sync with [itemDrafts] on every cart mutation in the screen ViewModel.
     */
    val draftOrder: ImmutableList<String> = persistentListOf(),
    /** Which cart line is being edited in the console. */
    val activeExerciseId: String? = null,
    val sections: ImmutableList<ExerciseSectionUiItem> = persistentListOf(),
    val selectedExerciseForDetail: Exercise? = null,
    /** [Exercise.id] → measurement mode from the library catalog (for cart validation and console UI). */
    val exerciseMeasurementById: ImmutableMap<String, ExerciseMeasurementMode> = persistentMapOf(),
    /** When non-null, show add-success confirmation (cart already cleared in the same VM update). */
    val addExerciseSuccess: AddExerciseSuccessSummary? = null,
    /**
     * Session cumulative count of exercises successfully scheduled from this screen (increments on confirm).
     * Drives workout-plan FAB badge; not reset when clearing the cart.
     */
    val workoutPlanFabBadgeCount: Int = 0,
    /** Mutable booking fields; null when sheet closed. [sessionBooking] is derived in the ViewModel combine. */
    val sessionBookingInput: SessionBookingInput? = null,
    val sessionBooking: SessionBookingUiModel? = null,
    /** Precomputed: non-empty valid cart for opening the booking sheet (see [canOpenBooking]). */
    val isAddToSessionEnabled: Boolean = false,
)

/**
 * One section in the library: a body region and its exercises.
 */
@Immutable
data class ExerciseSectionUiItem(
    val bodyRegion: BodyRegion,
    val items: ImmutableList<GExerciseCardUiModel>,
)
