package com.hoabui.virtualbody3d.ui.common_ui.atom.progress

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.GymTheme

// ─────────────────────────────────────────────────────────────────────────────
// GCircularProgress
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Tokenized circular progress atom for the Gym design system.
 *
 * Supports two modes:
 * - **Indeterminate** — `progress = null` (default). Renders a spinning animation.
 *   `trackColor` is ignored in this mode (M3 indeterminate indicator has no track).
 * - **Determinate** — `progress = 0f..1f`. Renders a static arc showing completion.
 *   The arc is clamped to `[0f, 1f]` automatically.
 *
 * All visual properties default to `GymTheme.token.colors` so indicators are
 * automatically brand-consistent across the app.
 *
 * @param modifier Modifier applied to the indicator.
 * @param progress `null` for indeterminate spin; a value in `[0f, 1f]` for
 *   a determinate arc. Values outside the range are clamped.
 * @param color Arc / indicator color. Defaults to `token.colors.primary`.
 * @param trackColor Background track color used in **determinate** mode only.
 *   Defaults to `token.colors.surfaceSubtle`.
 * @param strokeWidth Width of the indicator arc. Defaults to `4.dp` (M3 default).
 * @param strokeCap Cap style for the arc endpoints. Defaults to [StrokeCap.Round].
 */
@Composable
fun GCircularProgress(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    color: Color = GymTheme.token.colors.primary,
    trackColor: Color = GymTheme.token.colors.surfaceSubtle,
    strokeWidth: Dp? = null,
    strokeCap: StrokeCap = StrokeCap.Round,
) {
    val resolvedStrokeWidth = strokeWidth ?: GymTheme.token.spacing.xxs
    if (progress == null) {
        CircularProgressIndicator(
            modifier = modifier,
            color = color,
            strokeWidth = resolvedStrokeWidth,
            strokeCap = strokeCap,
        )
    } else {
        val clamped = progress.coerceIn(0f, 1f)
        CircularProgressIndicator(
            progress = { clamped },
            modifier = modifier,
            color = color,
            trackColor = trackColor,
            strokeWidth = resolvedStrokeWidth,
            strokeCap = strokeCap,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "GCircularProgress — Indeterminate")
@Composable
private fun PreviewIndeterminate() {
    GymTheme {
        Column(
            modifier = Modifier.padding(GymTheme.token.spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(GymTheme.token.spacing.md),
        ) {
            GCircularProgress()
        }
    }
}

@Preview(showBackground = true, name = "GCircularProgress — Determinate 60%%")
@Composable
private fun PreviewDeterminate() {
    GymTheme {
        val token = GymTheme.token
        Column(
            modifier = Modifier.padding(token.spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(token.spacing.md),
        ) {
            GCircularProgress(
                progress = 0.6f,
                modifier = Modifier.size(48.dp),
                strokeWidth = 6.dp,
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GCircularProgress — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewDark() {
    GymTheme(darkTheme = true) {
        GCircularProgress(modifier = Modifier.padding(GymTheme.token.spacing.md))
    }
}
