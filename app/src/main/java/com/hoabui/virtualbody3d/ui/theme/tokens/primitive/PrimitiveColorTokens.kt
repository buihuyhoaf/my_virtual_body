package com.hoabui.virtualbody3d.ui.theme.tokens.primitive

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Primitive color tokens contain only raw brand values.
 * No semantic meaning should be attached at this layer.
 */
@Immutable
data class PrimitiveColorTokens(
    val green500: Color,
    val green300: Color,
    val gray950: Color,
    val gray900: Color,
    val gray800: Color,
    val gray600: Color,
    val gray200: Color,
    val white: Color,
    val red500: Color
) {
    companion object {
        fun default(): PrimitiveColorTokens = PrimitiveColorTokens(
            green500 = Color(0xFF7CFF6B),
            green300 = Color(0xFFB0FFA7),
            gray950 = Color(0xFF0A0C0A),
            gray900 = Color(0xFF121512),
            gray800 = Color(0xFF171C17),
            gray600 = Color(0xFF99A29A),
            gray200 = Color(0xFFDCE4DC),
            white = Color(0xFFFFFFFF),
            red500 = Color(0xFFFF6F7A)
        )
    }
}
