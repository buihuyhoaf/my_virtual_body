package com.hoabui.virtualbody3d.ui.theme.tokens.primitive

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Primitive color tokens for Rose Social Calm theme.
 * Raw brand values only; no semantic meaning at this layer.
 */
@Immutable
data class PrimitiveColorTokens(
    val rose500: Color,
    val rose400: Color,
    val rose100: Color,
    val neutral0: Color,
    val neutral50: Color,
    val neutral100: Color,
    val neutral200: Color,
    val neutral600: Color,
    val neutral900: Color,
    val mint400: Color
) {
    companion object {
        fun default(): PrimitiveColorTokens = PrimitiveColorTokens(
            rose500 = Color(0xFFA5B4FC),
            rose400 = Color(0xFF818CF8),
            rose100 = Color(0xFFE0E7FF),
            neutral0 = Color(0xFFFFFFFF),
            neutral50 = Color(0xFFFAFAFB),
            neutral100 = Color(0xFFF3F4F6),
            neutral200 = Color(0xFFE5E7EB),
            neutral600 = Color(0xFF6B7280),
            neutral900 = Color(0xFF111827),
            mint400 = Color(0xFF34D399)
        )
    }
}
