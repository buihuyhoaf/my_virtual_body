package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.ExerciseLibraryCatalogGrouped
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.LibraryPresentationSlice
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/**
 * UI state for the Exercise Library screen (flat fields; merged list projection applied in ViewModel).
 */
@Immutable
data class ExerciseLibraryUiState(
    val searchQuery: String = "",
    val selectedExerciseCategory: ExerciseCategory? = null,
    val selectedBodyRegions: ImmutableSet<BodyRegion>? = null,
    val selectedEquipment: EquipmentType? = null,
    val itemDrafts: ImmutableMap<String, ExerciseDraft> = persistentMapOf(),
    val draftOrder: ImmutableList<String> = persistentListOf(),
    val activeExerciseId: String? = null,
    val isCartExpanded: Boolean = false,
    val catalogGroupedByRegion: ExerciseLibraryCatalogGrouped = persistentMapOf(),
    val catalogExercisesById: Map<String, Exercise> = emptyMap(),
    val libraryList: LibraryPresentationSlice = LibraryPresentationSlice(),
)
