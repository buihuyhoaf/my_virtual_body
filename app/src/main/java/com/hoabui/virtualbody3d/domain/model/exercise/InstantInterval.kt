package com.hoabui.virtualbody3d.domain.model.exercise

import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Half-open interval [start, end) on the UTC timeline for session overlap checks.
 */
data class InstantInterval(
    val start: Instant,
    val end: Instant,
) {
    init {
        require(start < end) { "Interval start must be strictly before end" }
    }

    fun overlaps(other: InstantInterval): Boolean =
        start < other.end && other.start < end

    /** True if this interval intersects [otherStart, otherEnd) (half-open). */
    fun intersects(otherStart: Instant, otherEnd: Instant): Boolean =
        start < otherEnd && otherStart < end
}

fun instantIntervalFromStart(start: Instant, durationMinutes: Long): InstantInterval =
    InstantInterval(start, start.plus(Duration.ofMinutes(durationMinutes)))

/**
 * Wall-clock minute span of half-open **[start, end)** (e.g. booked session receipt duration).
 */
fun halfOpenInstantIntervalDurationMinutes(start: Instant, end: Instant): Long {
    require(start < end) { "start must be before end for half-open interval" }
    return ChronoUnit.MINUTES.between(start, end)
}

fun InstantInterval.durationMinutes(): Long =
    halfOpenInstantIntervalDurationMinutes(start, end)
