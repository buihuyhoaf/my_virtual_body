package com.hoabui.virtualbody3d.domain.util

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import java.util.Locale
import kotlin.math.max

data class ExerciseCaloriesMetadata(
    val met: Double,
    val tutSecondsPerRep: Double,
)

data class CaloriesEstimate(
    val calories: Float,
    val met: Double,
    val epocFactor: Double,
)

object ExerciseCaloriesMetadataProvider {
    /**
     * Seed metadata using common MET tables and typical time-under-tension heuristics.
     * Values are intentionally conservative placeholders until the catalog supplies per-exercise data.
     */
    private val metadataById: Map<String, ExerciseCaloriesMetadata> = mapOf(
        "squat" to ExerciseCaloriesMetadata(met = 6.0, tutSecondsPerRep = 4.5),
        "bicep_curl" to ExerciseCaloriesMetadata(met = 3.5, tutSecondsPerRep = 4.5),
        "bench_press" to ExerciseCaloriesMetadata(met = 5.0, tutSecondsPerRep = 4.5),
        "deadlift" to ExerciseCaloriesMetadata(met = HEAVY_COMPOUND_BASE_MET, tutSecondsPerRep = 4.5),
        "running" to ExerciseCaloriesMetadata(met = 7.5, tutSecondsPerRep = DEFAULT_TUT_SECONDS_PER_REP),
        "cycling" to ExerciseCaloriesMetadata(met = 6.8, tutSecondsPerRep = DEFAULT_TUT_SECONDS_PER_REP),
    )

    fun metadataFor(exerciseId: String): ExerciseCaloriesMetadata? =
        metadataById[exerciseId] ?: metadataById[exerciseId.lowercase(Locale.ROOT)]
}

object CaloriesCalculator {
    @Suppress("UNUSED_PARAMETER")
    fun estimateCalories(
        exerciseId: String,
        measurementMode: ExerciseMeasurementMode,
        durationMinutes: Double,
        totalReps: Int,
        averageLoadKg: Double,
        bodyWeightKg: Double,
        leanBodyMassKg: Double?,
    ): Float {
        return estimateCaloriesWithMetadata(
            exerciseId = exerciseId,
            measurementMode = measurementMode,
            durationMinutes = durationMinutes,
            totalReps = totalReps,
            averageLoadKg = averageLoadKg,
            bodyWeightKg = bodyWeightKg,
            leanBodyMassKg = leanBodyMassKg,
        ).calories
    }

    @Suppress("UNUSED_PARAMETER")
    fun estimateCaloriesWithMetadata(
        exerciseId: String,
        measurementMode: ExerciseMeasurementMode,
        durationMinutes: Double,
        totalReps: Int,
        averageLoadKg: Double,
        bodyWeightKg: Double,
        leanBodyMassKg: Double?,
    ): CaloriesEstimate {
        val normalizedExerciseId = exerciseId.lowercase(Locale.ROOT)
        val metadata = ExerciseCaloriesMetadataProvider.metadataFor(normalizedExerciseId)
        val met = when {
            // Enforce a minimum MET for heavy compounds even if metadata is missing or conservative.
            measurementMode == ExerciseMeasurementMode.Strength && isHeavyCompoundLift(normalizedExerciseId) ->
                max(metadata?.met ?: DEFAULT_STRENGTH_MET, HEAVY_COMPOUND_BASE_MET)
            else -> metadata?.met ?: defaultMetFor(measurementMode)
        }
        val tutSeconds = metadata?.tutSecondsPerRep ?: DEFAULT_TUT_SECONDS_PER_REP
        val effectiveMinutes = when (measurementMode) {
            ExerciseMeasurementMode.Duration -> durationMinutes
            // Strength uses time-under-tension only; rest periods are intentionally excluded.
            ExerciseMeasurementMode.Strength -> totalReps * tutSeconds / 60.0
        }.coerceAtLeast(0.0)
        if (effectiveMinutes <= 0.0) {
            return CaloriesEstimate(
                calories = 0f,
                met = met,
                epocFactor = 1.0,
            )
        }
        // Body weight fallback is applied below when user profile data is incomplete to avoid zero output.
        val effectiveBodyWeightKg = if (bodyWeightKg > 0.0) bodyWeightKg else DEFAULT_BODY_WEIGHT_KG
        val safeLoadKg = averageLoadKg.coerceAtLeast(0.0)

        /*
         * Strength model:
         * BaseCalories = BaseMET * 0.0175 * BodyWeightKg * EffectiveMinutes
         * EffectiveMinutes = (Reps * TUT_Per_Rep) / 60
         * IntensityScaler = 1 + (AverageLoadKg / BodyWeightKg)
         * EPOCScaler = 1 + (k * (AverageLoadKg / BodyWeightKg))
         * FinalCalories = BaseCalories * IntensityScaler * EPOCScaler
         */
        // Lean body mass is intentionally not used to keep the strength model MET-based and consistent.
        // The parameter remains for compatibility with existing callers.
        val baseCalories = met * MET_WEIGHT_FACTOR * effectiveBodyWeightKg * effectiveMinutes
        val isStrength = measurementMode == ExerciseMeasurementMode.Strength
        val loadRatio = if (isStrength && safeLoadKg > 0.0) safeLoadKg / effectiveBodyWeightKg else 0.0
        val (intensityScaler, epocScaler) = if (isStrength) {
            val epocCoefficient = epocCoefficientFor(normalizedExerciseId)
            (1.0 + loadRatio) to (1.0 + (epocCoefficient * loadRatio))
        } else {
            1.0 to 1.0
        }
        return CaloriesEstimate(
            calories = (baseCalories * intensityScaler * epocScaler).toFloat(),
            met = met,
            epocFactor = epocScaler,
        )
    }

    private fun defaultMetFor(measurementMode: ExerciseMeasurementMode): Double = when (measurementMode) {
        ExerciseMeasurementMode.Duration -> DEFAULT_CARDIO_MET
        ExerciseMeasurementMode.Strength -> DEFAULT_STRENGTH_MET
    }

    private fun isHeavyCompoundLift(normalizedExerciseId: String): Boolean =
        HEAVY_COMPOUND_LIFTS.contains(normalizedExerciseId)

    private fun epocCoefficientFor(normalizedExerciseId: String): Double =
        if (isHeavyCompoundLift(normalizedExerciseId)) EPOC_COEFFICIENT_COMPOUND else EPOC_COEFFICIENT_ISOLATION
}

private const val MET_WEIGHT_FACTOR = 0.0175
private const val DEFAULT_TUT_SECONDS_PER_REP = 4.5
private const val DEFAULT_CARDIO_MET = 6.0
private const val DEFAULT_STRENGTH_MET = 5.0
// Fallback when user body weight is missing or invalid; may skew estimates for users far from 70kg.
private const val DEFAULT_BODY_WEIGHT_KG = 70.0
// Minimum MET for heavy compounds, calibrated to hit ~25-40 kcal for 10 reps @ 95kg, 4.5s tempo, 70kg body weight.
private const val HEAVY_COMPOUND_BASE_MET = 7.5
// Higher k for heavy compounds to reflect stronger afterburn vs. isolation lifts (calibrated to target range above).
private const val EPOC_COEFFICIENT_COMPOUND = 0.4
// Lower k for isolation work to reduce afterburn influence (calibrated to target range above).
private const val EPOC_COEFFICIENT_ISOLATION = 0.2

private val HEAVY_COMPOUND_LIFTS = setOf(
    "deadlift",
    "squat",
    "bench_press",
)
