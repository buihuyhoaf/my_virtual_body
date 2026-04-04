package com.hoabui.virtualbody3d.domain.model.exercise

import java.time.LocalTime

/** Default duration for a booked workout session when reserving consecutive 30-minute grid slots. */
const val SESSION_BOOKING_DURATION_MINUTES: Long = 60L

/** Grid step for time-slot selection in the booking UI. */
const val SESSION_BOOKING_SLOT_STEP_MINUTES: Long = 30L

/** Fallback location id for legacy rows that have no facility. */
const val DEFAULT_SESSION_LOCATION_ID: String = "default"

/** First 30-minute row shown in the booking grid (inclusive). */
val SESSION_BOOKING_GRID_FIRST_SLOT: LocalTime = LocalTime.of(5, 0)

/** Last 30-minute row shown in the booking grid (inclusive). */
val SESSION_BOOKING_GRID_LAST_SLOT: LocalTime = LocalTime.of(21, 30)

/** Wall-clock start of the "midday" period band in booking (first slot at or after this is in midday / afternoon split). */
val SESSION_BOOKING_PERIOD_MIDDAY_START: LocalTime = LocalTime.of(10, 0)

/** Wall-clock start of the afternoon/evening band (last period). */
val SESSION_BOOKING_PERIOD_AFTERNOON_START: LocalTime = LocalTime.of(15, 0)
