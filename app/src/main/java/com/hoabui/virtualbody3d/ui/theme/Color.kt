package com.hoabui.virtualbody3d.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Holistic Vitality – matches [PrimitiveColorTokens] (light: sage / sand / slate; dark: moss / muted sage / parchment).

private val SageGreen = Color(0xFF7DAA92)
private val WarmSand = Color(0xFFFDFCF8)
private val SlateGray = Color(0xFF4A5568)
private val DeepMoss = Color(0xFF1A2421)
private val MutedSage = Color(0xFF8ABBA3)
private val Parchment = Color(0xFFE2E2E2)
private val Terracotta = Color(0xFFE2725B)

private val BrandPrimaryLight = SageGreen
private val BrandPrimaryDark = MutedSage
private val BrandPrimaryContainerLight = Color(0xFFE5F0E9)
private val BrandPrimaryContainerDark = Color(0xFF6A9082)
private val BrandOnPrimary = Color(0xFFFFFFFF)
private val BrandOnPrimaryContainerLight = Color(0xFF2F4538)
private val BrandOnPrimaryContainerDark = Parchment

private val BrandSecondary = Terracotta
private val BrandOnSecondary = Color(0xFFFFFFFF)
private val BrandSecondaryContainerLight = Color(0xFFE2EDE5)
private val BrandSecondaryContainerDark = Color(0xFF2A3834)
private val BrandOnSecondaryContainerLight = Color(0xFF2D4136)
private val BrandOnSecondaryContainerDark = Parchment

private val LightBackground = WarmSand
private val LightSurface = WarmSand
private val LightSurfaceVariant = Color(0xFFF5F1EA)
private val LightOutline = Color(0xFFB8CDC4)
private val LightOnBackground = SlateGray
private val LightOnSurface = SlateGray
private val LightOnSurfaceVariant = Color(0xFF718096)

private val DarkBackground = DeepMoss
private val DarkSurface = DeepMoss
private val DarkSurfaceVariant = Color(0xFF222D2A)
private val DarkOutline = Color(0xFF4A5E56)
private val DarkOnBackground = Parchment
private val DarkOnSurface = Parchment
private val DarkOnSurfaceVariant = Color(0xFFBFC9C6)

private val ErrorColor = Color(0xFFB3261E)

val FitnessLightColorScheme = lightColorScheme(
    primary = BrandPrimaryLight,
    onPrimary = BrandOnPrimary,
    primaryContainer = BrandPrimaryContainerLight,
    onPrimaryContainer = BrandOnPrimaryContainerLight,
    secondary = BrandSecondary,
    onSecondary = BrandOnSecondary,
    secondaryContainer = BrandSecondaryContainerLight,
    onSecondaryContainer = BrandOnSecondaryContainerLight,
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
    primary = BrandPrimaryDark,
    onPrimary = BrandOnPrimary,
    primaryContainer = BrandPrimaryContainerDark,
    onPrimaryContainer = BrandOnPrimaryContainerDark,
    secondary = BrandSecondary,
    onSecondary = BrandOnSecondary,
    secondaryContainer = BrandSecondaryContainerDark,
    onSecondaryContainer = BrandOnSecondaryContainerDark,
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

// Feature colors – light brand reference (use tokens in Composables for dark-aware UI).
val BodyPrimary = BrandPrimaryLight
val BodyPrimaryTint = BrandPrimaryLight.copy(alpha = 0.06f)
val BodyPrimaryLight = BrandPrimaryLight.copy(alpha = 0.15f)
val BodyPrimaryBorder = BrandPrimaryLight.copy(alpha = 0.20f)
val BodyEmerald = BrandPrimaryContainerLight
val BodyEmeraldLight = BrandPrimaryContainerLight.copy(alpha = 0.15f)
val BodyAmber = BrandSecondary
val BodyAmberLight = BrandSecondary.copy(alpha = 0.30f)
val BodyRose = BrandPrimaryContainerDark
val BodyRoseLight = BrandPrimaryContainerDark.copy(alpha = 0.30f)
val BodySceneBackground = DarkBackground
val BodyBackgroundLight = LightBackground
val BodyBackgroundDark = DarkBackground
val BodyPreviewTrack = SlateGray.copy(alpha = 0.05f)
val TopBarBackground = Color(0xCCFDFCF8)
val TopBarBorder = BrandPrimaryLight.copy(alpha = 0.25f)
val ViewControlBackground = Color(0xE6FDFCF8)
val CardBackground = LightSurfaceVariant.copy(alpha = 0.50f)
val PrimaryMetricColor = BrandPrimaryLight
val MuscleMetricColor = BrandPrimaryLight
val FatMetricColor = BrandPrimaryContainerDark
val GlassChipBackgroundStart = LightSurface
val GlassChipBackgroundEnd = LightSurface
val GlassChipBorder = LightOutline.copy(alpha = 0.30f)
val GlassChipIconBackground = BrandPrimaryLight.copy(alpha = 0.15f)
val ScoreRingTrack = SlateGray.copy(alpha = 0.20f)
val BmiNormalColor = BrandPrimaryLight
val BmiOverColor = BrandPrimaryContainerDark
val BmiUnderColor = BrandPrimaryContainerLight
val FatGradientStart = BrandPrimaryContainerLight
val FatGradientEnd = BrandPrimaryContainerDark
val MuscleGradientEnd = BrandPrimaryLight
