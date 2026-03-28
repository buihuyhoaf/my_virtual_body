package com.hoabui.virtualbody3d.ui.common_ui.molecule.card

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.component.CardImageWithTextSizeTokens

// ─────────────────────────────────────────────────────────────────────────────
// CardSize + cardDimensions (moved from deprecated CardImageWithText.kt)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Visual scale for [GImageCard]. Dimensions come from
 * [CardImageWithTextSizeTokens] on `GymTheme.token.bodyAnalysis.cardImageWithText`.
 */
enum class CardSize { Small, Medium, Large }

/**
 * Resolves [CardImageWithTextSizeTokens] to (width, height) for a given [CardSize].
 */
fun CardImageWithTextSizeTokens.cardDimensions(cardSize: CardSize): Pair<Dp, Dp> =
    when (cardSize) {
        CardSize.Small -> smallWidth to smallHeight
        CardSize.Medium -> mediumWidth to mediumHeight
        CardSize.Large -> largeWidth to largeHeight
    }

/**
 * How the optional [GImageCard] `badge` slot is framed.
 * [Holistic]: glass-style capsule behind label-only content.
 * [None]: caller owns all badge visuals; only top-end float + padding.
 */
enum class GImageCardBadgeChrome {
    Holistic,
    None,
}

// ─────────────────────────────────────────────────────────────────────────────
// Public atoms for call sites (Holistic Vitality)
// ─────────────────────────────────────────────────────────────────────────────

/** Readex Pro Regular via [androidx.compose.material3.Typography.labelSmall]. */
@Composable
fun GImageCardHolisticCapsuleLabel(text: String, modifier: Modifier = Modifier) {
    val token = GymTheme.token
    GText(
        modifier = modifier,
        text = text,
        style = token.typography.labelSmall,
        color = token.colors.textPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

/** Beginner difficulty indicator: green dot for use in [GImageCard] `textSectionLeading`. */
@Composable
fun GImageCardBeginnerDifficultyDot(modifier: Modifier = Modifier) {
    val token = GymTheme.token
    Box(
        modifier = modifier
            .size(token.spacing.xs)
            .clip(CircleShape)
            .background(token.colors.difficultyBeginnerText),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Internal helpers
// ─────────────────────────────────────────────────────────────────────────────

private data class CardSizeStyle(
    val firstLineStyle: TextStyle,
    val secondLineStyle: TextStyle,
)

@Composable
private fun resolveCardSizeStyle(cardSize: CardSize): CardSizeStyle {
    val token = GymTheme.token
    return when (cardSize) {
        CardSize.Small -> CardSizeStyle(
            firstLineStyle = token.typography.labelMedium,
            secondLineStyle = token.typography.labelSmall,
        )
        CardSize.Medium, CardSize.Large -> CardSizeStyle(
            firstLineStyle = token.typography.labelMedium,
            secondLineStyle = token.typography.labelSmall,
        )
    }
}

private fun Modifier.pressScale(interactionSource: MutableInteractionSource, scale: Float): Modifier =
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }

@Composable
private fun rememberCardBorderStroke(): BorderStroke {
    val token = GymTheme.token
    val onSurfaceInk = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    return BorderStroke(token.borderWidth.hairlineSubtle, onSurfaceInk)
}

// ─────────────────────────────────────────────────────────────────────────────
// GImageCard
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Soft-edge professional card: [Column] with square image on top, text below on [surface].
 * Optional [badge] floats on the image (glass-style when [badgeChrome] is [Holistic]).
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
    badgeChrome: GImageCardBadgeChrome = GImageCardBadgeChrome.Holistic,
    textSectionLeading: (@Composable RowScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val token = GymTheme.token
    val (cardWidth, cardHeight) = token.bodyAnalysis.cardImageWithText.cardDimensions(cardSize)
    val sizeStyle = resolveCardSizeStyle(cardSize)
    val softCorner = token.bodyAnalysis.gImageCardCornerRadius
    val cardShape = when (cardSize) {
        CardSize.Small -> RoundedCornerShape(softCorner)
        CardSize.Medium, CardSize.Large -> RoundedCornerShape(token.radius.lg)
    }
    val imageClip = RoundedCornerShape(softCorner)

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

    val containerColor = token.colors.surface
    val cardColors = CardDefaults.cardColors(containerColor = containerColor)
    val flatElevation = CardDefaults.cardElevation(
        defaultElevation = token.elevation.level0,
        pressedElevation = token.elevation.level0,
    )
    val borderStroke = rememberCardBorderStroke()

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = cardModifier,
            shape = cardShape,
            colors = cardColors,
            elevation = flatElevation,
            border = borderStroke,
            interactionSource = interactionSource,
        ) {
            GImageCardContent(
                cardWidth = cardWidth,
                imageClip = imageClip,
                model = model,
                contentDescription = contentDescription,
                sizeStyle = sizeStyle,
                firstLineText = firstLineText,
                secondLineText = secondLineText,
                badge = badge,
                badgeChrome = badgeChrome,
                textSectionLeading = textSectionLeading,
            )
        }
    } else {
        Card(
            modifier = cardModifier,
            shape = cardShape,
            colors = cardColors,
            elevation = flatElevation,
            border = borderStroke,
        ) {
            GImageCardContent(
                cardWidth = cardWidth,
                imageClip = imageClip,
                model = model,
                contentDescription = contentDescription,
                sizeStyle = sizeStyle,
                firstLineText = firstLineText,
                secondLineText = secondLineText,
                badge = badge,
                badgeChrome = badgeChrome,
                textSectionLeading = textSectionLeading,
            )
        }
    }
}

@Composable
private fun GImageCardContent(
    cardWidth: Dp,
    imageClip: RoundedCornerShape,
    model: Any?,
    contentDescription: String?,
    sizeStyle: CardSizeStyle,
    firstLineText: String,
    secondLineText: String,
    badge: (@Composable BoxScope.() -> Unit)?,
    badgeChrome: GImageCardBadgeChrome,
    textSectionLeading: (@Composable RowScope.() -> Unit)?,
) {
    val token = GymTheme.token
    val textTopInset = token.bodyAnalysis.gImageCardTextSectionTopPadding
    val onSurfaceInk = MaterialTheme.colorScheme.onSurface
    val glassBorderColor = onSurfaceInk.copy(alpha = 0.12f)

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardWidth),
        ) {
            AsyncImage(
                model = model,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(imageClip),
                contentScale = ContentScale.Crop,
            )
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(token.spacing.xs),
                ) {
                    when (badgeChrome) {
                        GImageCardBadgeChrome.Holistic -> {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(token.radius.pill))
                                    .background(token.colors.surface.copy(alpha = 0.7f))
                                    .border(
                                        width = token.borderWidth.hairlineSubtle,
                                        color = glassBorderColor,
                                        shape = RoundedCornerShape(token.radius.pill),
                                    )
                                    .padding(
                                        horizontal = token.spacing.xs,
                                        vertical = token.spacing.xxxs,
                                    ),
                            ) {
                                badge()
                            }
                        }
                        GImageCardBadgeChrome.None -> Box { badge() }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = token.spacing.xs)
                .padding(top = textTopInset),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xxxs),
        ) {
            textSectionLeading?.invoke(this)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xxxs),
            ) {
                GText(
                    text = firstLineText,
                    style = sizeStyle.firstLineStyle,
                    color = token.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                GText(
                    text = secondLineText,
                    style = sizeStyle.secondLineStyle,
                    color = token.colors.textSecondary.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
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
                secondLineText = "12 reps · 3 sets",
                cardSize = CardSize.Small,
                textSectionLeading = { GImageCardBeginnerDifficultyDot() },
                onClick = {},
            )
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = "Medium card",
                firstLineText = "Bench Press",
                secondLineText = "10 reps · 4 sets",
                cardSize = CardSize.Medium,
                onClick = {},
            )
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = "Large card",
                firstLineText = "Full Body Blast",
                secondLineText = "8 reps · 5 sets",
                cardSize = CardSize.Large,
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "GImageCard — Holistic badge")
@Composable
private fun PreviewWithBadge() {
    GymTheme {
        val token = GymTheme.token
        Box(modifier = Modifier.padding(token.spacing.md)) {
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = "Card with badge",
                firstLineText = "Squat Circuit",
                secondLineText = "12 reps · 3 sets",
                cardSize = CardSize.Large,
                badge = { GImageCardHolisticCapsuleLabel("New") },
                badgeChrome = GImageCardBadgeChrome.Holistic,
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
                secondLineText = "—",
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
                secondLineText = "12 reps · 3 sets",
                cardSize = CardSize.Large,
                onClick = {},
            )
        }
    }
}
