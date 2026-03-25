package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    val panelBorder: Color,
    val selectedBorderWidth: Dp,
    val todayBorderWidth: Dp,
    val dayBadgeOuterPadding: Dp,
    val dayBadgeHorizontalPadding: Dp,
    val dayBadgeVerticalPadding: Dp,
    val dayItemImageSize: Dp,
    val dayItemMetaSpacing: Dp,
    val panelBottomPadding: Dp,
    val panelOffsetY: Dp,
    val panelBorderWidth: Dp
)

fun gymCalendarTokens(colors: SemanticColorTokens): CalendarTokens = CalendarTokens(
    yearTextColor = colors.textPrimary,
    monthTextColor = colors.textSecondary,
    monthDividerColor = colors.borderSubtle,
    todayBorderColor = colors.borderSubtle,
    selectedDayBackground = colors.primarySoft,
    selectedBorderColor = colors.calendarSelectedBorder,
    panelBorder = colors.borderSubtle,
    selectedBorderWidth = 1.5.dp,
    todayBorderWidth = 1.dp,
    dayBadgeOuterPadding = 6.dp,
    dayBadgeHorizontalPadding = 6.dp,
    dayBadgeVerticalPadding = 2.dp,
    dayItemImageSize = 34.dp,
    dayItemMetaSpacing = 2.dp,
    panelBottomPadding = 14.dp,
    panelOffsetY = (-12).dp,
    panelBorderWidth = 1.dp
)
