package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLine
import com.hoabui.virtualbody3d.domain.model.calendar.resolveWorkoutCalendarLineImage
import com.hoabui.virtualbody3d.domain.util.CaloriesCalculator
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.math.roundToInt
import java.util.Locale

class ObserveWorkoutCalendarDayDetailUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
    private val exercisesRepository: ExercisesRepository,
) {
    operator fun invoke(day: LocalDate): Flow<List<WorkoutCalendarExerciseLine>> {
        val dayKey = day.toEpochDay()
        val zoneId = ZoneId.systemDefault()
        return combine(
            workoutScheduleRepository.observeSchedulesInDayRange(dayKey, dayKey),
            exercisesRepository.getAllExercises(),
        ) { schedules, exercises ->
            val exerciseById = exercises.associateBy { it.id }
            schedules.mapNotNull { sch ->
                val rowId = sch.rowId ?: return@mapNotNull null
                val catalog = exerciseById[sch.exerciseId]
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
                    setBreakdownLabel = setBreakdownLabel,
                    caloriesLabel = "🔥 ${calories.roundToInt()} kcal",
                    caloriesKcal = calories,
                    sets = sch.sets,
                    reps = sch.reps,
                    durationSeconds = sch.durationSeconds,
                    measurementMode = sch.measurementMode,
                    sessionId = sch.sessionId,
                    image = resolveWorkoutCalendarLineImage(
                        exerciseLocalImageName = sch.exerciseLocalImageName,
                        exerciseImageResUrl = sch.exerciseImageResUrl,
                        catalogExercise = catalog,
                    ),
                    startInstant = sch.scheduledAt.atZone(zoneId).toInstant(),
                )
            }
        }
    }
}

private fun formatWeight(weightKg: Double): String {
    val rounded = String.format(Locale.ROOT, "%.1f", weightKg)
    return rounded.removeSuffix(".0")
}

private const val DEFAULT_BODY_WEIGHT_KG = 70.0
