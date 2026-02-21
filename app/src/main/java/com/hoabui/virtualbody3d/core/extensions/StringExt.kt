package com.hoabui.virtualbody3d.core.extensions

fun String.formatPercent(): String {
    val trimmed = trim()
    if (trimmed.isBlank()) return ""
    return if (trimmed.endsWith("%")) trimmed else "$trimmed%"
}

fun String.formatMeasurement(unit: String): String {
    val trimmedValue = trim()
    val trimmedUnit = unit.trim()
    return listOf(trimmedValue, trimmedUnit).filter { it.isNotEmpty() }.joinToString(" ")
}
