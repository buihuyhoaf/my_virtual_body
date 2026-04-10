package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.WorkoutSessionDto
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Find-or-merge identity uses [WorkoutSessionEntity.dayKey] + epoch millis + [locationId].
 * Ensures [WorkoutSessionDto.toEntity] dayKey matches the plan zone’s calendar day of session start.
 */
class WorkoutSessionIdentityMappingTest {

    @Test
    fun toEntity_dayKey_matchesLocalStartDate_inPlanZone() {
        val zone = ZoneId.of("America/Los_Angeles")
        val start = ZonedDateTime.of(2026, 7, 15, 10, 0, 0, 0, zone).toInstant()
        val end = ZonedDateTime.of(2026, 7, 15, 12, 0, 0, 0, zone).toInstant()
        val dto = WorkoutSessionDto(
            id = "s1",
            startEpochMillis = start.toEpochMilli(),
            endEpochMillis = end.toEpochMilli(),
            locationId = "gym-a",
        )
        val entity = dto.toEntity(zone)
        val expectedDayKey = start.atZone(zone).toLocalDate().toEpochDay()
        assertEquals(expectedDayKey, entity.dayKey)
        assertEquals(start.toEpochMilli(), entity.startEpochMillis)
        assertEquals(end.toEpochMilli(), entity.endEpochMillis)
        assertEquals("gym-a", entity.locationId)
    }

    @Test
    fun toDomain_roundTrip_preservesInstants() {
        val zone = ZoneId.of("UTC")
        val session = WorkoutSession(
            id = "id1",
            startInstant = ZonedDateTime.of(2026, 1, 10, 9, 0, 0, 0, zone).toInstant(),
            endInstant = ZonedDateTime.of(2026, 1, 10, 11, 0, 0, 0, zone).toInstant(),
            locationId = "loc",
        )
        val back = session.toDto().toEntity(zone).toDomain()
        assertEquals(session, back)
    }
}
