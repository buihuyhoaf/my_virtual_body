package com.hoabui.virtualbody3d.domain.model.exercise

/**
 * Normalizes user query for library search (trim + lowercase).
 */
fun normalizeExerciseLibraryQuery(raw: String): String = raw.trim().lowercase()

/**
 * Whether [this] exercise matches [normalizedQuery] by name, primary muscle enum names,
 * or equipment enum name. Empty [normalizedQuery] matches all.
 */
fun Exercise.matchesLibrarySearch(normalizedQuery: String): Boolean {
    if (normalizedQuery.isEmpty()) return true
    if (name.lowercase().contains(normalizedQuery)) return true
    if (primaryMuscles.any { it.name.lowercase().contains(normalizedQuery) }) return true
    val equipmentName = equipment?.name?.lowercase() ?: return false
    return equipmentName.contains(normalizedQuery)
}
