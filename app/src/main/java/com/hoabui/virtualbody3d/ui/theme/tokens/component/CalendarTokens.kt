package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens

/**
 * Calendar screen tokens for Rose Social Soft vibe.
 * Used by CalendarScreen for year, month, day cell, and detail panel styling.
 */
@Immutable
data class CalendarTokens(
    val yearTextColor: Color,
    val monthTextColor: Color,
    val monthDividerColor: Color,
    val todayBorderColor: Color,
    val selectedDayBackground: Color,
    val selectedBorderColor: Color,
    val panelBorder: Color
)

fun gymCalendarTokens(colors: SemanticColorTokens): CalendarTokens = CalendarTokens(
    yearTextColor = colors.textPrimary,
    monthTextColor = colors.textSecondary,
    monthDividerColor = colors.borderSubtle,
    todayBorderColor = colors.borderSubtle,
    selectedDayBackground = colors.primarySoft,
    selectedBorderColor = colors.calendarSelectedBorder,
    panelBorder = colors.borderSubtle
)
