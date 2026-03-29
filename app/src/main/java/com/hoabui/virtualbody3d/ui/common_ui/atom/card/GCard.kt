package com.hoabui.virtualbody3d.ui.common_ui.atom.card

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.gymPremiumInnerRadialDepth
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment

// ─────────────────────────────────────────────────────────────────────────────
// GCard
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Pure container card atom for the Gym design system.
 *
 * Shape defaults to [GymTheme.token.card] radii; pass [shape] for image tiles and custom corners.
 * Premium styling (border, inner radial, hero rim) follows [treatment].
 *
 * ### Clickable vs. non-clickable
 * - `onClick = null` → non-clickable [Card]
 * - `onClick != null` → clickable [Card] with M3 ripple
 * - `onClick` + [onLongClick] → non-clickable [Card] with [combinedClickable] (no ripple indication; use caller feedback e.g. press scale)
 *
 * @param pressedElevation When null, pressed state uses [level2][com.hoabui.virtualbody3d.ui.theme.tokens.ElevationTokens.level2] if clickable.
 * @param contentModifier Applied to the inner [Column] (e.g. [fillMaxSize] for fixed-height tiles).
 */
@Composable
fun GCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    shape: Shape? = null,
    elevation: Dp = GymTheme.token.card.elevation,
    pressedElevation: Dp? = null,
    containerColor: Color = GymTheme.token.colors.surface,
    border: BorderStroke? = null,
    treatment: GSurfaceTreatment = GSurfaceTreatment.Standard,
    contentModifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val token = GymTheme.token
    val surfaceTok = token.surface
    val resolvedShape = shape ?: RoundedCornerShape(token.card.cornerRadius)
    val effectiveBorder = when {
        border != null -> border
        treatment == GSurfaceTreatment.Flat || !surfaceTok.applyDefaultSubtleBorder -> null
        treatment == GSurfaceTreatment.Hero -> null
        else -> BorderStroke(token.borderWidth.hairline, token.colors.borderSubtle)
    }
    val rimBrush = Brush.linearGradient(
        colors = listOf(
            token.colors.primary.copy(alpha = surfaceTok.gradientRimAlphaHigh),
            token.colors.borderSubtle.copy(alpha = surfaceTok.gradientRimAlphaLow),
        ),
    )
    val borderModifier = if (treatment == GSurfaceTreatment.Hero) {
        Modifier.border(
            width = token.borderWidth.hairline,
            brush = rimBrush,
            shape = resolvedShape,
        )
    } else {
        Modifier
    }
    val resolvedPressed = pressedElevation
        ?: if (onClick != null || onLongClick != null) token.elevation.level2 else token.elevation.level0
    val colors = CardDefaults.cardColors(containerColor = containerColor)
    val cardElevation = CardDefaults.cardElevation(
        defaultElevation = elevation,
        pressedElevation = resolvedPressed,
        focusedElevation = elevation,
        hoveredElevation = elevation,
        disabledElevation = token.elevation.level0,
    )
    val columnModifier = Modifier
        .gymPremiumInnerRadialDepth(
            enabled = treatment != GSurfaceTreatment.Flat,
            token = token,
        )
        .then(contentModifier)

    val cardInteractionSource = interactionSource ?: remember { MutableInteractionSource() }

    when {
        onClick != null && onLongClick != null -> {
            Card(
                modifier = modifier
                    .then(borderModifier)
                    .clip(resolvedShape)
                    .combinedClickable(
                        interactionSource = cardInteractionSource,
                        indication = null,
                        onClick = onClick,
                        onLongClick = onLongClick,
                    ),
                shape = resolvedShape,
                colors = colors,
                elevation = cardElevation,
                border = effectiveBorder,
            ) {
                Column(modifier = columnModifier, content = content)
            }
        }
        onClick != null -> {
            Card(
                onClick = onClick,
                modifier = modifier.then(borderModifier),
                shape = resolvedShape,
                colors = colors,
                elevation = cardElevation,
                border = effectiveBorder,
                interactionSource = cardInteractionSource,
            ) {
                Column(modifier = columnModifier, content = content)
            }
        }
        else -> {
            Card(
                modifier = modifier.then(borderModifier),
                shape = resolvedShape,
                colors = colors,
                elevation = cardElevation,
                border = effectiveBorder,
            ) {
                Column(modifier = columnModifier, content = content)
            }
        }
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

@Preview(showBackground = true, name = "GCard — Hero treatment")
@Composable
private fun PreviewHero() {
    GymTheme {
        val token = GymTheme.token
        GCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.spacing.md),
            treatment = GSurfaceTreatment.Hero,
            elevation = token.surface.heroShadowElevation,
        ) {
            Column(modifier = Modifier.padding(token.spacing.md)) {
                GText(text = "Primary focal card", style = token.typography.titleSmall)
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
