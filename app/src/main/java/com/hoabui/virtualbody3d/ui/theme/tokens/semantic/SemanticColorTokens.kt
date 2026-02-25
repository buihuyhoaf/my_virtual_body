package com.hoabui.virtualbody3d.ui.theme.tokens.semantic

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveColorTokens

/**
 * Semantic colors for Rose Social Calm theme.
 * Maps primitive brand values to UI meaning.
 * Legacy names (surfaceBorder, outlineSoft, dashboard*, etc.) are aliased to new semantics for UI compatibility.
 */
@Immutable
data class SemanticColorTokens(
    val primary: Color,
    val primarySoft: Color,
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfaceSubtle: Color,
    val borderSubtle: Color,
    val borderStrong: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val error: Color,
    val onPrimary: Color,
    val onError: Color,
    val surfaceBorder: Color,
    val outlineSoft: Color,
    val surfaceOverlay: Color,
    val backgroundTransparent: Color,
    val backgroundScrim: Color,
    val previewTrack: Color,
    val primarySelected: Color,
    val dashboardPanelBackground: Color,
    val dashboardHandle: Color,
    val dashboardNutritionCardBackground: Color,
    val dashboardNutritionCardBorder: Color,
    val dashboardRingTrack: Color,
    val dashboardMealCardBackground: Color,
    val dashboardMealImageBackground: Color,
    val dashboardSummaryCardBackground: Color,
    val dashboardFloatingNavBackground: Color,
    val dashboardFloatingNavBorder: Color,
    val calendarYearBackground: Color,
    val calendarSelectedBorder: Color
)

fun lightSemanticColors(primitive: PrimitiveColorTokens): SemanticColorTokens {
    val borderSubtle = primitive.neutral200
    val surface = primitive.neutral0
    val surfaceSubtle = primitive.neutral100
    val background = primitive.neutral50
    val primarySoft = primitive.rose100
    return SemanticColorTokens(
        primary = primitive.rose500,
        primarySoft = primarySoft,
        background = background,
        surface = surface,
        surfaceElevated = surface,
        surfaceSubtle = surfaceSubtle,
        borderSubtle = borderSubtle,
        borderStrong = primitive.neutral600,
        textPrimary = primitive.neutral900,
        textSecondary = primitive.neutral600,
        textMuted = primitive.neutral600.copy(alpha = 0.7f),
        error = Color(0xFFEF4444),
        onPrimary = primitive.neutral0,
        onError = primitive.neutral0,
        surfaceBorder = borderSubtle,
        outlineSoft = borderSubtle,
        surfaceOverlay = surface,
        backgroundTransparent = Color.Transparent,
        backgroundScrim = primitive.neutral900.copy(alpha = 0.16f),
        previewTrack = surfaceSubtle,
        primarySelected = primarySoft,
        dashboardPanelBackground = surface,
        dashboardHandle = primitive.neutral600.copy(alpha = 0.40f),
        dashboardNutritionCardBackground = surface,
        dashboardNutritionCardBorder = borderSubtle,
        dashboardRingTrack = primitive.neutral900.copy(alpha = 0.14f),
        dashboardMealCardBackground = surface,
        dashboardMealImageBackground = surfaceSubtle,
        dashboardSummaryCardBackground = surface,
        dashboardFloatingNavBackground = surface,
        dashboardFloatingNavBorder = borderSubtle,
        calendarYearBackground = primarySoft.copy(alpha = 0.55f),
        calendarSelectedBorder = primitive.rose500
    )
}

fun darkSemanticColors(primitive: PrimitiveColorTokens): SemanticColorTokens {
    val borderSubtle = primitive.neutral600.copy(alpha = 0.30f)
    val borderStrong = primitive.neutral200
    val surface = primitive.neutral900
    val surfaceSubtle = primitive.neutral600.copy(alpha = 0.16f)
    val background = primitive.neutral900
    val primarySoft = primitive.rose100.copy(alpha = 0.24f)
    return SemanticColorTokens(
        primary = primitive.rose500,
        primarySoft = primarySoft,
        background = background,
        surface = surface,
        surfaceElevated = surface,
        surfaceSubtle = surfaceSubtle,
        borderSubtle = borderSubtle,
        borderStrong = borderStrong,
        textPrimary = primitive.neutral0,
        textSecondary = primitive.neutral200,
        textMuted = primitive.neutral200.copy(alpha = 0.7f),
        error = Color(0xFFEF4444),
        onPrimary = primitive.neutral0,
        onError = primitive.neutral0,
        surfaceBorder = borderSubtle,
        outlineSoft = borderSubtle,
        surfaceOverlay = primitive.neutral0.copy(alpha = 0.80f),
        backgroundTransparent = Color.Transparent,
        backgroundScrim = primitive.neutral900.copy(alpha = 0.24f),
        previewTrack = primitive.neutral0.copy(alpha = 0.08f),
        primarySelected = primarySoft,
        dashboardPanelBackground = surface,
        dashboardHandle = primitive.neutral600.copy(alpha = 0.50f),
        dashboardNutritionCardBackground = surface,
        dashboardNutritionCardBorder = primitive.neutral0.copy(alpha = 0.12f),
        dashboardRingTrack = primitive.neutral0.copy(alpha = 0.14f),
        dashboardMealCardBackground = surface,
        dashboardMealImageBackground = surface,
        dashboardSummaryCardBackground = surface,
        dashboardFloatingNavBackground = surface,
        dashboardFloatingNavBorder = primitive.neutral0.copy(alpha = 0.14f),
        calendarYearBackground = primarySoft.copy(alpha = 0.55f),
        calendarSelectedBorder = primitive.rose500
    )
}
