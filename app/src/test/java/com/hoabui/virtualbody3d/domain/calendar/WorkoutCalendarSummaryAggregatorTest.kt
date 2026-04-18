package com.hoabui.virtualbody3d.domain.calendar

import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarDayCellStatus
import com.hoabui.virtualbody3d.domain.model.exercise.DEFAULT_SESSION_LOCATION_ID
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutExecutionStatus
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import java.time.LocalDateTime
import java.time.Clock
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutCalendarSummaryAggregatorTest {

    private val systemZone = Clock.systemDefaultZone().zone

    @Test
    fun toCellStatus_empty_isEmpty() {
        assertEquals(WorkoutCalendarDayCellStatus.Empty, emptyList<WorkoutSchedule>().toCellStatus())
    }

    @Test
    fun toCellStatus_singleScheduled() {
        val rows = listOf(schedule(id = "a", status = WorkoutExecutionStatus.Scheduled, hour = 9))
        assertEquals(WorkoutCalendarDayCellStatus.Scheduled, rows.toCellStatus())
    }

    @Test
    fun toCellStatus_mixedStatuses() {
        val rows = listOf(
            schedule(id = "a", status = WorkoutExecutionStatus.Completed, hour = 9),
            schedule(id = "b", status = WorkoutExecutionStatus.Scheduled, hour = 10),
        )
        assertEquals(WorkoutCalendarDayCellStatus.Mixed, rows.toCellStatus())
    }

    @Test
    fun groupSchedulesToDaySummaries_groupsByLocalDay() {
        val day1Morning = LocalDateTime.of(2025, 4, 5, 8, 0)
        val day1Evening = LocalDateTime.of(2025, 4, 5, 20, 0)
        val day2 = LocalDateTime.of(2025, 4, 6, 12, 0)
        val schedules = listOf(
            scheduleAt("a", day1Morning, WorkoutExecutionStatus.Scheduled),
            scheduleAt("b", day1Evening, WorkoutExecutionStatus.Completed),
            scheduleAt("c", day2, WorkoutExecutionStatus.Missed),
        )
        val summaries = groupSchedulesToDaySummaries(schedules)
        assertEquals(2, summaries.size)
        val key1 = day1Morning.atZone(systemZone).toLocalDate().toEpochDay()
        assertEquals(WorkoutCalendarDayCellStatus.Mixed, summaries[key1]?.cellStatus)
        val key2 = day2.atZone(systemZone).toLocalDate().toEpochDay()
        assertEquals(WorkoutCalendarDayCellStatus.Missed, summaries[key2]?.cellStatus)
    }

    private fun schedule(
        id: String,
        status: WorkoutExecutionStatus,
        hour: Int,
    ): WorkoutSchedule = WorkoutSchedule(
        id = id,
        exerciseId = "ex-$id",
        scheduledAt = LocalDateTime.of(2025, 4, 10, hour, 0),
        sets = 3,
        reps = 10,
        weightKg = 40.0,
        restSeconds = 60,
        notes = null,
        measurementMode = ExerciseMeasurementMode.Strength,
        executionStatus = status,
        locationId = DEFAULT_SESSION_LOCATION_ID,
    )

    private fun scheduleAt(
        id: String,
        at: LocalDateTime,
        status: WorkoutExecutionStatus,
    ): WorkoutSchedule = WorkoutSchedule(
        id = id,
        exerciseId = "ex-$id",
        scheduledAt = at,
        sets = 3,
        reps = 10,
        weightKg = 40.0,
        restSeconds = 60,
        notes = null,
        measurementMode = ExerciseMeasurementMode.Strength,
        executionStatus = status,
        locationId = DEFAULT_SESSION_LOCATION_ID,
    )
}
