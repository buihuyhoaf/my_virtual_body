package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLine
import com.hoabui.virtualbody3d.domain.model.calendar.resolveWorkoutCalendarLineImage
import com.hoabui.virtualbody3d.domain.util.CaloriesCalculator
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.math.roundToInt

class ObserveWorkoutCalendarDayDetailUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
    private val exercisesRepository: ExercisesRepository,
) {
    operator fun invoke(day: LocalDate, zoneId: ZoneId): Flow<List<WorkoutCalendarExerciseLine>> {
        val dayKey = day.toEpochDay()
        return combine(
            workoutScheduleRepository.observeSchedulesInDayRange(dayKey, dayKey),
            exercisesRepository.getAllExercises(),
        ) { schedules, exercises ->
            val exerciseById = exercises.associateBy { it.id }
            schedules.mapNotNull { sch ->
                val rowId = sch.rowId ?: return@mapNotNull null
                val catalog = exerciseById[sch.exerciseId]
                val startLabel = sch.scheduledAt.format(
                    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.getDefault()),
                )
                val setBreakdownLabel = when (sch.measurementMode) {
                    com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode.Strength ->
                        "${sch.sets} Sets • ${formatWeight(sch.weightKg)} kg x ${sch.reps}"
                    com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode.Duration -> {
                        val minutes = (sch.durationSeconds ?: 0) / 60
                        "${sch.sets} Sets • ${minutes.coerceAtLeast(1)}m"
                    }
                }
                val totalReps = sch.sets.coerceAtLeast(0) * sch.reps.coerceAtLeast(0)
                val durationMinutes = (sch.durationSeconds ?: 0) / 60.0
                val calories = CaloriesCalculator.estimateCalories(
                    exerciseId = sch.exerciseId,
                    measurementMode = sch.measurementMode,
                    durationMinutes = durationMinutes,
                    totalReps = totalReps,
                    averageLoadKg = sch.weightKg.coerceAtLeast(0.0),
                    bodyWeightKg = DEFAULT_BODY_WEIGHT_KG,
                    leanBodyMassKg = null,
                )
                WorkoutCalendarExerciseLine(
                    rowId = rowId,
                    exerciseId = sch.exerciseId,
                    exerciseDisplayName = catalog?.name ?: sch.exerciseId,
                    startTimeLabel = startLabel,
                    setBreakdownLabel = setBreakdownLabel,
                    caloriesLabel = "🔥 ${calories.roundToInt()} kcal",
                    sets = sch.sets,
                    reps = sch.reps,
                    durationSeconds = sch.durationSeconds,
                    measurementMode = sch.measurementMode,
                    executionStatus = sch.executionStatus,
                    sessionId = sch.sessionId,
                    image = resolveWorkoutCalendarLineImage(
                        exerciseLocalImageName = sch.exerciseLocalImageName,
                        exerciseImageResUrl = sch.exerciseImageResUrl,
                        catalogExercise = catalog,
                    ),
                )
            }
        }
    }
}

private fun formatWeight(weightKg: Double): String {
    val rounded = "%.1f".format(weightKg)
    return rounded.removeSuffix(".0")
}

private const val DEFAULT_BODY_WEIGHT_KG = 70.0
