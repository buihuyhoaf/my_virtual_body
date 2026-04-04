package com.hoabui.virtualbody3d.domain.model.exercise

/**
 * Pure parsing for booking snapshot lines — mirrors cart draft semantics used with
 * [normalizeDurationMinutesSeconds] and strength validation (> 0 sets/reps).
 */
fun parseCartStrengthSetsRepsForSummary(setsRaw: String, repsRaw: String): Pair<Int, Int>? {
    val sets = setsRaw.trim().toIntOrNull() ?: return null
    val reps = repsRaw.trim().toIntOrNull() ?: return null
    if (sets <= 0 || reps <= 0) return null
    return sets to reps
}

/** Total seconds for duration cart line using [normalizeDurationMinutesSeconds], or null if zero/invalid. */
fun parseCartDurationTotalSecondsForSummary(minutesRaw: String, secondsRaw: String): Int? {
    val minutes = minutesRaw.trim().toIntOrNull() ?: 0
    val seconds = secondsRaw.trim().toIntOrNull() ?: 0
    val total = normalizeDurationMinutesSeconds(minutes, seconds)
    return if (total > 0) total else null
}
