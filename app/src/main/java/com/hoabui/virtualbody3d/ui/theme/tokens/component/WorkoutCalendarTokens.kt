package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.ElevationTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveBorderTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveRadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

/**
 * Spacing, rhythm, and shared calendar chrome for Workout Calendar (8/16dp baseline via spacing scale).
 *
 * Horizontal section insets use [sectionSurfacePaddingVertical] only so in-card text aligns with the
 * screen gutter ([screenHorizontalPadding]); extra horizontal padding was removed to unify the reading path.
 */
@Immutable
data class WorkoutCalendarTokens(
    /** Outer [Column] padding on the calendar success screen. */
    val screenHorizontalPadding: Dp,
    val screenVerticalPadding: Dp,
    /** Between stacked sections (month grid → exercise list). */
    val sectionGapMajor: Dp,
    /**
     * inner horizontal inset for planning section surfaces — [spacing.none] so labels align to the global gutter.
     */
    val sectionSurfacePaddingHorizontal: Dp,
    /** Vertical-only breathability inside month/week surfaces. */
    val sectionSurfacePaddingVertical: Dp,
    /** Gap between weekday letter row and month day grid. */
    val sectionTitleToContentGap: Dp,
    /** Vertical padding above/below unified section titles to avoid clipping ascenders/descenders. */
    val sectionTitleSafePaddingVertical: Dp,
    /** Corner radius for month grid day cells. */
    val dayCellCornerRadius: Dp,
    /** Minimum square size for month grid day cells (touch + dot row). */
    val monthGridCellMinSize: Dp,
    /** Month grid gaps (horizontal + vertical between cells). */
    val monthGridCellGap: Dp,
    /** Selected day ring (month grid). */
    val daySelectedBorderWidth: Dp,
    /** Today ring when not selected. */
    val dayTodayBorderWidth: Dp,
    /** Space below the date header before the first exercise card. */
    val exerciseListHeaderToListGap: Dp,
    /** Vertical gap between modular exercise items in the list. */
    val exerciseItemListGap: Dp,
    /** Inner padding inside each exercise [GCard] row. */
    val exerciseItemInnerPadding: Dp,
    val exerciseRowTitleToMetricsGap: Dp,
    val exerciseRowMetricsToStatusGap: Dp,
    val sectionSurfaceElevation: Dp,
)

fun gymWorkoutCalendarTokens(
    spacing: PrimitiveSpacingTokens,
    radius: PrimitiveRadiusTokens,
    border: PrimitiveBorderTokens,
    elevation: ElevationTokens,
): WorkoutCalendarTokens {
    val cell = spacing.xxl
    val gridGap = spacing.xxs
    return WorkoutCalendarTokens(
        screenHorizontalPadding = spacing.md,
        screenVerticalPadding = spacing.sm,
        sectionGapMajor = spacing.lg,
        sectionSurfacePaddingHorizontal = spacing.none,
        sectionSurfacePaddingVertical = spacing.sm,
        sectionTitleToContentGap = spacing.xs,
        sectionTitleSafePaddingVertical = spacing.xs,
        dayCellCornerRadius = radius.md,
        monthGridCellMinSize = cell,
        monthGridCellGap = gridGap,
        daySelectedBorderWidth = border.thin,
        dayTodayBorderWidth = border.hairline,
        exerciseListHeaderToListGap = spacing.md,
        exerciseItemListGap = spacing.sm,
        exerciseItemInnerPadding = spacing.md,
        exerciseRowTitleToMetricsGap = spacing.xxxs,
        exerciseRowMetricsToStatusGap = spacing.xxs,
        sectionSurfaceElevation = elevation.level0,
    )
}
