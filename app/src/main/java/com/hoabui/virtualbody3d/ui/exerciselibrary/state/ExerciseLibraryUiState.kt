package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate
import java.time.ZoneId

/** Epoch millis at start of today in the system default zone (cart date default). */
fun defaultExerciseLibraryCartDateMillis(): Long {
    val zone = ZoneId.systemDefault()
    return LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli()
}

/**
 * UI state for the Exercise Library screen.
 */
@Immutable
data class ExerciseLibraryUiState(
    val searchQuery: String = "",
    val selectedExerciseCategory: ExerciseCategory? = null,
    val selectedEquipment: EquipmentType? = null,
    val selectedExerciseId: String? = null,
    /** Global sets/reps applied when confirming the cart (defaults match [com.hoabui.virtualbody3d.ui.addworkout.state.AddWorkoutUiState]). */
    val globalSets: Int = 3,
    val globalReps: Int = 10,
    /** Start-of-day millis in [ZoneId.systemDefault] for scheduled workout date. */
    val selectedDate: Long = defaultExerciseLibraryCartDateMillis(),
    val sections: ImmutableList<ExerciseSectionUiItem> = persistentListOf(),
    val selectedExerciseForDetail: Exercise? = null
)

/**
 * One section in the library: a body region and its exercises.
 */
@Immutable
data class ExerciseSectionUiItem(
    val bodyRegion: BodyRegion,
    val items: ImmutableList<GExerciseCardUiModel>
)
