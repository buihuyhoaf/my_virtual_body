package com.hoabui.virtualbody3d.ui.common_ui.atom.button

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.ui.theme.GymTheme

// ─────────────────────────────────────────────────────────────────────────────
// GIconButton
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Tokenized icon-button atom for the Gym design system.
 *
 * Three visual variants:
 * - **Standard** — transparent background, icon tinted `token.colors.textPrimary`.
 *   Best for toolbar and inline actions. Custom background/clip can be applied
 *   via [modifier] (e.g. camera buttons that style their own circle background).
 * - **Filled** — circular container filled with `token.colors.primary`, icon
 *   tinted `token.colors.onPrimary`. Use for primary send / confirm actions.
 * - **Tonal** — circular container filled with `token.colors.surfaceSubtle`,
 *   icon tinted `token.colors.textPrimary`. Use for secondary icon actions.
 *
 * The icon is passed as a composable [content] slot so both `ImageVector` and
 * `painter` icons are supported without a supertype explosion.
 *
 * @param onClick Action to perform when tapped.
 * @param modifier Modifier applied to the underlying button container.
 * @param enabled When `false` the button is drawn with reduced opacity and
 *   ignores tap events.
 * @param variant Visual style. Defaults to [GIconButtonVariant.Standard].
 * @param content Composable slot for the icon — typically an [Icon].
 */
@Composable
fun GIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: GIconButtonVariant = GIconButtonVariant.Standard,
    content: @Composable () -> Unit,
) {
    val token = GymTheme.token
    when (variant) {
        GIconButtonVariant.Standard -> IconButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = token.colors.textPrimary,
                disabledContentColor = token.colors.textMuted,
            ),
            content = content,
        )

        GIconButtonVariant.Filled -> FilledIconButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = token.colors.primary,
                contentColor = token.colors.onPrimary,
                disabledContainerColor = token.colors.surfaceSubtle,
                disabledContentColor = token.colors.textMuted,
            ),
            content = content,
        )

        GIconButtonVariant.Tonal -> FilledTonalIconButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = token.colors.surfaceSubtle,
                contentColor = token.colors.textPrimary,
                disabledContainerColor = token.colors.surfaceSubtle,
                disabledContentColor = token.colors.textMuted,
            ),
            content = content,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Variant enum
// ─────────────────────────────────────────────────────────────────────────────

enum class GIconButtonVariant {
    /** Transparent background — use for toolbar/inline actions. */
    Standard,

    /** Primary-filled circle — use for primary send / confirm actions. */
    Filled,

    /** Subtle-filled circle — use for secondary icon actions. */
    Tonal,
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "GIconButton — Standard")
@Composable
private fun PreviewStandard() {
    GymTheme {
        GIconButton(onClick = {}) {
            Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null)
        }
    }
}

@Preview(showBackground = true, name = "GIconButton — Filled")
@Composable
private fun PreviewFilled() {
    GymTheme {
        GIconButton(onClick = {}, variant = GIconButtonVariant.Filled) {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
        }
    }
}

@Preview(showBackground = true, name = "GIconButton — Tonal")
@Composable
private fun PreviewTonal() {
    GymTheme {
        GIconButton(onClick = {}, variant = GIconButtonVariant.Tonal) {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
        }
    }
}

@Preview(
    showBackground = true,
    name = "GIconButton — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewDark() {
    GymTheme(darkTheme = true) {
        GIconButton(onClick = {}, variant = GIconButtonVariant.Filled) {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
        }
    }
}
