package com.hoabui.virtualbody3d.ui.theme.font

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.hoabui.virtualbody3d.R

private val GooglePlayServicesFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private fun googleFontFamily(
    name: String,
    weights: List<FontWeight>
): FontFamily = FontFamily(
    weights.map { weight ->
        Font(
            googleFont = GoogleFont(name),
            fontProvider = GooglePlayServicesFontProvider,
            weight = weight
        )
    }
)

/**
 * Plus Jakarta Sans via Google Fonts (downloadable). Use in [com.hoabui.virtualbody3.ui.theme.tokens.semantic.gymTypographyTokens] or other typography.
 * Requires Google Play services on device for first fetch.
 */
val PlusJakartaSansFamily: FontFamily = googleFontFamily(
    name = "Plus Jakarta Sans",
    weights = listOf(
        FontWeight.Normal,
        FontWeight.Medium,
        FontWeight.SemiBold,
        FontWeight.Bold
    )
)

/**
 * Readex Pro via Google Fonts (downloadable). Use in typography tokens for secondary / display styling.
 */
val ReadexProFamily: FontFamily = googleFontFamily(
    name = "Readex Pro",
    weights = listOf(
        FontWeight.Normal,
        FontWeight.Medium,
        FontWeight.SemiBold,
        FontWeight.Bold
    )
)
