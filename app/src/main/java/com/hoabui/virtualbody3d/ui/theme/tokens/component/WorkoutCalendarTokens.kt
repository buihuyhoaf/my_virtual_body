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
    /** Leading thumbnail in day exercise list (aligned with library row scale). */
    val exerciseRowThumbnailSize: Dp,
    val exerciseRowThumbnailToTextGap: Dp,
    /** Horizontal width of revealed delete track (anchored swipe). */
    val swipeDeleteTrackWidth: Dp,
    /** Icon size on delete underlay (primitive icon scale). */
    val swipeDeleteIconSize: Dp,
    /** Gap between delete icon and label on underlay. */
    val swipeDeleteIconLabelGap: Dp,
    /** Fraction of [swipeDeleteTrackWidth] for one-time list nudge (~peek). */
    val swipeDeleteNudgeFraction: Float,
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
        exerciseRowThumbnailSize = spacing.xxl,
        exerciseRowThumbnailToTextGap = spacing.sm,
        swipeDeleteTrackWidth = spacing.xxxl,
        swipeDeleteIconSize = spacing.iconMedium,
        swipeDeleteIconLabelGap = spacing.xs,
        swipeDeleteNudgeFraction = 0.28f,
        sectionSurfaceElevation = elevation.level0,
    )
}
