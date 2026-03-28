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
import androidx.compose.runtime.remember
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken
import com.hoabui.virtualbody3d.ui.theme.tokens.LocalGymToken
import com.hoabui.virtualbody3d.ui.theme.tokens.darkGymToken
import com.hoabui.virtualbody3d.ui.theme.tokens.lightGymToken
import com.hoabui.virtualbody3d.ui.theme.tokens.component.LocalButtonTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.LocalCardTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.gymTypographyTokens

/**
 * Theme host for GymToken: Holistic Vitality — light (sage / sand / slate) and dark (moss / muted sage / parchment).
 */
@Composable
fun GymTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    windowSizeClass: WindowSizeClass? = null,
    tokenOverride: GymToken? = null,
    content: @Composable () -> Unit
) {
    val useExpandedProfile = remember(windowSizeClass) {
        isExpandedWindow(windowSizeClass)
    }
    val adaptiveSpacing = remember(useExpandedProfile) {
        if (useExpandedProfile) PrimitiveSpacingTokens.expanded() else PrimitiveSpacingTokens.compact()
    }
    val adaptiveTypography = remember(useExpandedProfile) {
        // Infrastructure hook: keep same scale now, allow expanded profile override in next phase.
        gymTypographyTokens().material
    }
    val token = tokenOverride ?: if (darkTheme) {
        darkGymToken(
            primitiveSpacing = adaptiveSpacing,
            typography = adaptiveTypography
        )
    } else {
        lightGymToken(
            primitiveSpacing = adaptiveSpacing,
            typography = adaptiveTypography
        )
    }

    CompositionLocalProvider(
        LocalGymToken provides token,
        LocalButtonTokens provides token.button,
        LocalCardTokens provides token.card
    ) {
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

private fun isExpandedWindow(windowSizeClass: WindowSizeClass?): Boolean {
    if (windowSizeClass == null) return false
    return windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact ||
        windowSizeClass.heightSizeClass == WindowHeightSizeClass.Expanded
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
            secondary = secondary,
            onSecondary = onSecondary,
            secondaryContainer = secondaryContainer,
            onSecondaryContainer = onSecondaryContainer,
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
            secondary = secondary,
            onSecondary = onSecondary,
            secondaryContainer = secondaryContainer,
            onSecondaryContainer = onSecondaryContainer,
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
