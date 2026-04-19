package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLine
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarSessionBlock
import com.hoabui.virtualbody3d.domain.model.calendar.resolveWorkoutCalendarLineImage
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutSessionRepository
import com.hoabui.virtualbody3d.domain.util.CaloriesCalculator
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetWorkoutDetailsUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val exercisesRepository: ExercisesRepository,
) {
    /**
     * Returns workout session blocks grouped by sessionId for the given day.
     * Each block contains the session time range and associated exercises.
     */
    operator fun invoke(day: LocalDate): Flow<List<WorkoutCalendarSessionBlock>> {
        val dayKey = day.toEpochDay()
        return combine(
            workoutScheduleRepository.observeSchedulesInDayRange(dayKey, dayKey),
            workoutSessionRepository.observeWorkoutSessionsInDayRange(dayKey, dayKey),
            exercisesRepository.getAllExercises(),
        ) { schedules, workoutSessions, exercises ->
            val exerciseById = exercises.associateBy { it.id }
            val workoutSessionsById = workoutSessions.associateBy { it.id }

            val lines = schedules.sortedBy { it.scheduledAt }
                .mapNotNull { schedule ->
                    val rowId = schedule.rowId ?: return@mapNotNull null
                    val startInstant = schedule.scheduledAt.atZone(Clock.systemDefaultZone().zone).toInstant()
                    val calories = estimateScheduleCalories(schedule)
                    val catalog = exerciseById[schedule.exerciseId]

                    WorkoutCalendarExerciseLine(
                        rowId = rowId,
                        exerciseId = schedule.exerciseId,
                        exerciseDisplayName = catalog?.name ?: schedule.exerciseId,
                        setBreakdownLabel = buildSetBreakdownLabel(schedule),
                        caloriesLabel = formatCaloriesLabel(calories),
                        caloriesKcal = calories,
                        sets = schedule.sets,
                        reps = schedule.reps,
                        durationSeconds = schedule.durationSeconds,
                        measurementMode = schedule.measurementMode,
                        sessionId = schedule.sessionId,
                        image = resolveWorkoutCalendarLineImage(
                            exerciseLocalImageName = schedule.exerciseLocalImageName,
                            exerciseImageResUrl = schedule.exerciseImageResUrl,
                            catalogExercise = catalog,
                        ),
                        startInstant = startInstant,
                    )
                }

            groupExercisesIntoSessionBlocks(
                lines = lines,
                workoutSessionsById = workoutSessionsById,
            )
        }
    }
}

private fun buildSetBreakdownLabel(schedule: WorkoutSchedule): String {
    return when (schedule.measurementMode) {
        ExerciseMeasurementMode.Strength -> {
            val sets = schedule.sets.coerceAtLeast(0)
            val reps = schedule.reps.coerceAtLeast(0)
            if (sets <= 0 || reps <= 0) {
                ""
            } else {
                val weightKg = schedule.weightKg.coerceAtLeast(0.0)
                if (weightKg > 0.0) "$sets x $reps x ${formatWeight(weightKg)}kg" else "$sets x $reps"
            }
        }

        ExerciseMeasurementMode.Duration -> {
            val durationSeconds = schedule.durationSeconds?.coerceAtLeast(0) ?: 0
            if (durationSeconds <= 0) "" else "$durationSeconds s"
        }
    }
}

private fun estimateScheduleCalories(schedule: WorkoutSchedule): Float {
    val durationMinutes = (schedule.durationSeconds ?: 0) / 60.0
    val totalReps = (schedule.sets.coerceAtLeast(0) * schedule.reps.coerceAtLeast(0))
    return CaloriesCalculator.estimateCalories(
        exerciseId = schedule.exerciseId,
        measurementMode = schedule.measurementMode,
        durationMinutes = durationMinutes,
        totalReps = totalReps,
        averageLoadKg = schedule.weightKg.coerceAtLeast(0.0),
        bodyWeightKg = DEFAULT_BODY_WEIGHT_KG,
        leanBodyMassKg = null,
    )
}

private fun formatCaloriesLabel(kcal: Float): String = "${kcal.roundToInt()} kcal"

private fun formatWeight(weightKg: Double): String {
    val rounded = String.format(Locale.ROOT, "%.1f", weightKg)
    return rounded.removeSuffix(".0")
}

private fun groupExercisesIntoSessionBlocks(
    lines: List<WorkoutCalendarExerciseLine>,
    workoutSessionsById: Map<String, WorkoutSession>,
): List<WorkoutCalendarSessionBlock> {
    if (lines.isEmpty()) return emptyList()

    val locale = Locale.getDefault()
    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
    val systemZone = Clock.systemDefaultZone().zone

    val groupedBySession = lines.groupBy { it.sessionId }
    return groupedBySession.map { (sessionId, exercises) ->
        val sortedExercises = exercises.sortedBy { it.startInstant }
        val fallbackStartInstant = sortedExercises.first().startInstant
        val startInstant = resolveSessionStartInstant(
            sessionId = sessionId,
            workoutSessionsById = workoutSessionsById,
            fallbackStartInstant = fallbackStartInstant,
        )
        val endInstant = resolveSessionEndInstant(
            sessionId = sessionId,
            workoutSessionsById = workoutSessionsById,
            startInstant = startInstant,
            sortedExercises = sortedExercises,
        )
        val startTime = startInstant.atZone(systemZone).toLocalTime().format(timeFormatter)
        val endTime = endInstant.atZone(systemZone).toLocalTime().format(timeFormatter)

        WorkoutCalendarSessionBlock(
            sessionId = sessionId,
            sessionTimeLabel = "$startTime - $endTime",
            startInstant = startInstant,
            endInstant = endInstant,
            exercises = sortedExercises,
            totalCaloriesKcal = sortedExercises.sumOf { it.caloriesKcal.toDouble() }.toFloat(),
        )
    }.sortedBy { it.startInstant }
}

private fun resolveSessionStartInstant(
    sessionId: String?,
    workoutSessionsById: Map<String, WorkoutSession>,
    fallbackStartInstant: Instant,
): Instant {
    if (sessionId.isNullOrBlank()) return fallbackStartInstant
    return workoutSessionsById[sessionId]?.startInstant ?: fallbackStartInstant
}

private fun resolveSessionEndInstant(
    sessionId: String?,
    workoutSessionsById: Map<String, WorkoutSession>,
    startInstant: Instant,
    sortedExercises: List<WorkoutCalendarExerciseLine>,
): Instant {
    if (!sessionId.isNullOrBlank()) {
        workoutSessionsById[sessionId]?.endInstant?.let { return it }
    }
    val lastExercise = sortedExercises.lastOrNull() ?: return startInstant
    val lastDurationSeconds = lastExercise.durationSeconds?.coerceAtLeast(0) ?: 0
    return if (lastDurationSeconds > 0) {
        lastExercise.startInstant.plusSeconds(lastDurationSeconds.toLong())
    } else {
        lastExercise.startInstant
    }
}

private const val DEFAULT_BODY_WEIGHT_KG = 70.0
