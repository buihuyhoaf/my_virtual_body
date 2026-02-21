package com.hoabui.virtualbody3d.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken
import com.hoabui.virtualbody3d.ui.theme.tokens.LocalGymToken
import com.hoabui.virtualbody3d.ui.theme.tokens.darkGymToken
import com.hoabui.virtualbody3d.ui.theme.tokens.lightGymToken

/**
 * Theme host for GymToken: provides Material3 and token access in one place.
 */
@Composable
fun GymTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    tokenOverride: GymToken? = null,
    content: @Composable () -> Unit
) {
    val token = tokenOverride ?: if (darkTheme) darkGymToken() else lightGymToken()

    CompositionLocalProvider(LocalGymToken provides token) {
        MaterialTheme(
            colorScheme = token.colors.toMaterialColorScheme(darkTheme),
            typography = token.typography,
            shapes = Shapes(
                small = RoundedCornerShape(token.radius.sm),
                medium = RoundedCornerShape(token.radius.md),
                large = RoundedCornerShape(token.radius.lg)
            ),
            content = content
        )
    }
}

/**
 * API for UI consumption: GymTheme.token.spacing.md / GymTheme.token.colors.primary.
 */
object GymTheme {
    val token: GymToken
        @Composable
        @ReadOnlyComposable
        get() = LocalGymToken.current
}

private fun com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens.toMaterialColorScheme(
    isDark: Boolean
): ColorScheme {
    val primaryContainer = if (isDark) primary.copy(alpha = 0.20f) else primary.copy(alpha = 0.12f)
    val surfaceVariant = if (isDark) surface.copy(alpha = 0.92f) else surface
    val outline = if (isDark) textSecondary.copy(alpha = 0.55f) else textSecondary.copy(alpha = 0.40f)

    return if (isDark) {
        darkColorScheme(
            primary = primary,
            onPrimary = background,
            secondary = primary,
            background = background,
            surface = surface,
            onBackground = textPrimary,
            onSurface = textPrimary,
            error = error,
            onError = background,
            primaryContainer = primaryContainer,
            onPrimaryContainer = textPrimary,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = textSecondary,
            outline = outline
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = background,
            secondary = primary,
            background = background,
            surface = surface,
            onBackground = textPrimary,
            onSurface = textPrimary,
            error = error,
            onError = surface,
            primaryContainer = primaryContainer,
            onPrimaryContainer = textPrimary,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = textSecondary,
            outline = outline
        )
    }
}
