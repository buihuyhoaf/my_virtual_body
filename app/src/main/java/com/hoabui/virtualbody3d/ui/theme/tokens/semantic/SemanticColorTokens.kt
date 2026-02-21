package com.hoabui.virtualbody3d.ui.theme.tokens.semantic

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveColorTokens

/**
 * Semantic colors translate primitive brand values into UI meaning.
 */
@Immutable
data class SemanticColorTokens(
    val primary: Color,
    val background: Color,
    val surface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val error: Color,
    val primarySoft: Color,
    val primarySelected: Color,
    val backgroundTransparent: Color,
    val backgroundScrim: Color,
    val surfaceOverlay: Color,
    val surfaceBorder: Color,
    val outlineSoft: Color,
    val previewTrack: Color
)

fun darkSemanticColors(primitive: PrimitiveColorTokens): SemanticColorTokens = SemanticColorTokens(
    primary = primitive.green500,
    background = primitive.gray950,
    surface = primitive.gray900,
    textPrimary = primitive.white,
    textSecondary = primitive.gray600,
    error = primitive.red500,
    primarySoft = primitive.green500.copy(alpha = 0.10f),
    primarySelected = primitive.green500.copy(alpha = 0.16f),
    backgroundTransparent = Color.Transparent,
    backgroundScrim = primitive.gray950.copy(alpha = 0.24f),
    surfaceOverlay = primitive.white.copy(alpha = 0.80f),
    surfaceBorder = primitive.white.copy(alpha = 0.20f),
    outlineSoft = primitive.gray600.copy(alpha = 0.50f),
    previewTrack = primitive.white.copy(alpha = 0.08f)
)

// Light semantic mapping is intentionally basic and ready for expansion.
fun lightSemanticColors(primitive: PrimitiveColorTokens): SemanticColorTokens = SemanticColorTokens(
    primary = primitive.green500,
    background = primitive.white,
    surface = primitive.gray200,
    textPrimary = primitive.gray900,
    textSecondary = primitive.gray600,
    error = primitive.red500,
    primarySoft = primitive.green500.copy(alpha = 0.10f),
    primarySelected = primitive.green500.copy(alpha = 0.16f),
    backgroundTransparent = Color.Transparent,
    backgroundScrim = primitive.gray900.copy(alpha = 0.16f),
    surfaceOverlay = primitive.white,
    surfaceBorder = primitive.gray600.copy(alpha = 0.20f),
    outlineSoft = primitive.gray600.copy(alpha = 0.40f),
    previewTrack = primitive.gray900.copy(alpha = 0.08f)
)
