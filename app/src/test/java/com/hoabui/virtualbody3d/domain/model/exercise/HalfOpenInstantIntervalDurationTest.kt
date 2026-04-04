package com.hoabui.virtualbody3d.domain.model.exercise

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class HalfOpenInstantIntervalDurationTest {

    @Test
    fun duration_threeContiguousThirtyMinuteSlots_is90Minutes() {
        val start = Instant.parse("2026-04-01T10:00:00Z")
        val end = start.plusSeconds(90L * 60L)
        assertEquals(90L, halfOpenInstantIntervalDurationMinutes(start, end))
    }

    @Test
    fun duration_instantInterval_extension_matchesHelper() {
        val interval = InstantInterval(
            start = Instant.parse("2026-06-15T08:30:00Z"),
            end = Instant.parse("2026-06-15T09:00:00Z"),
        )
        assertEquals(30L, interval.durationMinutes())
    }
}
