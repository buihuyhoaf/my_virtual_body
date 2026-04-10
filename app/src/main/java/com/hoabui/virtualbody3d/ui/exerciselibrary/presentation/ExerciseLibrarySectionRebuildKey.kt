package com.hoabui.virtualbody3d.ui.exerciselibrary.presentation

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeExerciseLibraryQuery
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryCatalogState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState

@Immutable
internal data class ExerciseLibrarySectionRebuildKey(
    val normalizedQuery: String,
    val category: ExerciseCategory?,
    val equipment: EquipmentType?,
    val cartKeySignature: String,
    /** Stable semantic fingerprint of grouped catalog content (not map identity). */
    val catalogContentSignature: String,
)

internal fun exerciseLibrarySectionRebuildKey(
    catalog: ExerciseLibraryCatalogState,
    filters: ExerciseLibraryUiState,
): ExerciseLibrarySectionRebuildKey {
    val cartSig = buildString {
        filters.cart.draftOrder.forEach { append(it).append(',') }
        append('|')
        filters.cart.itemDrafts.forEach { (k, d) ->
            append(k).append('=').append(d.sets).append('/').append(d.reps).append(';')
        }
        append('|')
        append(filters.cart.activeExerciseId ?: "")
    }
    return ExerciseLibrarySectionRebuildKey(
        normalizedQuery = normalizeExerciseLibraryQuery(filters.filters.searchQuery),
        category = filters.filters.selectedExerciseCategory,
        equipment = filters.filters.selectedEquipment,
        cartKeySignature = cartSig,
        catalogContentSignature = catalogGroupedContentSignature(catalog),
    )
}

private fun catalogGroupedContentSignature(catalog: ExerciseLibraryCatalogState): String = buildString {
    for (region in catalog.catalogGroupedByRegion.keys.sortedBy { it.name }) {
        val ids = catalog.catalogGroupedByRegion[region].orEmpty().map { it.id }.sorted()
        append(region.name).append(':').append(ids.joinToString(",")).append('|')
    }
}
