package com.hoabui.virtualbody3d.domain.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val ISO_DAY_KEY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

fun Instant.toIsoDayKey(zoneId: ZoneId = ZoneId.systemDefault()): String =
    atZone(zoneId).toLocalDate().toIsoDayKey()

fun Long.toIsoDayKey(zoneId: ZoneId = ZoneId.systemDefault()): String =
    Instant.ofEpochMilli(this).toIsoDayKey(zoneId)

fun LocalDate.toIsoDayKey(): String = format(ISO_DAY_KEY_FORMATTER)
