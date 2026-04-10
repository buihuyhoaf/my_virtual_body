package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/** Epoch millis at start of today in the system default zone (default booking date). */
fun defaultExerciseLibraryCartDateMillis(): Long {
    val zone = ZoneId.systemDefault()
    return LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli()
}

fun defaultExerciseLibraryCartTime(): LocalTime {
    val zone = ZoneId.systemDefault()
    return LocalTime.now(zone)
}
