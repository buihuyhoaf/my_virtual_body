package com.hoabui.virtualbody3d.ui.theme.tokens.primitive

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Primitive color tokens – wine-plum brand (#8E3B46). Mature, editorial, calm. No dynamic color.
 */
@Immutable
data class PrimitiveColorTokens(
    val primary: Color,
    val primaryContainerLight: Color,
    val primaryContainerDark: Color,
    val onPrimary: Color,
    val onPrimaryContainerLight: Color,
    val onPrimaryContainerDark: Color,
    val backgroundLight: Color,
    val surfaceLight: Color,
    val surfaceVariantLight: Color,
    val outlineLight: Color,
    val borderSubtleLight: Color,
    val textPrimaryLight: Color,
    val textSecondaryLight: Color,
    val backgroundDark: Color,
    val surfaceDark: Color,
    val surfaceVariantDark: Color,
    val outlineDark: Color,
    val borderSubtleDark: Color,
    val textPrimaryDark: Color,
    val textSecondaryDark: Color,
    val error: Color,
    val transparent: Color,
    val splashCard: Color
) {
    companion object {
        fun default(): PrimitiveColorTokens = PrimitiveColorTokens(
            primary = Color(0xFF8E3B46),
            primaryContainerLight = Color(0xFFF3D9DD),
            primaryContainerDark = Color(0xFF4A1F24),
            onPrimary = Color(0xFFFFFFFF),
            onPrimaryContainerLight = Color(0xFF4A1F24),
            onPrimaryContainerDark = Color(0xFFF3D9DD),
            backgroundLight = Color(0xFFFFFFFF),
            surfaceLight = Color(0xFFFFFFFF),
            surfaceVariantLight = Color(0xFFF8EDEE),
            outlineLight = Color(0xFFE6C7CB),
            borderSubtleLight = Color.Black,
            textPrimaryLight = Color.Black,
            textSecondaryLight = Color(0xFF6A2B33),
            backgroundDark = Color(0xFF1C0F12),
            surfaceDark = Color(0xFF261418),
            surfaceVariantDark = Color(0xFF301A1F),
            outlineDark = Color(0xFF4A2A2F),
            borderSubtleDark = Color(0xFF3A1F23),
            textPrimaryDark = Color(0xFFFBEFF1),
            textSecondaryDark = Color(0xFFE0B9BE),
            error = Color(0xFFB3261E),
            transparent = Color.Transparent,
            splashCard = Color(0xFF5C1F2A)
        )
    }
}
