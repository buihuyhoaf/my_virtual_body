package com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.RectangleShape
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

// ─────────────────────────────────────────────────────────────────────────────
// Variant
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Visual variant for [GTopBar].
 *
 * | Variant     | Background                | Bottom border | Use for                          |
 * |-------------|---------------------------|---------------|----------------------------------|
 * | Solid       | `token.colors.surface`    | Subtle        | Standard detail / list screens   |
 * | Transparent | `Color.Transparent`       | None          | Camera, body-scan overlays       |
 */
enum class GTopBarVariant { Solid, Transparent }

// ─────────────────────────────────────────────────────────────────────────────
// GTopBar
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Standardised top-bar molecule for the Gym design system.
 *
 * Centralises three patterns that are currently copy-pasted across five screens
 * (`ExerciseLibraryScreen`, `BodyRegionDetailScreen`, `AddWorkoutScreen`,
 * `BodyDetailAnalystScreen`, `MessageDetailScreen`):
 *  - Status-bar window inset handling
 *  - Navigation icon slot (back arrow by default when provided)
 *  - Actions slot (trailing icon buttons)
 *
 * ### Inset interaction with [GScaffold]
 * When `GTopBar` is used inside `GScaffold`, pass
 * `windowInsets = WindowInsets(0)` to avoid double padding, because
 * `GScaffold` already consumes `WindowInsets.safeDrawing`.
 *
 * @param title Bar title text. Rendered with `titleLarge` + `SemiBold`.
 * @param navigationIcon Optional slot. Pass `null` for home-style bars with no back arrow.
 *   When non-null, the composable is rendered at the leading edge inside a fixed-size box.
 * @param actions Trailing-edge slot (e.g. overflow menu icon, share icon).
 *   Each icon should be wrapped in [IconButton].
 * @param windowInsets Inset consumed by the bar. Defaults to [WindowInsets.statusBars].
 *   Pass `WindowInsets(0)` when inside a [GScaffold] that already handles insets.
 * @param variant [GTopBarVariant.Solid] draws a surface background + bottom divider.
 *   [GTopBarVariant.Transparent] renders over content (camera / body-scan screens).
 */
@Composable
fun GTopBar(
    modifier: Modifier = Modifier,
    title: String = "",
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = WindowInsets.statusBars,
    variant: GTopBarVariant = GTopBarVariant.Solid,
) {
    val token = GymTheme.token
    val containerColor = when (variant) {
        GTopBarVariant.Solid -> token.colors.surface
        GTopBarVariant.Transparent -> Color.Transparent
    }

    GSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RectangleShape,
        color = containerColor,
        shadowElevation = token.elevation.level0,
        treatment = GSurfaceTreatment.Flat,
    ) {
        // Content column: inset padding + row + optional divider
        androidx.compose.foundation.layout.Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(windowInsets)
                    .padding(
                        start = if (navigationIcon != null) token.spacing.xxs else token.spacing.md,
                        end = token.spacing.xs,
                        top = token.spacing.xs,
                        bottom = token.spacing.xs,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                // Leading: navigation icon (fixed 48×48 touch target) or spacer
                if (navigationIcon != null) {
                    Box(modifier = Modifier.size(token.spacing.xxl)) {
                        navigationIcon()
                    }
                }

                // Title: fills remaining space, truncates on overflow
                GText(
                    text = title,
                    style = token.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = token.colors.textPrimary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )

                // Trailing: actions row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    content = actions,
                )
            }

            // Bottom divider only on Solid variant
            if (variant == GTopBarVariant.Solid) {
                HorizontalDivider(
                    color = token.colors.borderSubtle,
                    thickness = token.spacing.dividerThickness,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Convenience: back-navigation icon
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Ready-made back-arrow [navigationIcon] slot content.
 * Pass this directly to [GTopBar.navigationIcon]:
 * ```
 * GTopBar(
 *     title = "Exercise Library",
 *     navigationIcon = { GTopBarBackIcon(onBack = onNavigateUp) },
 * )
 * ```
 */
@Composable
fun GTopBarBackIcon(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    IconButton(
        onClick = onBack,
        modifier = modifier.size(token.spacing.xxl),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.body_region_detail_back),
            tint = token.colors.textPrimary,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "GTopBar — Solid, no nav")
@Composable
private fun PreviewSolidNoNav() {
    GymTheme {
        GTopBar(title = "Home")
    }
}

@Preview(showBackground = true, name = "GTopBar — Solid, with back")
@Composable
private fun PreviewSolidWithBack() {
    GymTheme {
        GTopBar(
            title = "Exercise Library",
            navigationIcon = { GTopBarBackIcon(onBack = {}) },
        )
    }
}

@Preview(showBackground = true, name = "GTopBar — Solid, with back + actions")
@Composable
private fun PreviewSolidWithActions() {
    GymTheme {
        GTopBar(
            title = "Workout Detail",
            navigationIcon = { GTopBarBackIcon(onBack = {}) },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = GymTheme.token.colors.textPrimary,
                    )
                }
            },
        )
    }
}

@Preview(showBackground = true, name = "GTopBar — Transparent")
@Composable
private fun PreviewTransparent() {
    GymTheme {
        GTopBar(
            title = "Scan Result",
            navigationIcon = { GTopBarBackIcon(onBack = {}) },
            variant = GTopBarVariant.Transparent,
        )
    }
}

@Preview(
    showBackground = true,
    name = "GTopBar — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewDark() {
    GymTheme(darkTheme = true) {
        GTopBar(
            title = "Body Region Detail",
            navigationIcon = { GTopBarBackIcon(onBack = {}) },
        )
    }
}

@Preview(showBackground = true, name = "GTopBar — Long title truncation")
@Composable
private fun PreviewLongTitle() {
    GymTheme {
        GTopBar(
            title = "This Is A Very Long Title That Should Truncate With Ellipsis",
            navigationIcon = { GTopBarBackIcon(onBack = {}) },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = GymTheme.token.colors.textPrimary,
                    )
                }
                Spacer(modifier = Modifier.size(GymTheme.token.spacing.xxs))
            },
        )
    }
}
