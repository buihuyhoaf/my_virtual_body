package com.hoabui.virtualbody3d.data.local.db

import java.time.LocalDate

/**
 * Logcat-friendly formatting for workout DB tracing ([WORKOUT_DB_TRACE_LOG_TAG]).
 * Uses ISO-8601 local dates for epoch-day values (not millis).
 */
const val WORKOUT_DB_TRACE_LOG_TAG = "WorkoutDbTrace"

fun formatEpochDayForLog(epochDay: Long): String =
    "$epochDay [${LocalDate.ofEpochDay(epochDay)}]"

fun formatEpochDayRangeForLog(startDay: Long, endDay: Long): String =
    "${formatEpochDayForLog(startDay)}..${formatEpochDayForLog(endDay)}"
