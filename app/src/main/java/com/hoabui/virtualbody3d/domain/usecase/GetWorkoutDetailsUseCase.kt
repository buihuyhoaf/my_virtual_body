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
        val logDayKey = day.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return combine(
            workoutScheduleRepository.observeSchedulesInDayRange(dayKey, dayKey),
            workoutSessionRepository.observeWorkoutSessionsInDayRange(dayKey, dayKey),
            workoutLogRepository.observeWorkoutLogsByDay(logDayKey),
            exercisesRepository.getAllExercises(),
        ) { schedules, workoutSessions, logSessions, exercises ->
            val exerciseById = exercises.associateBy { it.id }
            val workoutSessionsById = workoutSessions.associateBy { it.id }
            val logSessionsById = logSessions.associateBy { it.id }
            val logBySessionExercise = logSessions.flatMap { it.exercises }.groupBy { log ->
                LogKey(log.sessionId, log.exerciseId)
            }.mapValues { (_, list) -> list.sortedBy { it.startInstant } }.toMutableMap()

            // Map schedules to exercise lines
            val lines = schedules.sortedBy { it.scheduledAt }
                .mapNotNull { schedule ->
                    val rowId = schedule.rowId ?: return@mapNotNull null
                    val logEntry = consumeLogEntry(schedule, logBySessionExercise)
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
    logBySessionExercise: MutableMap<LogKey, List<WorkoutLogExerciseDetail>>,
): WorkoutLogExerciseDetail? {
    val key = schedule.sessionId?.let { LogKey(it, schedule.exerciseId) }
    if (key != null) {
        val list = logBySessionExercise[key]
        if (!list.isNullOrEmpty()) {
            logBySessionExercise[key] = list.drop(1)
            val entry = list.first()
            Log.d(TAG, "consumeLogEntry: Matched by sessionId+exerciseId key=$key -> entry.id=${entry.id}")
            return entry
        }
    }
    // Strict session binding: never cross-match by exerciseId alone.
    Log.d(
        TAG,
        "consumeLogEntry: No strict session match for exerciseId=${schedule.exerciseId}, " +
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
    val setBreakdownLabel = buildSetBreakdownLabel(
        measurementMode = logEntry?.measurementMode ?: schedule.measurementMode,
        logEntry = logEntry,
        schedule = schedule,
    )

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
    measurementMode: ExerciseMeasurementMode,
    logEntry: WorkoutLogExerciseDetail?,
    schedule: WorkoutSchedule,
): String {
    val logSets = logEntry?.sets?.sortedBy { it.setIndex }.orEmpty()

    return when (measurementMode) {
        ExerciseMeasurementMode.Strength -> {
            if (logEntry != null) {
                buildStrengthLinearProgression(logSets)
                    ?: formatStrengthSetBreakdown(
                        setCount = logSets.size,
                        reps = logSets.map { it.reps }.filter { it > 0 },
                        weights = logSets.map { it.weightKg }.filter { it > 0.0 },
                    )
            } else {
                buildStrengthLinearProgressionFromSchedule(schedule)
                    ?: formatStrengthSetBreakdown(
                        setCount = schedule.sets,
                        reps = listOf(schedule.reps).filter { it > 0 },
                        weights = emptyList(),
                    )
            }
        }
        ExerciseMeasurementMode.Duration -> {
            if (logEntry != null && logSets.isNotEmpty()) {
                buildDurationLinearProgression(logSets)
                    ?: run {
                        val durations = logSets.mapNotNull { it.durationSeconds }.filter { it > 0 }
                        formatDurationSetBreakdown(
                            setCount = logSets.size,
                            durations = durations,
                            fallbackDurationSeconds = null,
                        )
                    }
            } else {
                buildDurationLinearProgressionFromSchedule(schedule)
                    ?: run {
                        val fallbackDuration = schedule.durationSeconds?.takeIf { it > 0 }
                        formatDurationSetBreakdown(
                            setCount = schedule.sets,
                            durations = emptyList(),
                            fallbackDurationSeconds = fallbackDuration,
                        )
                    }
            }
        }
    }
}

private fun buildStrengthLinearProgression(logSets: List<WorkoutLogSetDetail>): String? {
    val validSets = logSets
        .sortedBy { it.setIndex }
        .filter { it.reps > 0 || it.weightKg > 0.0 }
    if (validSets.isEmpty()) return null
    return validSets.mapIndexed { index, set ->
        val repsPart = set.reps.takeIf { it > 0 }?.toString()
        val weightPart = set.weightKg.takeIf { it > 0.0 }?.let { "${formatWeight(it)}kg" }
        val detail = when {
            repsPart != null && weightPart != null -> "${repsPart}x$weightPart"
            repsPart != null -> "$repsPart reps"
            weightPart != null -> weightPart
            else -> "-"
        }
        "${index + 1}: $detail"
    }.joinToString(separator = " \u2192 ")
}

private fun buildStrengthLinearProgressionFromSchedule(schedule: WorkoutSchedule): String? {
    val setCount = schedule.sets.coerceAtLeast(0)
    if (setCount <= 0) return null
    val repsPart = schedule.reps.takeIf { it > 0 }?.toString()
    val weightPart = schedule.weightKg.takeIf { it > 0.0 }?.let { "${formatWeight(it)}kg" }
    val detail = when {
        repsPart != null && weightPart != null -> "${repsPart}x$weightPart"
        repsPart != null -> "$repsPart reps"
        weightPart != null -> weightPart
        else -> return null
    }
    return (1..setCount).joinToString(separator = " \u2192 ") { visualIndex -> "$visualIndex: $detail" }
}

private fun buildDurationLinearProgression(logSets: List<WorkoutLogSetDetail>): String? {
    val validDurations = logSets
        .sortedBy { it.setIndex }
        .mapNotNull { it.durationSeconds?.takeIf { d -> d > 0 } }
    if (validDurations.isEmpty()) return null
    return validDurations.mapIndexed { index, duration ->
        "${index + 1}: ${formatDurationSeconds(duration)}"
    }.joinToString(separator = " \u2192 ")
}

private fun buildDurationLinearProgressionFromSchedule(schedule: WorkoutSchedule): String? {
    val setCount = schedule.sets.coerceAtLeast(0)
    val duration = schedule.durationSeconds?.takeIf { it > 0 } ?: return null
    if (setCount <= 0) return null
    val detail = formatDurationSeconds(duration)
    return (1..setCount).joinToString(separator = " \u2192 ") { visualIndex -> "$visualIndex: $detail" }
}

private fun formatStrengthSetBreakdown(
    setCount: Int,
    reps: List<Int>,
    weights: List<Double>,
): String {
    val safeSetCount = setCount.coerceAtLeast(0)
    if (safeSetCount == 0) return "0 Sets"
    val repsRange = reps.takeIf { it.isNotEmpty() }?.let { repsList ->
        val min = repsList.minOrNull() ?: 0
        val max = repsList.maxOrNull() ?: min
        min to max
    }
    val avgWeight = weights.takeIf { it.isNotEmpty() }?.average()
    val isUniformReps = repsRange?.let { it.first == it.second } ?: false
    val isUniformWeight = weights.distinctBy { it.normalizeWeightForGrouping() }.size <= 1
    val weightLabel = avgWeight?.let { formatWeight(it) }
    return if (isUniformReps && isUniformWeight && repsRange != null && avgWeight != null) {
        "$safeSetCount Sets • $weightLabel kg x ${repsRange.first}"
    } else {
        val repsLabel = repsRange?.let { range ->
            if (range.first == range.second) range.first.toString() else "${range.first}-${range.second}"
        }
        when {
            weightLabel != null && repsLabel != null ->
                "$safeSetCount Sets • $weightLabel kg avg x $repsLabel"
            weightLabel != null ->
                "$safeSetCount Sets • $weightLabel kg avg"
            repsLabel != null ->
                "$safeSetCount Sets • $repsLabel reps"
            else -> "$safeSetCount Sets"
        }
    }
}

private fun formatDurationSetBreakdown(
    setCount: Int,
    durations: List<Int>,
    fallbackDurationSeconds: Int?,
): String {
    val safeSetCount = setCount.coerceAtLeast(0)
    val durationsSafe = durations.filter { it > 0 }
    val totalDuration = durationsSafe.sum()
    val effectiveDuration = if (totalDuration > 0) totalDuration else fallbackDurationSeconds ?: 0
    if (safeSetCount == 0 && effectiveDuration <= 0) return "0 Sets"
    val durationLabel = formatDurationSeconds(effectiveDuration)
    return if (safeSetCount > 0) {
        "$safeSetCount Sets • $durationLabel"
    } else {
        "Duration • $durationLabel"
    }
}

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

private fun formatDurationSeconds(seconds: Int): String {
    val safeSeconds = seconds.coerceAtLeast(0)
    val mins = safeSeconds / 60
    val secs = safeSeconds % 60
    return if (secs == 0) {
        "${mins}m"
    } else {
        String.format(Locale.ROOT, "%d:%02d", mins, secs)
    }
}

private fun Double.normalizeWeightForGrouping(): String =
    String.format(Locale.ROOT, "%.1f", this)

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
