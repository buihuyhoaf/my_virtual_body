package com.hoabui.virtualbody3d.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Wine-plum brand (#8E3B46). Mature, editorial, calm. No dynamic color.

private val BrandPrimary = Color(0xFF8E3B46)
private val BrandPrimaryContainerLight = Color(0xFFF3D9DD)
private val BrandPrimaryContainerDark = Color(0xFF4A1F24)
private val BrandOnPrimary = Color(0xFFFFFFFF)
private val BrandOnPrimaryContainerLight = Color(0xFF4A1F24)
private val BrandOnPrimaryContainerDark = Color(0xFFF3D9DD)

private val LightBackground = Color(0xFFFAF5F6)
private val LightSurface = Color(0xFFFFFFFF)
private val LightSurfaceVariant = Color(0xFFF8EDEE)
private val LightOutline = Color(0xFFE6C7CB)
private val LightOnBackground = Color(0xFF2C1115)
private val LightOnSurface = Color(0xFF2C1115)
private val LightOnSurfaceVariant = Color(0xFF6A2B33)

private val DarkBackground = Color(0xFF1C0F12)
private val DarkSurface = Color(0xFF261418)
private val DarkSurfaceVariant = Color(0xFF301A1F)
private val DarkOutline = Color(0xFF4A2A2F)
private val DarkOnBackground = Color(0xFFFBEFF1)
private val DarkOnSurface = Color(0xFFFBEFF1)
private val DarkOnSurfaceVariant = Color(0xFFE0B9BE)

private val ErrorColor = Color(0xFFB3261E)

val FitnessLightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    primaryContainer = BrandPrimaryContainerLight,
    onPrimaryContainer = BrandOnPrimaryContainerLight,
    secondary = BrandPrimary,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    outline = LightOutline,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = ErrorColor,
    onError = BrandOnPrimary
)

val FitnessDarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    primaryContainer = BrandPrimaryContainerDark,
    onPrimaryContainer = BrandOnPrimaryContainerDark,
    secondary = BrandPrimary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    outline = DarkOutline,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = ErrorColor,
    onError = BrandOnPrimary
)

// Feature colors – wine-plum only. No orange, no brown text.
val BodyPrimary = BrandPrimary
val BodyPrimaryTint = Color(0x0F8E3B46)
val BodyPrimaryLight = Color(0x268E3B46)
val BodyPrimaryBorder = Color(0x338E3B46)
val BodyEmerald = BrandPrimaryContainerLight
val BodyEmeraldLight = Color(0x26F3D9DD)
val BodyAmber = BrandPrimary
val BodyAmberLight = Color(0x4D8E3B46)
val BodyRose = BrandPrimaryContainerDark
val BodyRoseLight = Color(0x4D4A1F24)
val BodySceneBackground = DarkBackground
val BodyBackgroundLight = LightBackground
val BodyBackgroundDark = DarkBackground
val BodyPreviewTrack = Color(0x0D2C1115)
val TopBarBackground = Color(0xCCFFFFFF)
val TopBarBorder = Color(0x80E6C7CB)
val ViewControlBackground = Color(0xE6FFFFFF)
val CardBackground = Color(0x80F8EDEE)
val PrimaryMetricColor = BrandPrimary
val MuscleMetricColor = BrandPrimary
val FatMetricColor = BrandPrimaryContainerDark
val GlassChipBackgroundStart = LightSurface
val GlassChipBackgroundEnd = LightSurface
val GlassChipBorder = Color(0x4DF1DADC)
val GlassChipIconBackground = Color(0x268E3B46)
val ScoreRingTrack = Color(0x332C1115)
val BmiNormalColor = BrandPrimary
val BmiOverColor = BrandPrimaryContainerDark
val BmiUnderColor = BrandPrimaryContainerLight
val FatGradientStart = BrandPrimaryContainerLight
val FatGradientEnd = BrandPrimaryContainerDark
val MuscleGradientEnd = BrandPrimary
