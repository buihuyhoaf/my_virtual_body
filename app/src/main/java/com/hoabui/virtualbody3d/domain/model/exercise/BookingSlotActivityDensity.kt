package com.hoabui.virtualbody3d.domain.model.exercise

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/**
 * Pure domain tier for slot visuals (no Android resources).
 * Ordered by increasing utilization for committed-work density.
 */
enum class SlotDensityTier {
    Empty,
    Light,
    Moderate,
    Heavy,
    OverCapacity,
}

/** Numeric summary for one grid row before localization / UI mapping. */
data class SlotDensityKernel(
    val slotStart: LocalTime,
    val totalPlannedMinutes: Int,
    val slotCapacityMinutes: Int,
    val utilizationRatio: Float,
    val overCapacity: Boolean,
    val densityTier: SlotDensityTier,
)

private const val SECONDS_PER_REP_HEURISTIC = 4

/**
 * Best-effort planned wall time for one schedule line (minutes, at least 1 when count > 0).
 */
fun estimatedPlannedMinutesForScheduleLine(schedule: WorkoutSchedule): Int {
    return when (schedule.measurementMode) {
        ExerciseMeasurementMode.Duration -> {
            val sec = schedule.durationSeconds?.takeIf { it > 0 } ?: return 0
            (sec + 59) / 60
        }
        ExerciseMeasurementMode.Strength -> {
            if (schedule.sets <= 0 || schedule.reps <= 0) return 0
            val workSeconds = schedule.sets * schedule.reps * SECONDS_PER_REP_HEURISTIC
            val restSeconds = schedule.sets * schedule.restSeconds.coerceAtLeast(0)
            val totalSec = workSeconds + restSeconds
            ((totalSec + 59) / 60).coerceAtLeast(1)
        }
    }
}

/** Same heuristic as [estimatedPlannedMinutesForScheduleLine] for an uncommitted line. */
fun estimatedPlannedMinutesForSessionLine(line: SessionExerciseLine): Int {
    return when (line.measurementMode) {
        ExerciseMeasurementMode.Duration -> {
            val sec = line.durationSeconds?.takeIf { it > 0 } ?: return 0
            (sec + 59) / 60
        }
        ExerciseMeasurementMode.Strength -> {
            if (line.sets <= 0 || line.reps <= 0) return 0
            val workSeconds = line.sets * line.reps * SECONDS_PER_REP_HEURISTIC
            val restSeconds = line.sets * line.restSeconds.coerceAtLeast(0)
            val totalSec = workSeconds + restSeconds
            ((totalSec + 59) / 60).coerceAtLeast(1)
        }
    }
}

private fun utilizationTier(
    ratio: Float,
    overCapacity: Boolean,
    totalPlannedMinutes: Int,
): SlotDensityTier = when {
    overCapacity -> SlotDensityTier.OverCapacity
    totalPlannedMinutes <= 0 -> SlotDensityTier.Empty
    ratio < 0.34f -> SlotDensityTier.Light
    ratio < 0.67f -> SlotDensityTier.Moderate
    else -> SlotDensityTier.Heavy
}

/**
 * Projects committed schedules into 30-minute slot buckets for [locationId] on [date].
 * In-flight cart overlay is applied only on [draftAnchorSlot] (typically the earliest selected slot).
 */
fun projectBookingSlotDensityKernels(
    date: LocalDate,
    zoneId: ZoneId,
    locationId: String,
    slotStarts: List<LocalTime>,
    schedules: List<WorkoutSchedule>,
    draftTotalMinutes: Int,
    draftAnchorSlot: LocalTime?,
): List<SlotDensityKernel> {
    val capacity = SESSION_BOOKING_SLOT_STEP_MINUTES.toInt()
    return slotStarts.map { slot ->
        val bucket = thirtyMinuteIntervalAtSlot(date, slot, zoneId)
        val inBucket = schedules.filter { sch ->
            if (sch.locationId != locationId) return@filter false
            val t = sch.scheduledAt.atZone(zoneId).toInstant()
            t >= bucket.start && t < bucket.end
        }
        var committedMinutes = 0
        for (s in inBucket) {
            committedMinutes += estimatedPlannedMinutesForScheduleLine(s)
        }
        val applyDraft = draftAnchorSlot != null && slot == draftAnchorSlot && draftTotalMinutes > 0
        val draftExtraMinutes = if (applyDraft) draftTotalMinutes else 0
        val totalPlannedMinutes = committedMinutes + draftExtraMinutes
        val ratio = if (capacity > 0) totalPlannedMinutes.toFloat() / capacity else 0f
        val over = totalPlannedMinutes > capacity
        SlotDensityKernel(
            slotStart = slot,
            totalPlannedMinutes = totalPlannedMinutes,
            slotCapacityMinutes = capacity,
            utilizationRatio = ratio,
            overCapacity = over,
            densityTier = utilizationTier(ratio, over, totalPlannedMinutes),
        )
    }
}
