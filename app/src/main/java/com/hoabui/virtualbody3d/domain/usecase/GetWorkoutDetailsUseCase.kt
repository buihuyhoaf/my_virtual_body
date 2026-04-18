package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLine
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
    operator fun invoke(day: LocalDate, zoneId: ZoneId): Flow<List<WorkoutCalendarExerciseLine>> {
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
                "${log.sessionId}:${log.exerciseId}"
            }.mapValues { (_, list) -> list.sortedBy { it.startInstant } }.toMutableMap()
            schedules.sortedBy { it.scheduledAt }
                .mapNotNull { schedule ->
                    val rowId = schedule.rowId ?: return@mapNotNull null
                    val logEntry = consumeLogEntry(schedule, logBySessionExercise, logs)
                    val (startInstant, setBreakdownLabel, caloriesLabel) =
                        resolveWorkoutLineMetrics(schedule, logEntry, zoneId)
                    val catalog = exerciseById[schedule.exerciseId]
                    WorkoutCalendarExerciseLine(
                        rowId = rowId,
                        exerciseId = schedule.exerciseId,
                        exerciseDisplayName = logEntry?.displayNameSnapshot ?: catalog?.name ?: schedule.exerciseId,
                        startTimeLabel = formatStartTimeLabel(startInstant, zoneId),
                        setBreakdownLabel = setBreakdownLabel,
                        caloriesLabel = caloriesLabel,
                        sets = schedule.sets,
                        reps = schedule.reps,
                        durationSeconds = schedule.durationSeconds,
                        measurementMode = schedule.measurementMode,
                        executionStatus = schedule.executionStatus,
                        sessionId = schedule.sessionId,
                        image = resolveWorkoutCalendarLineImage(
                            exerciseLocalImageName = schedule.exerciseLocalImageName,
                            exerciseImageResUrl = schedule.exerciseImageResUrl,
                            catalogExercise = catalog,
                        ),
                    )
                }
        }
    }
}

private fun consumeLogEntry(
    schedule: WorkoutSchedule,
    logBySessionExercise: MutableMap<String, List<WorkoutLogExerciseDetail>>,
    logFallbackList: MutableList<WorkoutLogExerciseDetail>,
): WorkoutLogExerciseDetail? {
    val key = schedule.sessionId?.let { "$it:${schedule.exerciseId}" }
    if (key != null) {
        val list = logBySessionExercise[key]
        if (!list.isNullOrEmpty()) {
            logBySessionExercise[key] = list.drop(1)
            val entry = list.first()
            logFallbackList.remove(entry)
            return entry
        }
    }
    val fallbackIndex = logFallbackList.indexOfFirst { it.exerciseId == schedule.exerciseId }
    return if (fallbackIndex >= 0) logFallbackList.removeAt(fallbackIndex) else null
}

private data class WorkoutLineMetrics(
    val startInstant: Instant,
    val setBreakdownLabel: String,
    val caloriesLabel: String,
)

private fun resolveWorkoutLineMetrics(
    schedule: WorkoutSchedule,
    logEntry: WorkoutLogExerciseDetail?,
    zoneId: ZoneId,
): WorkoutLineMetrics {
    val startInstant = logEntry?.startInstant ?: schedule.scheduledAt.atZone(zoneId).toInstant()
    val setBreakdownLabel = buildSetBreakdownLabel(
        measurementMode = schedule.measurementMode,
        logEntry = logEntry,
        schedule = schedule,
    )
    val calories = logEntry?.energy?.kcal ?: estimateScheduleCalories(schedule)
    val caloriesLabel = formatCaloriesLabel(calories)
    return WorkoutLineMetrics(
        startInstant = startInstant,
        setBreakdownLabel = setBreakdownLabel,
        caloriesLabel = caloriesLabel,
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
            if (logSets.isNotEmpty()) {
                val reps = logSets.map { it.reps }.filter { it > 0 }
                val weights = logSets.map { it.weightKg }.filter { it > 0.0 }
                formatStrengthSetBreakdown(logSets.size, reps, weights)
            } else {
                formatStrengthSetBreakdown(
                    setCount = schedule.sets,
                    reps = listOf(schedule.reps).filter { it > 0 },
                    weights = listOf(schedule.weightKg).filter { it > 0.0 },
                )
            }
        }
        ExerciseMeasurementMode.Duration -> {
            val durations = logSets.mapNotNull { it.durationSeconds }.filter { it > 0 }
            val fallbackDuration = schedule.durationSeconds?.takeIf { it > 0 }
            formatDurationSetBreakdown(
                setCount = if (logSets.isNotEmpty()) logSets.size else schedule.sets,
                durations = durations,
                fallbackDurationSeconds = fallbackDuration,
            )
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
    val isUniformWeight = weights.distinctBy { it.formatWeightKey() }.size <= 1
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
        "%d:%02d".format(mins, secs)
    }
}

private fun Double.formatWeightKey(): String =
    "%.1f".format(this)

private fun formatWeight(weightKg: Double): String {
    val rounded = "%.1f".format(weightKg)
    return rounded.removeSuffix(".0")
}

private const val DEFAULT_BODY_WEIGHT_KG = 70.0
