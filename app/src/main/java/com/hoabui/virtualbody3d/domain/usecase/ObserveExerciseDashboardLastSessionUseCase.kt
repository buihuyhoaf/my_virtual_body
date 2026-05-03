package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.calendar.buildWorkoutCalendarExerciseLines
import com.hoabui.virtualbody3d.domain.calendar.groupLinesIntoWorkoutSessionBlocks
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.model.exercise.dashboard.ExerciseDashboardLastSessionRecap
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutSessionRepository
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.math.max
import kotlin.math.roundToInt

class ObserveExerciseDashboardLastSessionUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val exercisesRepository: ExercisesRepository,
) {
    operator fun invoke(): Flow<ExerciseDashboardLastSessionRecap?> =
        combine(
            workoutSessionRepository.observeWorkoutSessions(),
            workoutScheduleRepository.observeWorkoutSchedules(),
            exercisesRepository.getAllExercises(),
        ) { sessions, schedules, exercises ->
            computeRecap(Clock.systemDefaultZone(), sessions, schedules, exercises)
        }
}

private fun computeRecap(
    clock: Clock,
    sessionsList: List<WorkoutSession>,
    schedulesList: List<WorkoutSchedule>,
    exercisesList: List<Exercise>,
): ExerciseDashboardLastSessionRecap? {
    val latestSession = sessionsList.maxByOrNull { it.endInstant } ?: return null
    val exerciseById = exercisesList.associateBy { it.id }
    val workoutsById = sessionsList.associateBy { it.id }

    val sessionSchedules = schedulesList.filter {
        val sid = it.sessionId
        sid != null && sid == latestSession.id && it.rowId != null
    }
    val zone = clock.zone

    val lines = buildWorkoutCalendarExerciseLines(sessionSchedules, exerciseById)
    if (lines.isEmpty()) {
        val mins = durationMinutesInclusive(latestSession.startInstant, latestSession.endInstant)
        return ExerciseDashboardLastSessionRecap(
            anchorDate = latestSession.startInstant.atZone(zone).toLocalDate(),
            exerciseTitlesJoined = "",
            durationMinutes = max(mins, 0),
            totalKcalRounded = 0,
        )
    }

    val blocks = groupLinesIntoWorkoutSessionBlocks(lines, workoutsById)
    val block = blocks.firstOrNull { it.sessionId == latestSession.id }
        ?: blocks.maxByOrNull { it.endInstant }
        ?: return null

    val titlesDistinct = block.exercises
        .map { it.exerciseDisplayName }
        .distinct()
        .joinToString(separator = " · ")

    return ExerciseDashboardLastSessionRecap(
        anchorDate = block.startInstant.atZone(zone).toLocalDate(),
        exerciseTitlesJoined = titlesDistinct,
        durationMinutes = durationMinutesInclusive(block.startInstant, block.endInstant),
        totalKcalRounded = block.totalCaloriesKcal.roundToInt().coerceAtLeast(0),
    )
}

private fun durationMinutesInclusive(start: Instant, end: Instant): Int =
    ChronoUnit.MINUTES.between(start, end.coerceAtLeast(start)).coerceAtLeast(0L).toInt()

private fun Instant.coerceAtLeast(other: Instant): Instant =
    if (this < other) other else this
