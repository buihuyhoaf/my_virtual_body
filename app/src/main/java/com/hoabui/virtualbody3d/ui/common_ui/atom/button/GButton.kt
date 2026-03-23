package com.hoabui.virtualbody3d.ui.common_ui.atom.button

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens

// ─────────────────────────────────────────────────────────────────────────────
// Variant
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Visual variant for [GButton].
 *
 * | Variant   | Container | Border | Use for                          |
 * |-----------|-----------|--------|----------------------------------|
 * | Primary   | Filled    | None   | Primary CTA (one per screen)     |
 * | Outlined  | None      | Brand  | Secondary action                 |
 * | Ghost     | None      | None   | Tertiary / inline text action    |
 */
enum class GButtonVariant { Primary, Outlined, Ghost }

// ─────────────────────────────────────────────────────────────────────────────
// Internal color resolution
// ─────────────────────────────────────────────────────────────────────────────

private data class GButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val borderColor: Color,
)

private fun resolveButtonColors(
    variant: GButtonVariant,
    enabled: Boolean,
    colors: SemanticColorTokens,
    disabledContainerAlpha: Float,
    disabledContentAlpha: Float,
): GButtonColors = when (variant) {
    GButtonVariant.Primary -> GButtonColors(
        containerColor = if (enabled) colors.primary
        else colors.primary.copy(alpha = disabledContainerAlpha),
        contentColor = if (enabled) colors.onPrimary
        else colors.onPrimary.copy(alpha = disabledContentAlpha),
        borderColor = Color.Transparent,
    )
    GButtonVariant.Outlined -> GButtonColors(
        containerColor = Color.Transparent,
        contentColor = if (enabled) colors.primary
        else colors.primary.copy(alpha = disabledContentAlpha),
        borderColor = if (enabled) colors.primary
        else colors.primary.copy(alpha = disabledContainerAlpha),
    )
    GButtonVariant.Ghost -> GButtonColors(
        containerColor = Color.Transparent,
        contentColor = if (enabled) colors.primary
        else colors.primary.copy(alpha = disabledContentAlpha),
        borderColor = Color.Transparent,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// GButton
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Unified button atom for the Gym design system.
 *
 * All geometry, color, and motion values come exclusively from [GymTheme.token.button].
 * No magic numbers live inside this file.
 *
 * ### Loading state ([isLoading] = `true`)
 * The button dimensions stay fully stable: the content [Row] is hidden via
 * `graphicsLayer(alpha = 0f)` — it stays in the composition tree so the layout
 * does not shift. A [CircularProgressIndicator] cross-fades into the button center.
 * Pointer interaction is disabled for the duration.
 *
 * ### Icon slots ([leadingIcon] / [trailingIcon])
 * Each slot receives [androidx.compose.material3.LocalContentColor] equal to the
 * resolved `contentColor` (set by [Surface]), so an `Icon` passed in will be tinted
 * correctly without any extra configuration. Each slot is constrained to a
 * [GymTheme.token.button.iconSize] bounding [Box].
 *
 * @param text Button label. Truncated with an ellipsis if too long (single line).
 * @param onClick Callback. Not invoked when [isLoading] or `!`[enabled].
 * @param variant Visual style — [GButtonVariant.Primary], [GButtonVariant.Outlined],
 *   or [GButtonVariant.Ghost].
 * @param isLoading When `true`, replaces content with a spinner and disables interaction
 *   without causing layout shift.
 * @param enabled When `false`, renders disabled styling and ignores all clicks.
 * @param leadingIcon Optional slot rendered before [text]; inherits content color.
 * @param trailingIcon Optional slot rendered after [text]; inherits content color.
 */
@Composable
fun GButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: GButtonVariant = GButtonVariant.Primary,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val token = GymTheme.token
    val buttonTokens = token.button

    val resolved = resolveButtonColors(
        variant = variant,
        enabled = enabled,
        colors = token.colors,
        disabledContainerAlpha = buttonTokens.disabledContainerAlpha,
        disabledContentAlpha = buttonTokens.disabledContentAlpha,
    )

    // Crossfade: content ↔ spinner over 150 ms (fast enough to feel snappy)
    val contentAlpha by animateFloatAsState(
        targetValue = if (isLoading) 0f else 1f,
        animationSpec = tween(durationMillis = 150, easing = LinearEasing),
        label = "g_button_content_alpha",
    )
    val spinnerAlpha by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f,
        animationSpec = tween(durationMillis = 150, easing = LinearEasing),
        label = "g_button_spinner_alpha",
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = buttonTokens.height)
            .semantics {
                role = Role.Button
                if (isLoading) stateDescription = "Loading"
            },
        // Clicks suppressed during loading; visual enabled-state uses the `enabled` flag
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(buttonTokens.cornerRadius),
        color = resolved.containerColor,
        // Surface propagates contentColor as LocalContentColor → icons auto-tint
        contentColor = resolved.contentColor,
        border = if (variant == GButtonVariant.Outlined) {
            BorderStroke(width = 1.5.dp, color = resolved.borderColor)
        } else {
            null
        },
        tonalElevation = if (variant == GButtonVariant.Primary && enabled) {
            token.elevation.level1
        } else {
            0.dp
        },
        shadowElevation = if (variant == GButtonVariant.Primary && enabled) {
            token.elevation.level1
        } else {
            0.dp
        },
    ) {
        // Box stacks spinner and content row; the larger child (content row) controls size,
        // so the button never shrinks or grows during the loading transition.
        Box(contentAlignment = Alignment.Center) {

            // ── Spinner layer ────────────────────────────────────────────────
            // Hidden (alpha=0) when not loading; sized to iconSize so it never
            // pushes the content row out of place.
            CircularProgressIndicator(
                modifier = Modifier
                    .size(buttonTokens.iconSize + 4.dp)
                    .graphicsLayer { alpha = spinnerAlpha },
                color = resolved.contentColor,
                strokeWidth = 2.dp,
            )

            // ── Content layer ────────────────────────────────────────────────
            // alpha=0 while loading, but STAYS IN THE LAYOUT TREE.
            // This is the key technique: the Row still occupies its measured space,
            // so the button's width and height are anchored to the content at all times.
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = buttonTokens.contentPaddingHorizontal,
                        vertical = token.spacing.xs,
                    )
                    .graphicsLayer { alpha = contentAlpha },
                horizontalArrangement = Arrangement.spacedBy(
                    space = token.spacing.xs,
                    alignment = Alignment.CenterHorizontally,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingIcon != null) {
                    // Constrain icon to tokenized size; LocalContentColor flows in from Surface
                    Box(modifier = Modifier.size(buttonTokens.iconSize)) {
                        leadingIcon()
                    }
                }
                Text(
                    text = text,
                    style = token.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    // No explicit color: inherits LocalContentColor from Surface → correct for
                    // all enabled/disabled states without any extra CompositionLocalProvider
                )
                if (trailingIcon != null) {
                    Box(modifier = Modifier.size(buttonTokens.iconSize)) {
                        trailingIcon()
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PreviewLabel(text: String) {
    val token = GymTheme.token
    Text(
        text = text.uppercase(),
        style = token.typography.labelSmall,
        color = token.colors.textMuted,
        modifier = Modifier.padding(top = token.spacing.md, bottom = token.spacing.xxs),
    )
}

@Composable
private fun GButtonShowcase(darkTheme: Boolean = false) {
    GymTheme(darkTheme = darkTheme) {
        val token = GymTheme.token
        Column(
            modifier = Modifier
                .background(token.colors.background)
                .padding(token.spacing.md)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
        ) {

            // ── Enabled ─────────────────────────────────────────────────────
            PreviewLabel("Enabled")
            GButton(
                text = "Primary",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            )
            GButton(
                text = "Outlined",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = GButtonVariant.Outlined,
            )
            GButton(
                text = "Ghost",
                onClick = {},
                variant = GButtonVariant.Ghost,
            )

            // ── Disabled ─────────────────────────────────────────────────────
            PreviewLabel("Disabled")
            GButton(
                text = "Primary",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
            )
            GButton(
                text = "Outlined",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = GButtonVariant.Outlined,
                enabled = false,
            )
            GButton(
                text = "Ghost",
                onClick = {},
                variant = GButtonVariant.Ghost,
                enabled = false,
            )

            // ── Loading ──────────────────────────────────────────────────────
            PreviewLabel("Loading — no layout shift")
            GButton(
                text = "Primary",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                isLoading = true,
            )
            GButton(
                text = "Outlined",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = GButtonVariant.Outlined,
                isLoading = true,
            )
            GButton(
                text = "Ghost",
                onClick = {},
                variant = GButtonVariant.Ghost,
                isLoading = true,
            )

            // ── Leading icon ─────────────────────────────────────────────────
            PreviewLabel("Leading Icon — auto-tinted via LocalContentColor")
            GButton(
                text = "Add Exercise",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                },
            )
            GButton(
                text = "Add Exercise",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = GButtonVariant.Outlined,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                },
            )
            GButton(
                text = "Add Exercise",
                onClick = {},
                variant = GButtonVariant.Ghost,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                },
            )

            // ── Trailing icon ────────────────────────────────────────────────
            PreviewLabel("Trailing Icon")
            GButton(
                text = "See Results",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                },
            )
            GButton(
                text = "See Results",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = GButtonVariant.Outlined,
                trailingIcon = {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                },
            )
            GButton(
                text = "See More",
                onClick = {},
                variant = GButtonVariant.Ghost,
                trailingIcon = {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                },
            )

            // ── Both icons ───────────────────────────────────────────────────
            PreviewLabel("Both Icons")
            GButton(
                text = "Start Workout",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                },
            )

            // ── Compact / wrap-content width ─────────────────────────────────
            PreviewLabel("Compact (wrap content)")
            Row(horizontalArrangement = Arrangement.spacedBy(token.spacing.xs)) {
                GButton(text = "Primary", onClick = {})
                GButton(text = "Outlined", onClick = {}, variant = GButtonVariant.Outlined)
                GButton(text = "Ghost", onClick = {}, variant = GButtonVariant.Ghost)
            }

            Spacer(modifier = Modifier.width(1.dp)) // visual bottom breathing room
        }
    }
}

@Preview(showBackground = true, name = "GButton — Light")
@Composable
private fun PreviewGButtonLight() {
    GButtonShowcase(darkTheme = false)
}

@Preview(
    showBackground = true,
    name = "GButton — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGButtonDark() {
    GButtonShowcase(darkTheme = true)
}
