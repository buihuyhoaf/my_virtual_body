package com.hoabui.virtualbody3d.data.model

/**
 * Snapshot tiến độ cơ thể từ API / local (data layer).
 * [dateIso] dạng ISO-8601 ngày: "2025-03-01".
 */
data class ProgressSnapshotDto(
    val dateIso: String,
    val imageUrl: String?,
    val weightKg: Float?,
    val bodyFatPercent: Float?,
    val muscleMassKg: Float?,
)
