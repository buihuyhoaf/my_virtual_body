package com.hoabui.virtualbody3d.domain.util

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import kotlin.math.max

data class ExerciseCaloriesMetadata(
    val met: Double,
    val tutSecondsPerRep: Double,
)

object ExerciseCaloriesMetadataProvider {
    /**
     * Seed metadata using common MET tables and typical time-under-tension heuristics.
     * Values are intentionally conservative placeholders until the catalog supplies per-exercise data.
     */
    private val metadataById: Map<String, ExerciseCaloriesMetadata> = mapOf(
        "squat" to ExerciseCaloriesMetadata(met = 6.0, tutSecondsPerRep = 4.5),
        "bicep_curl" to ExerciseCaloriesMetadata(met = 3.5, tutSecondsPerRep = 4.0),
        "bench_press" to ExerciseCaloriesMetadata(met = 5.0, tutSecondsPerRep = 4.0),
        "deadlift" to ExerciseCaloriesMetadata(met = 6.0, tutSecondsPerRep = 4.5),
        "running" to ExerciseCaloriesMetadata(met = 7.5, tutSecondsPerRep = DEFAULT_TUT_SECONDS_PER_REP),
        "cycling" to ExerciseCaloriesMetadata(met = 6.8, tutSecondsPerRep = DEFAULT_TUT_SECONDS_PER_REP),
    )

    fun metadataFor(exerciseId: String): ExerciseCaloriesMetadata? =
        metadataById[exerciseId] ?: metadataById[exerciseId.lowercase()]
}

object CaloriesCalculator {
    fun estimateCalories(
        exerciseId: String,
        measurementMode: ExerciseMeasurementMode,
        durationMinutes: Double,
        totalReps: Int,
        averageLoadKg: Double,
        bodyWeightKg: Double,
        leanBodyMassKg: Double?,
    ): Float {
        val metadata = ExerciseCaloriesMetadataProvider.metadataFor(exerciseId)
        val met = metadata?.met ?: defaultMetFor(measurementMode)
        val tutSeconds = metadata?.tutSecondsPerRep ?: DEFAULT_TUT_SECONDS_PER_REP
        val effectiveMinutes = when (measurementMode) {
            ExerciseMeasurementMode.Duration -> durationMinutes
            ExerciseMeasurementMode.Strength -> max(durationMinutes, totalReps * tutSeconds / 60.0)
        }.coerceAtLeast(0.0)
        if (effectiveMinutes <= 0.0 || bodyWeightKg <= 0.0) {
            return 0f
        }
        val baseCalories = if (leanBodyMassKg != null && leanBodyMassKg > 0.0) {
            val bmr = 370.0 + (21.6 * leanBodyMassKg)
            (bmr / MINUTES_PER_DAY) * met * effectiveMinutes
        } else {
            met * MET_WEIGHT_FACTOR * bodyWeightKg * effectiveMinutes
        }
        val intensityFactor = if (averageLoadKg > 0.0 && bodyWeightKg > 0.0) {
            1.0 + (averageLoadKg / bodyWeightKg)
        } else {
            1.0
        }
        return (baseCalories * intensityFactor).toFloat()
    }

    private fun defaultMetFor(measurementMode: ExerciseMeasurementMode): Double = when (measurementMode) {
        ExerciseMeasurementMode.Duration -> DEFAULT_CARDIO_MET
        ExerciseMeasurementMode.Strength -> DEFAULT_STRENGTH_MET
    }
}

private const val MET_WEIGHT_FACTOR = 0.0175
private const val MINUTES_PER_DAY = 1440.0
private const val DEFAULT_TUT_SECONDS_PER_REP = 4.0
private const val DEFAULT_CARDIO_MET = 6.0
private const val DEFAULT_STRENGTH_MET = 5.0
