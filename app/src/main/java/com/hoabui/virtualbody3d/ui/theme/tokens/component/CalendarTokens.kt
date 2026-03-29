package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveBorderTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.gymCalendarLayoutSemantics

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

fun gymCalendarTokens(
    colors: SemanticColorTokens,
    spacing: PrimitiveSpacingTokens,
    border: PrimitiveBorderTokens,
): CalendarTokens {
    val layout = gymCalendarLayoutSemantics()
    return CalendarTokens(
        yearTextColor = colors.textPrimary,
        monthTextColor = colors.textSecondary,
        monthDividerColor = colors.borderSubtle,
        todayBorderColor = colors.borderSubtle,
        selectedDayBackground = colors.primarySoft,
        selectedBorderColor = colors.calendarSelectedBorder,
        panelBorder = colors.borderSubtle,
        selectedBorderWidth = border.thin,
        todayBorderWidth = border.hairline,
        dayBadgeOuterPadding = spacing.xxs + spacing.xxxs,
        dayBadgeHorizontalPadding = spacing.xxs + spacing.xxxs,
        dayBadgeVerticalPadding = spacing.xxxs,
        dayItemImageSize = layout.dayItemImageSize,
        dayItemMetaSpacing = spacing.xxxs,
        panelBottomPadding = layout.panelBottomPadding,
        panelOffsetY = layout.panelOffsetY,
        panelBorderWidth = border.hairline
    )
}
