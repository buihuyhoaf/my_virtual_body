package com.hoabui.virtualbody3d.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Core semantic tokens for a dark-first fitness palette.
private val FitnessAccent = Color(0xFF7CFF6B)
private val FitnessOnAccent = Color(0xFF072002)
private val FitnessSecondary = Color(0xFF4BE39D)
private val FitnessDarkBackground = Color(0xFF0A0C0A)
private val FitnessDarkSurface = Color(0xFF171A17)
private val FitnessDarkOnBackground = Color(0xFFE7ECE6)
private val FitnessDarkOnSurface = Color(0xFFE3E8E2)
private val FitnessDarkError = Color(0xFFFF7A85)

private val FitnessLightPrimary = Color(0xFF146A26)
private val FitnessLightOnPrimary = Color(0xFFFFFFFF)
private val FitnessLightSecondary = Color(0xFF2F7D56)
private val FitnessLightBackground = Color(0xFFF7FBF5)
private val FitnessLightSurface = Color(0xFFFFFFFF)
private val FitnessLightOnBackground = Color(0xFF121512)
private val FitnessLightOnSurface = Color(0xFF171C17)
private val FitnessLightError = Color(0xFFB42318)

val FitnessDarkColorScheme = darkColorScheme(
    primary = FitnessAccent,
    onPrimary = FitnessOnAccent,
    secondary = FitnessSecondary,
    background = FitnessDarkBackground,
    surface = FitnessDarkSurface,
    onBackground = FitnessDarkOnBackground,
    onSurface = FitnessDarkOnSurface,
    error = FitnessDarkError
)

val FitnessLightColorScheme = lightColorScheme(
    primary = FitnessLightPrimary,
    onPrimary = FitnessLightOnPrimary,
    secondary = FitnessLightSecondary,
    background = FitnessLightBackground,
    surface = FitnessLightSurface,
    onBackground = FitnessLightOnBackground,
    onSurface = FitnessLightOnSurface,
    error = FitnessLightError
)

// Feature colors kept for existing screens.
val BodyPrimary = Color(0xFF2B7CEE)
val BodyPrimaryTint = Color(0x0F2B7CEE)
val BodyPrimaryLight = Color(0x262B7CEE)
val BodyPrimaryBorder = Color(0x332B7CEE)
val BodyEmerald = Color(0xFF10B981)
val BodyEmeraldLight = Color(0x2610B981)
val BodyAmber = Color(0xFFF59E0B)
val BodyAmberLight = Color(0x4DF59E0B)
val BodyRose = Color(0xFFF43F5E)
val BodyRoseLight = Color(0x4DF43F5E)
val BodySceneBackground = Color(0xFF1A2332)
val BodyBackgroundLight = Color(0xFFF6F7F8)
val BodyBackgroundDark = Color(0xFF101822)
val BodyPreviewTrack = Color(0x0D000000)
val TopBarBackground = Color(0xCCFFFBFE)
val TopBarBorder = Color(0x80A1A1A1)
val ViewControlBackground = Color(0xE6FFFBFE)
val CardBackground = Color(0x80E7E0EC)
val PrimaryMetricColor = Color(0xFF2B7CEE)
val MuscleMetricColor = Color(0xFF10B981)
val FatMetricColor = Color(0xFFF43F5E)
val GlassChipBackgroundStart = Color(0xFFFFFFFF)
val GlassChipBackgroundEnd = Color(0xFFFFFFFF)
val GlassChipBorder = Color(0x4DFFFFFF)
val GlassChipIconBackground = Color(0x262B7CEE)
val ScoreRingTrack = Color(0x66E2E8F0)
val BmiNormalColor = Color(0xFF22C55E)
val BmiOverColor = Color(0xFFFB923C)
val BmiUnderColor = Color(0xFF7DD3FC)
val FatGradientStart = Color(0xFF60A5FA)
val FatGradientEnd = Color(0xFF22D3EE)
val MuscleGradientEnd = Color(0xFF6366F1)