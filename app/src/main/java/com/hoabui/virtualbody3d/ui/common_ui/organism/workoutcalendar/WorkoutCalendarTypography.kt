package com.hoabui.virtualbody3d.ui.common_ui.organism.workoutcalendar

import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken

/** Shared by "Tuần này" and the formatted selected date header (identical size, weight, line metrics). */
internal fun workoutCalendarUnifiedSectionTitleStyle(token: GymToken): TextStyle =
    token.typography.labelLarge.merge(
        TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
    )

internal fun workoutCalendarMonthTitleStyle(token: GymToken): TextStyle =
    token.typography.titleLarge.merge(
        TextStyle(
            fontWeight = FontWeight.Bold,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
        ),
    )

internal fun workoutCalendarExerciseNameStyle(token: GymToken): TextStyle =
    token.typography.titleMedium.merge(
        TextStyle(
            fontWeight = FontWeight.Bold,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
        ),
    )

internal fun workoutCalendarStripTextStyle(base: TextStyle): TextStyle =
    base.merge(TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)))

internal fun workoutCalendarSupportingBodyStyle(token: GymToken): TextStyle =
    token.typography.bodySmall.merge(
        TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
    )

internal fun workoutCalendarSupportingLabelStyle(token: GymToken): TextStyle =
    token.typography.labelSmall.merge(
        TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
    )
