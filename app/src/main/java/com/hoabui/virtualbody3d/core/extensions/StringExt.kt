package com.hoabui.virtualbody3d.core.extensions


fun String.formatMeasurement(unit: String): String {
    val trimmed = trim()
    if (trimmed.isBlank()) return ""
    return if (trimmed.endsWith(unit)) trimmed else "$trimmed $unit"
}
