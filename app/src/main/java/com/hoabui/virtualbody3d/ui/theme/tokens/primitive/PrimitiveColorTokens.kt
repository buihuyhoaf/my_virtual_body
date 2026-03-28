package com.hoabui.virtualbody3d.ui.theme.tokens.primitive

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Primitive color tokens – Holistic Vitality (light + dark):
 * Light: SageGreen #7DAA92, WarmSand #FDFCF8, SlateGray #4A5568.
 * Dark: DeepMoss #1A2421, MutedSage #8ABBA3, Parchment #E2E2E2.
 */
@Immutable
data class PrimitiveColorTokens(
    /** Light-theme brand primary (SageGreen). */
    val primary: Color,
    /** Dark-theme brand primary (MutedSage). */
    val primaryDark: Color,
    val primaryContainerLight: Color,
    val primaryContainerDark: Color,
    val onPrimary: Color,
    val onPrimaryContainerLight: Color,
    val onPrimaryContainerDark: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainerLight: Color,
    val secondaryContainerDark: Color,
    val onSecondaryContainerLight: Color,
    val onSecondaryContainerDark: Color,
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
        private val SageGreen = Color(0xFF7DAA92)
        private val WarmSand = Color(0xFFFDFCF8)
        private val SlateGray = Color(0xFF4A5568)
        private val DeepMoss = Color(0xFF1A2421)
        private val MutedSage = Color(0xFF8ABBA3)
        private val Parchment = Color(0xFFE2E2E2)
        private val Terracotta = Color(0xFFE2725B)

        fun default(): PrimitiveColorTokens = PrimitiveColorTokens(
            primary = SageGreen,
            primaryDark = MutedSage,
            primaryContainerLight = Color(0xFFE5F0E9),
            primaryContainerDark = Color(0xFF6A9082),
            onPrimary = Color(0xFFFFFFFF),
            onPrimaryContainerLight = Color(0xFF2F4538),
            onPrimaryContainerDark = Parchment,
            secondary = Terracotta,
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainerLight = Color(0xFFE2EDE5),
            secondaryContainerDark = Color(0xFF2A3834),
            onSecondaryContainerLight = Color(0xFF2D4136),
            onSecondaryContainerDark = Parchment,
            backgroundLight = WarmSand,
            surfaceLight = WarmSand,
            surfaceVariantLight = Color(0xFFF5F1EA),
            outlineLight = Color(0xFFB8CDC4),
            borderSubtleLight = SlateGray.copy(alpha = 0.12f),
            textPrimaryLight = SlateGray,
            textSecondaryLight = Color(0xFF718096),
            backgroundDark = DeepMoss,
            surfaceDark = DeepMoss,
            surfaceVariantDark = Color(0xFF222D2A),
            outlineDark = Color(0xFF4A5E56),
            borderSubtleDark = Parchment.copy(alpha = 0.14f),
            textPrimaryDark = Parchment,
            textSecondaryDark = Color(0xFFBFC9C6),
            error = Color(0xFFB3261E),
            transparent = Color.Transparent,
            splashCard = Color(0xFFC45E4A)
        )
    }
}
