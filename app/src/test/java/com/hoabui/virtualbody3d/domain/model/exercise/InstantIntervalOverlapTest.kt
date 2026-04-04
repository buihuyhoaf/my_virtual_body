package com.hoabui.virtualbody3d.domain.model.exercise

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class InstantIntervalOverlapTest {

    @Test
    fun overlaps_touchingAtEnd_noOverlap_halfOpen() {
        val a = InstantInterval(
            start = Instant.parse("2026-04-01T10:00:00Z"),
            end = Instant.parse("2026-04-01T11:00:00Z"),
        )
        val b = InstantInterval(
            start = Instant.parse("2026-04-01T11:00:00Z"),
            end = Instant.parse("2026-04-01T12:00:00Z"),
        )
        assertFalse(a.overlaps(b))
        assertFalse(b.overlaps(a))
    }

    @Test
    fun overlaps_crossMidnight_overlapsWhenRangesIntersect() {
        val a = InstantInterval(
            start = Instant.parse("2026-04-01T23:30:00Z"),
            end = Instant.parse("2026-04-02T00:30:00Z"),
        )
        val b = InstantInterval(
            start = Instant.parse("2026-04-02T00:00:00Z"),
            end = Instant.parse("2026-04-02T01:00:00Z"),
        )
        assertTrue(a.overlaps(b))
    }

    @Test
    fun intersects_matchesOverlapsForInstantBounds() {
        val a = InstantInterval(
            start = Instant.parse("2026-04-01T10:00:00Z"),
            end = Instant.parse("2026-04-01T10:30:00Z"),
        )
        assertTrue(a.intersects(Instant.parse("2026-04-01T10:15:00Z"), Instant.parse("2026-04-01T10:45:00Z")))
        assertFalse(a.intersects(Instant.parse("2026-04-01T10:30:00Z"), Instant.parse("2026-04-01T11:00:00Z")))
    }
}
