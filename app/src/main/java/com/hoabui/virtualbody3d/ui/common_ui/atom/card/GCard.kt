package com.hoabui.virtualbody3d.ui.common_ui.atom.card

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

// ─────────────────────────────────────────────────────────────────────────────
// GCard
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Pure container card atom for the Gym design system.
 *
 * All visual properties (shape, elevation, default container colour) come from
 * `GymTheme.token.card`, so cards are automatically consistent across the app.
 *
 * ### Clickable vs. non-clickable
 * - `onClick = null` → renders a non-clickable [Card] (no ripple, no extra semantics overhead)
 * - `onClick != null` → renders a clickable [Card] with M3 ripple and `Role.Button` semantics
 *
 * ### Shape contract
 * The shape is always `token.card.cornerRadius` and is intentionally **not** overridable.
 * This enforces visual consistency — use a custom [Card] directly if you need a different shape.
 *
 * @param onClick Optional click callback. When non-null, the card is interactive.
 * @param elevation Shadow elevation. Defaults to `token.card.elevation` (8 dp).
 * @param containerColor Card background. Defaults to `token.colors.surface`.
 * @param content Column-scoped content slot.
 */
@Composable
fun GCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Dp = GymTheme.token.card.elevation,
    containerColor: Color = GymTheme.token.colors.surface,
    content: @Composable ColumnScope.() -> Unit,
) {
    val token = GymTheme.token
    val shape = RoundedCornerShape(token.card.cornerRadius)
    val colors = CardDefaults.cardColors(containerColor = containerColor)
    val cardElevation = CardDefaults.cardElevation(defaultElevation = elevation)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = cardElevation,
            content = content,
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = cardElevation,
            content = content,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "GCard — Non-clickable")
@Composable
private fun PreviewNonClickable() {
    GymTheme {
        val token = GymTheme.token
        GCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.spacing.md),
        ) {
            Column(
                modifier = Modifier.padding(token.spacing.md),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
            ) {
                GText(text = "Workout Summary", style = token.typography.titleSmall)
                GText(
                    text = "3 exercises · 45 min",
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "GCard — Clickable")
@Composable
private fun PreviewClickable() {
    GymTheme {
        val token = GymTheme.token
        GCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.spacing.md),
            onClick = {},
        ) {
            Column(
                modifier = Modifier.padding(token.spacing.md),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
            ) {
                GText(text = "Bench Press", style = token.typography.titleSmall)
                GText(
                    text = "Chest · 4 sets · 12 reps",
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "GCard — Custom elevation + color")
@Composable
private fun PreviewCustomColors() {
    GymTheme {
        val token = GymTheme.token
        GCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.spacing.md),
            elevation = token.elevation.level2,
            containerColor = token.colors.surfaceSubtle,
        ) {
            Column(modifier = Modifier.padding(token.spacing.md)) {
                GText(text = "Elevated surface card", style = token.typography.bodyMedium)
            }
        }
    }
}

@Preview(
    showBackground = true,
    name = "GCard — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        GCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.spacing.md),
            onClick = {},
        ) {
            Column(
                modifier = Modifier.padding(token.spacing.md),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
            ) {
                GText(text = "Dark mode card", style = token.typography.titleSmall)
                GText(
                    text = "Surface colours from token",
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary,
                )
            }
        }
    }
}
