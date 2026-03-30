package com.hoabui.virtualbody3d.domain.model.exercise

/**
 * Converts minutes + seconds into a single non-negative duration in seconds,
 * rolling seconds ≥ 60 into minutes. Negative minutes are treated as zero; negative seconds are treated as zero.
 */
fun normalizeDurationMinutesSeconds(minutes: Int, seconds: Int): Int {
    var m = minutes.coerceAtLeast(0)
    var s = seconds.coerceAtLeast(0)
    m += s / 60
    s %= 60
    return m * 60 + s
}
