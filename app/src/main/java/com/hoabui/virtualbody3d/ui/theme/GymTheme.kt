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
 * Theme host for GymToken: warm terracotta body-inspired. Provides Material3 and token access in one place.
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
    val primaryContainer = primarySoft
    val surfaceVariant = surfaceSubtle
    val outline = borderStrong

    return if (isDark) {
        darkColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            secondary = primary,
            background = background,
            surface = surface,
            onBackground = textPrimary,
            onSurface = textPrimary,
            error = error,
            onError = onError,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = textSecondary,
            outline = outline
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            secondary = primary,
            background = background,
            surface = surface,
            onBackground = textPrimary,
            onSurface = textPrimary,
            error = error,
            onError = onError,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = textSecondary,
            outline = outline
        )
    }
}
