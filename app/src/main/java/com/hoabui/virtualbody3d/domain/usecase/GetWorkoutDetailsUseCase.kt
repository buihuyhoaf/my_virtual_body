package com.hoabui.virtualbody3d.domain.usecase

import android.util.Log
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLine
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarSessionBlock
import com.hoabui.virtualbody3d.domain.model.calendar.resolveWorkoutCalendarLineImage
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import com.hoabui.virtualbody3d.domain.model.workoutlog.WorkoutLogExerciseDetail
import com.hoabui.virtualbody3d.domain.model.workoutlog.WorkoutLogSetDetail
import com.hoabui.virtualbody3d.domain.model.workoutlog.WorkoutLogSessionDetail
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutLogRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutSessionRepository
import com.hoabui.virtualbody3d.domain.util.CaloriesCalculator
import com.hoabui.virtualbody3d.domain.util.toIsoDayKey
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
    private val workoutLogRepository: WorkoutLogRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val exercisesRepository: ExercisesRepository,
) {
    /**
     * Returns workout session blocks grouped by sessionId for the given day.
     * Each block contains the session time range and associated exercises.
     */
    operator fun invoke(day: LocalDate): Flow<List<WorkoutCalendarSessionBlock>> {
        val dayKey = day.toEpochDay()
        val logDayKey = day.toIsoDayKey()
        return combine(
            workoutScheduleRepository.observeSchedulesInDayRange(dayKey, dayKey),
            workoutSessionRepository.observeWorkoutSessionsInDayRange(dayKey, dayKey),
            workoutLogRepository.observeWorkoutLogsByDay(logDayKey),
            exercisesRepository.getAllExercises(),
        ) { schedules, workoutSessions, logSessions, exercises ->
            val exerciseById = exercises.associateBy { it.id }
            val workoutSessionsById = workoutSessions.associateBy { it.id }
            val logSessionsById = logSessions.associateBy { it.id }
            val allLogExercises = logSessions.flatMap { it.exercises }.sortedBy { it.startInstant }
            val logBySessionExercise = allLogExercises
                .groupBy { log -> LogKey(log.sessionId, log.exerciseId) }
                .mapValues { (_, list) -> list.toMutableList() }
                .toMutableMap()
            val logByExercise = allLogExercises
                .groupBy { it.exerciseId }
                .mapValues { (_, list) -> list.toMutableList() }
                .toMutableMap()

            // Map schedules to exercise lines
            val lines = schedules.sortedBy { it.scheduledAt }
                .mapNotNull { schedule ->
                    val rowId = schedule.rowId ?: return@mapNotNull null
                    val logEntry = consumeLogEntry(
                        schedule = schedule,
                        logBySessionExercise = logBySessionExercise,
                        logByExercise = logByExercise,
                    )
                    val (startInstant, setBreakdownLabel, caloriesLabel, caloriesKcal) =
                        resolveWorkoutLineMetrics(schedule, logEntry)
                    val catalog = exerciseById[schedule.exerciseId]

                    // Debug logging for sessionId/exerciseId matching
                    Log.d(
                        TAG,
                        "Schedule [rowId=$rowId, exerciseId=${schedule.exerciseId}, " +
                            "sessionId=${schedule.sessionId}] -> " +
                            "Log [matched=${logEntry != null}, logSessionId=${logEntry?.sessionId}]",
                    )

                    WorkoutCalendarExerciseLine(
                        rowId = rowId,
                        exerciseId = schedule.exerciseId,
                        exerciseDisplayName = logEntry?.displayNameSnapshot ?: catalog?.name ?: schedule.exerciseId,
                        setBreakdownLabel = setBreakdownLabel,
                        caloriesLabel = caloriesLabel,
                        caloriesKcal = caloriesKcal,
                        sets = logEntry?.sets?.size ?: schedule.sets,
                        reps = logEntry?.sets?.firstOrNull()?.reps ?: schedule.reps,
                        durationSeconds = logEntry?.sets?.sumOf { it.durationSeconds ?: 0 }?.takeIf { it > 0 }
                            ?: schedule.durationSeconds,
                        measurementMode = logEntry?.measurementMode ?: schedule.measurementMode,
                        sessionId = logEntry?.sessionId ?: schedule.sessionId,
                        image = resolveWorkoutCalendarLineImage(
                            exerciseLocalImageName = schedule.exerciseLocalImageName,
                            exerciseImageResUrl = schedule.exerciseImageResUrl,
                            catalogExercise = catalog,
                        ),
                        startInstant = startInstant,
                    )
                }

            // Group exercise lines by sessionId into session blocks
            groupExercisesIntoSessionBlocks(
                lines = lines,
                workoutSessionsById = workoutSessionsById,
                logSessionsById = logSessionsById,
            )
        }
    }
}

private fun consumeLogEntry(
    schedule: WorkoutSchedule,
    logBySessionExercise: MutableMap<LogKey, MutableList<WorkoutLogExerciseDetail>>,
    logByExercise: MutableMap<String, MutableList<WorkoutLogExerciseDetail>>,
): WorkoutLogExerciseDetail? {
    val strictKey = schedule.sessionId?.let { LogKey(it, schedule.exerciseId) }
    if (strictKey != null) {
        val strictBucket = logBySessionExercise[strictKey]
        val strictHit = if (!strictBucket.isNullOrEmpty()) strictBucket.removeAt(0) else null
        if (strictHit != null) {
            logByExercise[schedule.exerciseId]?.removeAll { it.id == strictHit.id }
            Log.d(TAG, "consumeLogEntry: Strict hit key=$strictKey -> entry.id=${strictHit.id}")
            return strictHit
        }
    }

    val fallbackBucket = logByExercise[schedule.exerciseId]
    val fallbackHit = if (!fallbackBucket.isNullOrEmpty()) fallbackBucket.removeAt(0) else null
    if (fallbackHit != null) {
        val fallbackKey = LogKey(fallbackHit.sessionId, fallbackHit.exerciseId)
        logBySessionExercise[fallbackKey]?.removeAll { it.id == fallbackHit.id }
        Log.d(
            TAG,
            "consumeLogEntry: Fallback hit exerciseId=${schedule.exerciseId} -> entry.id=${fallbackHit.id}",
        )
        return fallbackHit
    }

    Log.d(
        TAG,
        "consumeLogEntry: No log match for exerciseId=${schedule.exerciseId}, " +
            "sessionId=${schedule.sessionId}",
    )
    return null
}

private data class LogKey(
    val sessionId: String,
    val exerciseId: String,
)

private data class WorkoutLineMetrics(
    val startInstant: Instant,
    val setBreakdownLabel: String,
    val caloriesLabel: String,
    val caloriesKcal: Float,
)

private fun resolveWorkoutLineMetrics(
    schedule: WorkoutSchedule,
    logEntry: WorkoutLogExerciseDetail?,
): WorkoutLineMetrics {
    val startInstant = logEntry?.startInstant ?: schedule.scheduledAt
        .atZone(Clock.systemDefaultZone().zone)
        .toInstant()

    // **BUG FIX**: Always prioritize actual log data over schedule data when log exists.
    // This fixes the "20 kg vs 80 kg" weight mismatch bug where schedule.weightKg was
    // incorrectly used as fallback even when the user had logged a different weight.
    val setBreakdownLabel = buildSetBreakdownLabel(logEntry)

    // Prioritize logged calories (actual performance) over estimated schedule calories
    val calories = logEntry?.energy?.kcal ?: estimateScheduleCalories(schedule, logEntry)
    val caloriesLabel = formatCaloriesLabel(calories)

    return WorkoutLineMetrics(
        startInstant = startInstant,
        setBreakdownLabel = setBreakdownLabel,
        caloriesLabel = caloriesLabel,
        caloriesKcal = calories,
    )
}

private fun buildSetBreakdownLabel(
    logEntry: WorkoutLogExerciseDetail?,
): String {
    if (logEntry == null) return ""
    val logSets = logEntry.sets.sortedBy { it.setIndex }
    return when (logEntry.measurementMode) {
        ExerciseMeasurementMode.Strength -> buildStrengthLinearProgression(logSets)
        ExerciseMeasurementMode.Duration -> buildDurationLinearProgression(logSets)
    }
}

private fun buildStrengthLinearProgression(logSets: List<WorkoutLogSetDetail>): String {
    val validSets = logSets
        .filter { it.weightKg > 0.0 }
    if (validSets.isEmpty()) return ""
    return validSets.mapIndexed { visualIndex, set ->
        formatProgressionItem(visualIndex + 1, "${set.reps} × ${formatWeight(set.weightKg)}kg")
    }.joinToString(separator = " \u2192 ")
}

private fun buildDurationLinearProgression(logSets: List<WorkoutLogSetDetail>): String {
    val validSets = logSets
        .mapNotNull { set ->
            val duration = set.durationSeconds?.takeIf { it > 0 } ?: return@mapNotNull null
            duration to set.reps
        }
    if (validSets.isEmpty()) return ""
    return validSets.mapIndexed { visualIndex, (durationSeconds, reps) ->
        formatProgressionItem(visualIndex + 1, "$reps × ${durationSeconds}s")
    }.joinToString(separator = " \u2192 ")
}

private fun formatProgressionItem(visualIndex: Int, detail: String): String = "$visualIndex: $detail"

private fun estimateScheduleCalories(schedule: WorkoutSchedule, logEntry: WorkoutLogExerciseDetail? = null): Float {
    // If we have log data, use actual logged values for estimation
    return if (logEntry != null && logEntry.sets.isNotEmpty()) {
        val logSets = logEntry.sets
        val totalReps = logSets.sumOf { it.reps.coerceAtLeast(0) }
        val averageLoadKg = logSets.mapNotNull { it.weightKg.takeIf { w -> w > 0.0 } }
            .let { loads -> if (loads.isEmpty()) 0.0 else loads.average() }
        val totalDurationSeconds = logSets.sumOf { it.durationSeconds ?: 0 }
        val durationMinutes = totalDurationSeconds / 60.0
        val bodyWeight = logEntry.energy?.bodyWeightUsed ?: DEFAULT_BODY_WEIGHT_KG

        CaloriesCalculator.estimateCalories(
            exerciseId = logEntry.exerciseId,
            measurementMode = logEntry.measurementMode,
            durationMinutes = durationMinutes,
            totalReps = totalReps,
            averageLoadKg = averageLoadKg,
            bodyWeightKg = bodyWeight,
            leanBodyMassKg = null,
        )
    } else {
        // No log data: estimate from schedule
        val durationMinutes = (schedule.durationSeconds ?: 0) / 60.0
        val totalReps = (schedule.sets.coerceAtLeast(0) * schedule.reps.coerceAtLeast(0))
        CaloriesCalculator.estimateCalories(
            exerciseId = schedule.exerciseId,
            measurementMode = schedule.measurementMode,
            durationMinutes = durationMinutes,
            totalReps = totalReps,
            averageLoadKg = schedule.weightKg.coerceAtLeast(0.0),
            bodyWeightKg = DEFAULT_BODY_WEIGHT_KG,
            leanBodyMassKg = null,
        )
    }
}

private fun formatCaloriesLabel(kcal: Float): String =
    "${kcal.roundToInt()} kcal"

private fun formatWeight(weightKg: Double): String {
    val rounded = String.format(Locale.ROOT, "%.1f", weightKg)
    return rounded.removeSuffix(".0")
}

/**
 * Groups exercise lines by sessionId into session blocks.
 * Exercises without a sessionId are placed in individual "singleton" blocks.
 */
private fun groupExercisesIntoSessionBlocks(
    lines: List<WorkoutCalendarExerciseLine>,
    workoutSessionsById: Map<String, WorkoutSession>,
    logSessionsById: Map<String, WorkoutLogSessionDetail>,
): List<WorkoutCalendarSessionBlock> {
    if (lines.isEmpty()) return emptyList()

    val locale = Locale.getDefault()
    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
    val systemZone = Clock.systemDefaultZone().zone

    // Group by sessionId; null sessionIds get individual groups
    val groupedBySession = lines.groupBy { it.sessionId }

    return groupedBySession.map { (sessionId, exercises) ->
        val sortedExercises = exercises.sortedBy { it.startInstant }
        val fallbackStartInstant = sortedExercises.first().startInstant
        val startInstant = resolveSessionStartInstant(
            sessionId = sessionId,
            workoutSessionsById = workoutSessionsById,
            logSessionsById = logSessionsById,
            fallbackStartInstant = fallbackStartInstant,
        )
        val endInstant = resolveSessionEndInstant(
            sessionId = sessionId,
            workoutSessionsById = workoutSessionsById,
            logSessionsById = logSessionsById,
            startInstant = startInstant,
            sortedExercises = sortedExercises,
        )

        val startTime = startInstant.atZone(systemZone).toLocalTime().format(timeFormatter)
        val endTime = endInstant.atZone(systemZone).toLocalTime().format(timeFormatter)

        val sessionTimeLabel = "$startTime - $endTime"

        WorkoutCalendarSessionBlock(
            sessionId = sessionId,
            sessionTimeLabel = sessionTimeLabel,
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
    logSessionsById: Map<String, WorkoutLogSessionDetail>,
    fallbackStartInstant: Instant,
): Instant {
    if (sessionId.isNullOrBlank()) return fallbackStartInstant
    return workoutSessionsById[sessionId]?.startInstant
        ?: logSessionsById[sessionId]?.startInstant
        ?: fallbackStartInstant
}

private fun resolveSessionEndInstant(
    sessionId: String?,
    workoutSessionsById: Map<String, WorkoutSession>,
    logSessionsById: Map<String, WorkoutLogSessionDetail>,
    startInstant: Instant,
    sortedExercises: List<WorkoutCalendarExerciseLine>,
): Instant {
    if (!sessionId.isNullOrBlank()) {
        workoutSessionsById[sessionId]?.endInstant?.let { return it }
        logSessionsById[sessionId]?.endInstant?.let { return it }
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
private const val TAG = "GetWorkoutDetailsUseCase"
