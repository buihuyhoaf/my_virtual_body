package com.hoabui.virtualbody3d.ui.common_ui.molecule.card

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.CardSize
import com.hoabui.virtualbody3d.ui.common_ui.cardDimensions
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

// ─────────────────────────────────────────────────────────────────────────────
// Internal helpers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Resolved per-size typography and spacing values for [GImageCard].
 * Extracted into a data class so the `when(cardSize)` block runs once
 * and the resulting values are stable for the rest of the composition.
 */
private data class CardSizeStyle(
    val firstLineStyle: TextStyle,
    val secondLineStyle: TextStyle,
    val textPaddingH: Dp,
    val textPaddingV: Dp,
    val textLineSpacing: Dp,
    val badgeOuterPadding: Dp,
    val badgeInnerH: Dp,
    val badgeInnerV: Dp,
)

@Composable
private fun resolveCardSizeStyle(cardSize: CardSize): CardSizeStyle {
    val token = GymTheme.token
    return when (cardSize) {
        CardSize.Small -> CardSizeStyle(
            firstLineStyle = token.typography.labelMedium,
            secondLineStyle = token.typography.labelSmall,
            textPaddingH = token.spacing.xxs,
            textPaddingV = token.spacing.xxs,
            textLineSpacing = token.spacing.xxxs,
            badgeOuterPadding = token.spacing.xxs,
            badgeInnerH = token.spacing.xxs,
            badgeInnerV = token.spacing.xxxs,
        )
        CardSize.Medium -> CardSizeStyle(
            firstLineStyle = token.typography.titleSmall,
            secondLineStyle = token.typography.labelSmall,
            textPaddingH = token.spacing.xs,
            textPaddingV = token.spacing.xxs,
            textLineSpacing = token.spacing.xxs,
            badgeOuterPadding = token.spacing.xs,
            badgeInnerH = token.spacing.xs,
            badgeInnerV = token.spacing.xxs,
        )
        CardSize.Large -> CardSizeStyle(
            firstLineStyle = token.typography.titleSmall,
            secondLineStyle = token.typography.labelSmall,
            textPaddingH = token.spacing.xs,
            textPaddingV = token.spacing.xs,
            textLineSpacing = token.spacing.xxs,
            badgeOuterPadding = token.spacing.xs,
            badgeInnerH = token.spacing.xs,
            badgeInnerV = token.spacing.xxs,
        )
    }
}

/**
 * Stateless press-scale animation: shrinks to 96% on press.
 * Extracted as a private Modifier extension — stateless, no composition context.
 */
private fun Modifier.pressScale(interactionSource: MutableInteractionSource, scale: Float): Modifier =
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }

// ─────────────────────────────────────────────────────────────────────────────
// GImageCard
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Media card molecule: an image filling the card background, a vertical gradient
 * overlay, two lines of text at the bottom, and an optional badge slot at the top-end.
 *
 * Supersedes [com.hoabui.virtualbody3d.ui.common_ui.CardImageWithText], fixing:
 * - `model: Any?` accepts remote URLs, `@DrawableRes` Int, `Uri`, or `Painter` —
 *   powered by Coil's [AsyncImage] (same as [GRoundedImage])
 * - `badge` is a composable **slot**, not an `Enum<*>` — the card is decoupled from
 *   the `Difficulty` domain model
 * - Single `shape` application (no double `.clip()` + `shape` bug)
 * - Typography and spacing resolved once via [CardSizeStyle] (no copy-pasted `when` blocks)
 * - Text rendered via [GText] (design-system token aware)
 * - Press scale is a private `Modifier` extension (stateless, reusable)
 *
 * @param model Image source. Accepts anything Coil understands: URL `String`, `@DrawableRes`
 *   `Int`, `Uri`, `File`, `Painter`, `ImageBitmap`, etc.
 * @param contentDescription Accessibility description for the background image.
 * @param firstLineText Primary label line (title / name).
 * @param secondLineText Secondary label line (subtitle / metadata).
 * @param cardSize Controls card dimensions from `GymTheme.token.bodyAnalysis.cardImageWithText`.
 * @param badge Optional slot rendered at the top-end of the card (e.g. a difficulty badge).
 *   The slot receives [BoxScope] so `Modifier.align(...)` works without extra wrapping.
 * @param onClick Optional click callback. When `null`, the card is non-interactive.
 */
@Composable
fun GImageCard(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    firstLineText: String,
    secondLineText: String,
    cardSize: CardSize = CardSize.Medium,
    badge: (@Composable BoxScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val token = GymTheme.token
    val (cardWidth, cardHeight) = token.bodyAnalysis.cardImageWithText.cardDimensions(cardSize)
    val sizeStyle = resolveCardSizeStyle(cardSize)
    val shape = RoundedCornerShape(token.radius.lg)

    // Press-scale state — only allocated when the card is interactive
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.96f else 1f,
        label = "g_image_card_scale",
    )

    val cardModifier = modifier
        .width(cardWidth)
        .height(cardHeight)
        .pressScale(interactionSource, scale)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = cardModifier,
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = token.colors.surface),
            elevation = CardDefaults.cardElevation(
                defaultElevation = token.elevation.level2,
                pressedElevation = token.elevation.level1,
            ),
            interactionSource = interactionSource,
        ) {
            GImageCardContent(
                model = model,
                contentDescription = contentDescription,
                sizeStyle = sizeStyle,
                firstLineText = firstLineText,
                secondLineText = secondLineText,
                badge = badge,
            )
        }
    } else {
        Card(
            modifier = cardModifier,
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = token.colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = token.elevation.level2),
        ) {
            GImageCardContent(
                model = model,
                contentDescription = contentDescription,
                sizeStyle = sizeStyle,
                firstLineText = firstLineText,
                secondLineText = secondLineText,
                badge = badge,
            )
        }
    }
}

@Composable
private fun GImageCardContent(
    model: Any?,
    contentDescription: String?,
    sizeStyle: CardSizeStyle,
    firstLineText: String,
    secondLineText: String,
    badge: (@Composable BoxScope.() -> Unit)?,
) {
    val token = GymTheme.token
    Box(modifier = Modifier.fillMaxSize()) {

        // Background image via Coil — supports URL, @DrawableRes, Uri, etc.
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Gradient: transparent at top → scrim at bottom
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            token.colors.backgroundScrim.copy(alpha = 0f),
                            token.colors.backgroundScrim.copy(alpha = 0.8f),
                        ),
                    ),
                ),
        )

        // Text overlay — bottom-start
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(
                    horizontal = sizeStyle.textPaddingH,
                    vertical = sizeStyle.textPaddingV,
                ),
            verticalArrangement = Arrangement.spacedBy(sizeStyle.textLineSpacing),
        ) {
            GText(
                text = firstLineText,
                style = sizeStyle.firstLineStyle,
                color = token.colors.onPrimary,
                maxLines = 1,
            )
            GText(
                text = secondLineText,
                style = sizeStyle.secondLineStyle,
                color = token.colors.onPrimary.copy(alpha = 0.8f),
                maxLines = 1,
            )
        }

        // Badge slot — top-end; caller provides full content and positioning
        if (badge != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(sizeStyle.badgeOuterPadding),
            ) {
                Surface(
                    shape = RoundedCornerShape(token.radius.sm),
                    color = Color.Transparent,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(token.radius.sm))
                            .padding(
                                horizontal = sizeStyle.badgeInnerH,
                                vertical = sizeStyle.badgeInnerV,
                            ),
                    ) {
                        badge()
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "GImageCard — All sizes")
@Composable
private fun PreviewAllSizes() {
    GymTheme {
        val token = GymTheme.token
        Row(
            modifier = Modifier.padding(token.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = "Small card",
                firstLineText = "Push Day",
                secondLineText = "Chest · 3 sets",
                cardSize = CardSize.Small,
                onClick = {},
            )
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = "Medium card",
                firstLineText = "Bench Press",
                secondLineText = "Intermediate",
                cardSize = CardSize.Medium,
                onClick = {},
            )
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = "Large card",
                firstLineText = "Full Body Blast",
                secondLineText = "Advanced · 60 min",
                cardSize = CardSize.Large,
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "GImageCard — With badge slot")
@Composable
private fun PreviewWithBadge() {
    GymTheme {
        val token = GymTheme.token
        Box(modifier = Modifier.padding(token.spacing.md)) {
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = "Card with badge",
                firstLineText = "Squat Circuit",
                secondLineText = "Legs · 5 sets",
                cardSize = CardSize.Large,
                badge = {
                    Box(
                        modifier = Modifier
                            .background(
                                color = token.colors.primary,
                                shape = RoundedCornerShape(token.radius.sm),
                            )
                            .padding(horizontal = token.spacing.xs, vertical = token.spacing.xxxs),
                    ) {
                        GText(
                            text = "Beginner",
                            style = token.typography.labelSmall,
                            color = token.colors.onPrimary,
                        )
                    }
                },
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "GImageCard — Non-clickable")
@Composable
private fun PreviewNonClickable() {
    GymTheme {
        val token = GymTheme.token
        Box(modifier = Modifier.padding(token.spacing.md)) {
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = "Non-clickable card",
                firstLineText = "Rest Day",
                secondLineText = "Recovery",
                cardSize = CardSize.Medium,
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GImageCard — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        Box(modifier = Modifier.padding(token.spacing.md)) {
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = null,
                firstLineText = "Night Training",
                secondLineText = "Full Body · 45 min",
                cardSize = CardSize.Large,
                onClick = {},
            )
        }
    }
}
