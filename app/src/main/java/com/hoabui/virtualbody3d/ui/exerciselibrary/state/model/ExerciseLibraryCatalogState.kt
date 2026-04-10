package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

/** Exercise catalog from the library repository; updated by the CatalogLoaded MVI update. */
@Immutable
data class ExerciseLibraryCatalogState(
    val catalogGroupedByRegion: PersistentMap<BodyRegion, ImmutableList<ExerciseLibraryCatalogEntryUiModel>> =
        persistentMapOf(),
)
