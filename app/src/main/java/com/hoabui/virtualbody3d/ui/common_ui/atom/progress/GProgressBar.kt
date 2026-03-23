package com.hoabui.virtualbody3d.ui.common_ui.atom.progress

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import kotlin.math.roundToInt

// ─────────────────────────────────────────────────────────────────────────────
// GProgressBar
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Smooth, accessible linear progress bar atom for the Gym design system.
 *
 * ### Animation
 * [progress] is animated internally via [animateFloatAsState] with a `spring` spec
 * (no bounciness, medium stiffness). Callers simply pass the raw `0f..1f` value;
 * the animation is free and requires no extra state at the call site.
 *
 * ### Performance
 * The M3 [LinearProgressIndicator] is called with the **lambda form**
 * `progress = { animatedProgress }`. The lambda is read only during the draw phase,
 * skipping the recomposition triggered by every frame of the animation.
 *
 * ### Accessibility
 * `semantics { progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f) }`
 * is applied to the indicator so TalkBack announces the current progress correctly.
 * The optional [label] is placed above the bar and read first by screen readers
 * because it appears earlier in the layout tree.
 *
 * @param progress Current progress value. Clamped to `0f..1f` internally.
 * @param label Optional text label shown above-start of the bar (e.g. muscle group name).
 * @param showPercentage When `true`, a percentage string (e.g. "72%") is shown
 *   end-aligned on the same row as [label].
 * @param trackColor Background track colour. Defaults to `token.colors.surfaceSubtle`.
 * @param indicatorColor Filled indicator colour. Defaults to `token.colors.primary`.
 * @param height Bar height. Defaults to `6.dp` — matching current usage in
 *   `BodyRegionDetailScreen`. Override via this parameter without touching token files.
 * @param cornerRadius Clip radius applied to both track and indicator.
 *   Defaults to `token.radius.sm`.
 */
@Composable
fun GProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    label: String? = null,
    showPercentage: Boolean = false,
    trackColor: Color = GymTheme.token.colors.surfaceSubtle,
    indicatorColor: Color = GymTheme.token.colors.primary,
    height: Dp = 6.dp,
    cornerRadius: Dp = GymTheme.token.radius.sm,
) {
    val token = GymTheme.token

    // Clamp before animation so glitches from out-of-range values are prevented
    val clampedProgress = progress.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "g_progress_bar",
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
    ) {
        // Optional label row (label + optional percentage end-aligned)
        if (label != null || showPercentage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (label != null) {
                    GText(
                        text = label,
                        style = token.typography.labelMedium,
                        color = token.colors.textSecondary,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (showPercentage) {
                    GText(
                        text = "${(clampedProgress * 100).roundToInt()}%",
                        style = token.typography.labelSmall,
                        color = token.colors.textMuted,
                    )
                }
            }
        }

        // Progress indicator
        // Lambda form: `progress = { animatedProgress }` means the lambda is
        // read only in the draw phase — recomposition is skipped during animation
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(cornerRadius))
                .semantics {
                    progressBarRangeInfo = ProgressBarRangeInfo(
                        current = clampedProgress,
                        range = 0f..1f,
                    )
                },
            color = indicatorColor,
            trackColor = trackColor,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "GProgressBar — States")
@Composable
private fun PreviewGProgressBarStates() {
    GymTheme {
        val token = GymTheme.token
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.lg),
        ) {
            // Plain bar at various progress values
            GText(
                text = "PLAIN",
                style = token.typography.labelSmall,
                color = token.colors.textMuted,
            )
            GProgressBar(progress = 0f)
            GProgressBar(progress = 0.35f)
            GProgressBar(progress = 0.72f)
            GProgressBar(progress = 1f)

            // With label
            GText(
                text = "WITH LABEL",
                style = token.typography.labelSmall,
                color = token.colors.textMuted,
            )
            GProgressBar(progress = 0.6f, label = "Chest")
            GProgressBar(progress = 0.3f, label = "Back")

            // With label + percentage
            GText(
                text = "WITH LABEL + PERCENTAGE",
                style = token.typography.labelSmall,
                color = token.colors.textMuted,
            )
            GProgressBar(progress = 0.72f, label = "Muscle mass", showPercentage = true)
            GProgressBar(progress = 0.28f, label = "Body fat", showPercentage = true)

            // Thick bar
            GText(
                text = "THICK VARIANT (12 dp)",
                style = token.typography.labelSmall,
                color = token.colors.textMuted,
            )
            GProgressBar(progress = 0.55f, height = 12.dp)

            // Custom colours
            GText(
                text = "CUSTOM COLOURS",
                style = token.typography.labelSmall,
                color = token.colors.textMuted,
            )
            GProgressBar(
                progress = 0.4f,
                label = "Calories burned",
                showPercentage = true,
                indicatorColor = token.colors.calorieIntake,
                trackColor = token.colors.calorieRingTrack,
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GProgressBar — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGProgressBarDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md),
        ) {
            GProgressBar(progress = 0.65f, label = "Arms", showPercentage = true)
            GProgressBar(progress = 0.40f, label = "Core", showPercentage = true)
            GProgressBar(progress = 0.85f, label = "Legs", showPercentage = true)
        }
    }
}
