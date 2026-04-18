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
        "squat" to ExerciseCaloriesMetadata(met = 7.5, tutSecondsPerRep = 4.5),
        "bicep_curl" to ExerciseCaloriesMetadata(met = 3.5, tutSecondsPerRep = 4.0),
        "bench_press" to ExerciseCaloriesMetadata(met = 7.5, tutSecondsPerRep = 4.0),
        "deadlift" to ExerciseCaloriesMetadata(met = 7.5, tutSecondsPerRep = 4.5),
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
        val met = when {
            measurementMode == ExerciseMeasurementMode.Strength && isHeavyCompoundLift(exerciseId) ->
                max(metadata?.met ?: DEFAULT_STRENGTH_MET, HEAVY_COMPOUND_BASE_MET)
            else -> metadata?.met ?: defaultMetFor(measurementMode)
        }
        val tutSeconds = metadata?.tutSecondsPerRep ?: DEFAULT_TUT_SECONDS_PER_REP
        val effectiveMinutes = when (measurementMode) {
            ExerciseMeasurementMode.Duration -> durationMinutes
            ExerciseMeasurementMode.Strength -> totalReps.coerceAtLeast(0) * tutSeconds / 60.0
        }.coerceAtLeast(0.0)
        if (effectiveMinutes <= 0.0) {
            return 0f
        }
        val effectiveBodyWeightKg = if (bodyWeightKg > 0.0) bodyWeightKg else DEFAULT_BODY_WEIGHT_KG
        val safeLoadKg = averageLoadKg.coerceAtLeast(0.0)

        /*
         * Strength model:
         * BaseCalories = BaseMET * 0.0175 * BodyWeightKg * EffectiveMinutes
         * EffectiveMinutes = (Reps * TUT_Per_Rep) / 60
         * IntensityScaler = 1 + (LiftedWeight / BodyWeightKg)
         * EPOCScaler = 1 + (k * (LiftedWeight / BodyWeightKg))
         * FinalCalories = BaseCalories * IntensityScaler * EPOCScaler
         */
        val baseCalories = met * MET_WEIGHT_FACTOR * effectiveBodyWeightKg * effectiveMinutes
        val loadRatio = if (measurementMode == ExerciseMeasurementMode.Strength && safeLoadKg > 0.0) {
            safeLoadKg / effectiveBodyWeightKg
        } else {
            0.0
        }
        val intensityScaler = if (measurementMode == ExerciseMeasurementMode.Strength) {
            1.0 + loadRatio
        } else {
            1.0
        }
        val epocCoefficient = if (measurementMode == ExerciseMeasurementMode.Strength) {
            epocCoefficientFor(exerciseId)
        } else {
            0.0
        }
        val epocScaler = if (measurementMode == ExerciseMeasurementMode.Strength) {
            1.0 + (epocCoefficient * loadRatio)
        } else {
            1.0
        }
        return (baseCalories * intensityScaler * epocScaler).toFloat()
    }

    private fun defaultMetFor(measurementMode: ExerciseMeasurementMode): Double = when (measurementMode) {
        ExerciseMeasurementMode.Duration -> DEFAULT_CARDIO_MET
        ExerciseMeasurementMode.Strength -> DEFAULT_STRENGTH_MET
    }

    private fun isHeavyCompoundLift(exerciseId: String): Boolean =
        HEAVY_COMPOUND_LIFTS.contains(exerciseId.lowercase())

    private fun epocCoefficientFor(exerciseId: String): Double =
        if (isHeavyCompoundLift(exerciseId)) EPOC_COEFFICIENT_COMPOUND else EPOC_COEFFICIENT_ISOLATION
}

private const val MET_WEIGHT_FACTOR = 0.0175
private const val DEFAULT_TUT_SECONDS_PER_REP = 4.0
private const val DEFAULT_CARDIO_MET = 6.0
private const val DEFAULT_STRENGTH_MET = 5.0
private const val DEFAULT_BODY_WEIGHT_KG = 70.0
private const val HEAVY_COMPOUND_BASE_MET = 7.5
private const val EPOC_COEFFICIENT_COMPOUND = 0.4
private const val EPOC_COEFFICIENT_ISOLATION = 0.2

private val HEAVY_COMPOUND_LIFTS = setOf(
    "deadlift",
    "squat",
    "bench_press",
)
