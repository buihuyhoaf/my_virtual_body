package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryCatalogEntryUiModel

/**
 * Same matching rules as [com.hoabui.virtualbody3d.domain.model.exercise.Exercise.matchesLibrarySearch],
 * using catalog entry fields only (no domain [Exercise]).
 */
fun ExerciseLibraryCatalogEntryUiModel.matchesLibrarySearch(normalizedQuery: String): Boolean {
    if (normalizedQuery.isEmpty()) return true
    if (name.lowercase().contains(normalizedQuery)) return true
    if (bodyRegion.name.lowercase().contains(normalizedQuery)) return true
    val equipmentName = equipment?.name?.lowercase() ?: return false
    return equipmentName.contains(normalizedQuery)
}
