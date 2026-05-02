package com.hoabui.virtualbody3d.ui.exerciselibrary.util

import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime

/** Epoch millis at start of today in the system default zone (default booking date). */
fun defaultExerciseLibraryCartDateMillis(): Long {
    val systemZone = Clock.systemDefaultZone().zone
    return LocalDate.now().atStartOfDay(systemZone).toInstant().toEpochMilli()
}

fun defaultExerciseLibraryCartTime(): LocalTime {
    return LocalTime.now()
}
