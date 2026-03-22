package com.hoabui.virtualbody3d.domain.model.body

import java.time.LocalDate

/**
 * Một điểm đo / snapshot trên timeline tiến độ (domain layer).
 */
data class ProgressSnapshot(
    val recordedOn: LocalDate,
    val imageUrl: String?,
    val weightKg: Float?,
    val bodyFatPercent: Float?,
    val muscleMassKg: Float?,
)
