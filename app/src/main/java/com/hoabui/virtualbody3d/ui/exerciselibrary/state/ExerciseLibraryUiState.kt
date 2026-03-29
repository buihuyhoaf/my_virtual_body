package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/** Epoch millis at start of today in the system default zone (picker default when schedule is unset). */
fun defaultExerciseLibraryCartDateMillis(): Long {
    val zone = ZoneId.systemDefault()
    return LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli()
}

fun defaultExerciseLibraryCartTime(): LocalTime {
    val zone = ZoneId.systemDefault()
    return LocalTime.now(zone)
}

/** Per-exercise manual entry while building the library cart ([sets]/[reps] as raw text for empty-friendly UI). */
@Immutable
data class ExerciseDraft(
    val sets: String = "",
    val reps: String = "",
)

/**
 * `true` when the primary "Thêm" action is allowed: date and time chosen, cart non-empty,
 * and every draft has sets/reps that parse as positive integers.
 */
fun ExerciseLibraryUiState.isAnchoredAddEnabled(): Boolean {
    if (selectedDate == null || selectedTime == null) return false
    if (itemDrafts.isEmpty()) return false
    return itemDrafts.values.all { draft ->
        val sets = draft.sets.trim().toIntOrNull()
        val reps = draft.reps.trim().toIntOrNull()
        sets != null && reps != null && sets > 0 && reps > 0
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
    /** Draft lines keyed by exercise id (cart). Order follows insertion. */
    val itemDrafts: ImmutableMap<String, ExerciseDraft> = persistentMapOf(),
    /** Which cart line is being edited in the console. */
    val activeExerciseId: String? = null,
    /** Start-of-day millis in [ZoneId.systemDefault] when chosen; `null` until user picks a date. */
    val selectedDate: Long? = null,
    /** Clock time when chosen; `null` until user picks a time. */
    val selectedTime: LocalTime? = null,
    val sections: ImmutableList<ExerciseSectionUiItem> = persistentListOf(),
    val selectedExerciseForDetail: Exercise? = null,
)

/**
 * One section in the library: a body region and its exercises.
 */
@Immutable
data class ExerciseSectionUiItem(
    val bodyRegion: BodyRegion,
    val items: ImmutableList<GExerciseCardUiModel>,
)
