package com.hoabui.virtualbody3d.ui.theme.tokens.semantic

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hoabui.virtualbody3d.ui.theme.font.PlusJakartaSansFamily
import com.hoabui.virtualbody3d.ui.theme.font.ReadexProFamily

/**
 * Holistic Vitality typography: Plus Jakarta Sans Bold for display, headline, and [titleMedium];
 * Readex Pro Medium for titles, body, and labels (Med-Tech weighted rhythm).
 */
@Immutable
data class SemanticTypographyTokens(
    val material: Typography
)

fun gymTypographyTokens(): SemanticTypographyTokens = SemanticTypographyTokens(
    material = Typography(
        displayLarge = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = (-0.15).sp
        ),
        displayMedium = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = (-0.12).sp
        ),
        displaySmall = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = (-0.08).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 30.sp,
            letterSpacing = (-0.06).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            letterSpacing = (-0.03).sp
        ),
        headlineSmall = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 26.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = ReadexProFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            lineHeight = 26.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.1.sp
        ),
        titleSmall = TextStyle(
            fontFamily = ReadexProFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = ReadexProFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.17.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = ReadexProFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.17.sp
        ),
        bodySmall = TextStyle(
            fontFamily = ReadexProFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.22.sp
        ),
        labelLarge = TextStyle(
            fontFamily = ReadexProFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.23.sp
        ),
        labelMedium = TextStyle(
            fontFamily = ReadexProFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.33.sp
        ),
        labelSmall = TextStyle(
            fontFamily = ReadexProFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.33.sp
        )
    )
)
