package com.hoabui.virtualbody3d.domain.usecase

import android.util.Log
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLine
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarSessionBlock
import com.hoabui.virtualbody3d.domain.model.calendar.resolveWorkoutCalendarLineImage
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.model.workoutlog.WorkoutLogExerciseDetail
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutLogRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import com.hoabui.virtualbody3d.domain.util.CaloriesCalculator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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
    private val exercisesRepository: ExercisesRepository,
) {
    /**
     * Returns workout session blocks grouped by sessionId for the given day.
     * Each block contains the session time range and associated exercises.
     */
    operator fun invoke(day: LocalDate, zoneId: ZoneId): Flow<List<WorkoutCalendarSessionBlock>> {
        val dayKey = day.toEpochDay()
        val logDayKey = day.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return combine(
            workoutScheduleRepository.observeSchedulesInDayRange(dayKey, dayKey),
            workoutLogRepository.observeWorkoutLogsByDay(logDayKey),
            exercisesRepository.getAllExercises(),
        ) { schedules, sessions, exercises ->
            val exerciseById = exercises.associateBy { it.id }
            val logs = sessions.flatMap { it.exercises }.toMutableList()
            val logBySessionExercise = logs.groupBy { log ->
                LogKey(log.sessionId, log.exerciseId)
            }.mapValues { (_, list) -> list.sortedBy { it.startInstant } }.toMutableMap()

            // Map schedules to exercise lines
            val lines = schedules.sortedBy { it.scheduledAt }
                .mapNotNull { schedule ->
                    val rowId = schedule.rowId ?: return@mapNotNull null
                    val logEntry = consumeLogEntry(schedule, logBySessionExercise, logs)
                    val (startInstant, setBreakdownLabel, caloriesLabel, caloriesKcal) =
                        resolveWorkoutLineMetrics(schedule, logEntry, zoneId)
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
            groupExercisesIntoSessionBlocks(lines, zoneId)
        }
    }
}

private fun consumeLogEntry(
    schedule: WorkoutSchedule,
    logBySessionExercise: MutableMap<LogKey, List<WorkoutLogExerciseDetail>>,
    logFallbackList: MutableList<WorkoutLogExerciseDetail>,
): WorkoutLogExerciseDetail? {
    val key = schedule.sessionId?.let { LogKey(it, schedule.exerciseId) }
    if (key != null) {
        val list = logBySessionExercise[key]
        if (!list.isNullOrEmpty()) {
            logBySessionExercise[key] = list.drop(1)
            val entry = list.first()
            logFallbackList.remove(entry)
            Log.d(TAG, "consumeLogEntry: Matched by sessionId+exerciseId key=$key -> entry.id=${entry.id}")
            return entry
        }
    }
    // Fallback: match by exerciseId only if no sessionId match
    val fallbackIndex = logFallbackList.indexOfFirst { it.exerciseId == schedule.exerciseId }
    return if (fallbackIndex >= 0) {
        val entry = logFallbackList.removeAt(fallbackIndex)
        Log.d(TAG, "consumeLogEntry: Fallback match by exerciseId=${schedule.exerciseId} -> entry.id=${entry.id}")
        entry
    } else {
        Log.d(TAG, "consumeLogEntry: No match found for schedule.exerciseId=${schedule.exerciseId}")
        null
    }
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
    zoneId: ZoneId,
): WorkoutLineMetrics {
    val startInstant = logEntry?.startInstant ?: schedule.scheduledAt.atZone(zoneId).toInstant()

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
            // **BUG FIX**: Always use log data exclusively when available.
            // Previously, when logSets was empty but had a match, it could fallback to schedule values.
            // Now we only use schedule values when there is no log entry at all.
            if (logEntry != null && logSets.isNotEmpty()) {
                // Use logged actual performance data (fixes 20kg vs 80kg bug)
                val reps = logSets.map { it.reps }.filter { it > 0 }
                val weights = logSets.map { it.weightKg }.filter { it > 0.0 }
                Log.d(TAG, "buildSetBreakdownLabel: Using LOG data - weights=$weights, reps=$reps")
                formatStrengthSetBreakdown(logSets.size, reps, weights)
            } else {
                // No log entry: use planned schedule values
                Log.d(TAG, "buildSetBreakdownLabel: Using SCHEDULE data - weight=${schedule.weightKg}, reps=${schedule.reps}")
                formatStrengthSetBreakdown(
                    setCount = schedule.sets,
                    reps = listOf(schedule.reps).filter { it > 0 },
                    weights = listOf(schedule.weightKg).filter { it > 0.0 },
                )
            }
        }
        ExerciseMeasurementMode.Duration -> {
            if (logEntry != null && logSets.isNotEmpty()) {
                // Use logged duration data
                val durations = logSets.mapNotNull { it.durationSeconds }.filter { it > 0 }
                formatDurationSetBreakdown(
                    setCount = logSets.size,
                    durations = durations,
                    fallbackDurationSeconds = null, // No fallback when we have log data
                )
            } else {
                // No log entry: use planned schedule values
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

private fun formatStartTimeLabel(instant: Instant, zoneId: ZoneId): String {
    val locale = Locale.getDefault()
    val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
    return instant.atZone(zoneId).toLocalTime().format(formatter)
}

private fun formatCaloriesLabel(kcal: Float): String =
    "🔥 ${kcal.roundToInt()} kcal"

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
    zoneId: ZoneId,
): List<WorkoutCalendarSessionBlock> {
    if (lines.isEmpty()) return emptyList()

    val locale = Locale.getDefault()
    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)

    // Group by sessionId; null sessionIds get individual groups
    val groupedBySession = lines.groupBy { it.sessionId }

    return groupedBySession.map { (sessionId, exercises) ->
        val sortedExercises = exercises.sortedBy { it.startInstant }
        val startInstant = sortedExercises.first().startInstant
        val endInstant = sortedExercises.last().startInstant

        // Calculate session time label: "08:00 AM - 09:30 AM session" or just "08:00 AM" for single exercises
        val startTime = startInstant.atZone(zoneId).toLocalTime().format(timeFormatter)
        val endTime = endInstant.atZone(zoneId).toLocalTime().format(timeFormatter)

        val sessionTimeLabel = if (sortedExercises.size == 1 || startTime == endTime) {
            startTime
        } else {
            "$startTime - $endTime"
        }

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

private const val DEFAULT_BODY_WEIGHT_KG = 70.0
private const val TAG = "GetWorkoutDetailsUseCase"
