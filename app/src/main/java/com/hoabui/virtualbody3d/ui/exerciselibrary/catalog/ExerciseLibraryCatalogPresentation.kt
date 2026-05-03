package com.hoabui.virtualbody3d.ui.exerciselibrary.catalog

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class ExerciseLibrarySectionRowUiModel(
    val bodyRegion: BodyRegion,
    val items: ImmutableList<GExerciseCardUiModel>,
)

typealias ExerciseLibraryCatalogGrouped =
    PersistentMap<BodyRegion, ImmutableList<ExerciseLibraryCatalogEntryUiModel>>

@Immutable
data class LibraryPresentationSlice(
    val sections: ImmutableList<ExerciseLibrarySectionRowUiModel> = persistentListOf(),
    val exerciseMeasurementById: ImmutableMap<String, ExerciseMeasurementMode> = persistentMapOf(),
    val isAddToSessionEnabled: Boolean = false,
)
