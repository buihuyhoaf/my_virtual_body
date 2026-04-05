package com.hoabui.virtualbody3d.domain.calendar

import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarDayCellStatus
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarDaySummary
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutExecutionStatus
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import java.time.ZoneId

fun groupSchedulesToDaySummaries(
    schedules: List<WorkoutSchedule>,
    zoneId: ZoneId,
): Map<Long, WorkoutCalendarDaySummary> {
    if (schedules.isEmpty()) return emptyMap()
    val byDay = schedules.groupBy { it.scheduledAt.atZone(zoneId).toLocalDate().toEpochDay() }
    return byDay.mapValues { (epochDay, rows) ->
        WorkoutCalendarDaySummary(
            epochDay = epochDay,
            cellStatus = rows.toCellStatus(),
        )
    }
}

fun List<WorkoutSchedule>.toCellStatus(): WorkoutCalendarDayCellStatus {
    if (isEmpty()) return WorkoutCalendarDayCellStatus.Empty
    val distinct = map { it.executionStatus }.distinct()
    if (distinct.size == 1) {
        return when (distinct.single()) {
            WorkoutExecutionStatus.Scheduled -> WorkoutCalendarDayCellStatus.Scheduled
            WorkoutExecutionStatus.Completed -> WorkoutCalendarDayCellStatus.Completed
            WorkoutExecutionStatus.Missed -> WorkoutCalendarDayCellStatus.Missed
            WorkoutExecutionStatus.Skipped -> WorkoutCalendarDayCellStatus.Missed
        }
    }
    return WorkoutCalendarDayCellStatus.Mixed
}
