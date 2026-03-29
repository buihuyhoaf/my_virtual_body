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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
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
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import com.hoabui.virtualbody3d.ui.theme.tokens.component.CardImageWithTextSizeTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAspectRatioTokens

// ─────────────────────────────────────────────────────────────────────────────
// CardSize + cardDimensions (moved from deprecated CardImageWithText.kt)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Visual scale for [GImageCard]. Dimensions come from
 * [CardImageWithTextSizeTokens] on `GymTheme.token.bodyAnalysis.cardImageWithText`.
 */
enum class CardSize {
    Small,
    Medium,
    Large,
    /** Dense tile for exercise library horizontal rows (3:2 image slot). */
    ExerciseLibraryTile,
}

/**
 * Resolves [CardImageWithTextSizeTokens] to (width, height) for a given [CardSize].
 */
fun CardImageWithTextSizeTokens.cardDimensions(cardSize: CardSize): Pair<Dp, Dp> =
    when (cardSize) {
        CardSize.Small -> smallWidth to smallHeight
        CardSize.Medium -> mediumWidth to mediumHeight
        CardSize.Large -> largeWidth to largeHeight
        CardSize.ExerciseLibraryTile -> exerciseLibraryWidth to exerciseLibraryHeight
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

/** Accent dot for use in [GImageCard] `textSectionLeading` (e.g. previews). */
@Composable
fun GImageCardAccentDot(modifier: Modifier = Modifier) {
    val token = GymTheme.token
    Box(
        modifier = modifier
            .size(token.spacing.xs)
            .clip(CircleShape)
            .background(token.colors.primary),
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
        CardSize.ExerciseLibraryTile -> CardSizeStyle(
            firstLineStyle = token.typography.labelLarge,
            secondLineStyle = token.typography.bodySmall,
        )
    }
}

private fun Modifier.pressScale(interactionSource: MutableInteractionSource, scale: Float): Modifier =
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }

// ─────────────────────────────────────────────────────────────────────────────
// GImageCard
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Soft-edge professional card: [Column] with image on top, text below on [surface].
 * Image slot is square for [CardSize.Small]/[CardSize.Medium]/[CardSize.Large];
 * [CardSize.ExerciseLibraryTile] uses a 3:2 image slot from [PrimitiveAspectRatioTokens].
 * Optional [badge] floats on the image (glass-style when [badgeChrome] is [Holistic]).
 *
 * @param trailingOverlayEnd Optional overlay aligned to the bottom-end of the **whole** card (e.g. library quick-add).
 *
 * @param selectionHighlight When `true`, uses primary border and subtle elevation for selected state.
 * @param onLongClick Optional long-press handler (e.g. open detail). Uses [combinedClickable] when non-null.
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
    /** Optional overlay on the image (e.g. bottom-end quick action); drawn above the image, below badge hit-testing order depends on declaration order. */
    imageOverlayEnd: (@Composable BoxScope.() -> Unit)? = null,
    /** Optional overlay on the full card (library Smart-Add); drawn above text and image. */
    trailingOverlayEnd: (@Composable BoxScope.() -> Unit)? = null,
    textSectionLeading: (@Composable RowScope.() -> Unit)? = null,
    selectionHighlight: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    val token = GymTheme.token
    val (cardWidth, cardHeight) = token.bodyAnalysis.cardImageWithText.cardDimensions(cardSize)
    val sizeStyle = resolveCardSizeStyle(cardSize)
    val softCorner = token.bodyAnalysis.gImageCardCornerRadius
    val cardShape = when (cardSize) {
        CardSize.Small -> RoundedCornerShape(softCorner)
        CardSize.Medium, CardSize.Large, CardSize.ExerciseLibraryTile -> RoundedCornerShape(token.radius.lg)
    }
    val aspect = PrimitiveAspectRatioTokens
    val imageSlotHeight = when (cardSize) {
        CardSize.ExerciseLibraryTile ->
            cardWidth * aspect.G_IMAGE_CARD_EXERCISE_LIBRARY_ASPECT_H.toFloat() /
                aspect.G_IMAGE_CARD_EXERCISE_LIBRARY_ASPECT_W.toFloat()
        else -> cardWidth
    }
    val imageClip = RoundedCornerShape(softCorner)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && (onClick != null || onLongClick != null)) 0.96f else 1f,
        label = "g_image_card_scale",
    )

    val cardModifier = modifier
        .width(cardWidth)
        .height(cardHeight)
        .pressScale(interactionSource, scale)

    val cardElevation = if (selectionHighlight) token.elevation.level1 else token.elevation.level0
    val borderStroke = if (selectionHighlight) {
        BorderStroke(token.borderWidth.thin, token.colors.primary)
    } else {
        BorderStroke(token.borderWidth.hairline, token.colors.borderSubtle)
    }

    when {
        onLongClick != null -> {
            GCard(
                onClick = onClick ?: {},
                onLongClick = onLongClick,
                modifier = cardModifier,
                shape = cardShape,
                elevation = cardElevation,
                pressedElevation = token.elevation.level0,
                border = borderStroke,
                treatment = GSurfaceTreatment.Flat,
                contentModifier = Modifier.fillMaxSize(),
                interactionSource = interactionSource,
            ) {
                GImageCardStack(
                    cardWidth = cardWidth,
                    imageSlotHeight = imageSlotHeight,
                    imageClip = imageClip,
                    cardSize = cardSize,
                    model = model,
                    contentDescription = contentDescription,
                    sizeStyle = sizeStyle,
                    firstLineText = firstLineText,
                    secondLineText = secondLineText,
                    badge = badge,
                    badgeChrome = badgeChrome,
                    imageOverlayEnd = imageOverlayEnd,
                    trailingOverlayEnd = trailingOverlayEnd,
                    textSectionLeading = textSectionLeading,
                    selectionHighlight = selectionHighlight,
                )
            }
        }
        onClick != null -> {
            GCard(
                onClick = onClick,
                modifier = cardModifier,
                shape = cardShape,
                elevation = cardElevation,
                pressedElevation = token.elevation.level0,
                border = borderStroke,
                treatment = GSurfaceTreatment.Flat,
                contentModifier = Modifier.fillMaxSize(),
                interactionSource = interactionSource,
            ) {
                GImageCardStack(
                    cardWidth = cardWidth,
                    imageSlotHeight = imageSlotHeight,
                    imageClip = imageClip,
                    cardSize = cardSize,
                    model = model,
                    contentDescription = contentDescription,
                    sizeStyle = sizeStyle,
                    firstLineText = firstLineText,
                    secondLineText = secondLineText,
                    badge = badge,
                    badgeChrome = badgeChrome,
                    imageOverlayEnd = imageOverlayEnd,
                    trailingOverlayEnd = trailingOverlayEnd,
                    textSectionLeading = textSectionLeading,
                    selectionHighlight = selectionHighlight,
                )
            }
        }
        else -> {
            GCard(
                modifier = cardModifier,
                shape = cardShape,
                elevation = cardElevation,
                pressedElevation = token.elevation.level0,
                border = borderStroke,
                treatment = GSurfaceTreatment.Flat,
                contentModifier = Modifier.fillMaxSize(),
            ) {
                GImageCardStack(
                    cardWidth = cardWidth,
                    imageSlotHeight = imageSlotHeight,
                    imageClip = imageClip,
                    cardSize = cardSize,
                    model = model,
                    contentDescription = contentDescription,
                    sizeStyle = sizeStyle,
                    firstLineText = firstLineText,
                    secondLineText = secondLineText,
                    badge = badge,
                    badgeChrome = badgeChrome,
                    imageOverlayEnd = imageOverlayEnd,
                    trailingOverlayEnd = trailingOverlayEnd,
                    textSectionLeading = textSectionLeading,
                    selectionHighlight = selectionHighlight,
                )
            }
        }
    }
}

@Composable
private fun GImageCardStack(
    cardWidth: Dp,
    imageSlotHeight: Dp,
    imageClip: RoundedCornerShape,
    cardSize: CardSize,
    model: Any?,
    contentDescription: String?,
    sizeStyle: CardSizeStyle,
    firstLineText: String,
    secondLineText: String,
    badge: (@Composable BoxScope.() -> Unit)?,
    badgeChrome: GImageCardBadgeChrome,
    imageOverlayEnd: (@Composable BoxScope.() -> Unit)?,
    trailingOverlayEnd: (@Composable BoxScope.() -> Unit)?,
    textSectionLeading: (@Composable RowScope.() -> Unit)?,
    selectionHighlight: Boolean,
) {
    val token = GymTheme.token
    val reserveQuickAddEndInset = trailingOverlayEnd != null && cardSize == CardSize.ExerciseLibraryTile
    Box(modifier = Modifier.fillMaxSize()) {
        if (selectionHighlight) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        color = token.colors.primary.copy(
                            alpha = token.bodyAnalysis.gImageCardSelectedSurfaceTintAlpha,
                        ),
                    ),
            )
        }
        GImageCardContent(
            cardWidth = cardWidth,
            imageSlotHeight = imageSlotHeight,
            imageClip = imageClip,
            cardSize = cardSize,
            model = model,
            contentDescription = contentDescription,
            sizeStyle = sizeStyle,
            firstLineText = firstLineText,
            secondLineText = secondLineText,
            badge = badge,
            badgeChrome = badgeChrome,
            imageOverlayEnd = imageOverlayEnd,
            textSectionLeading = textSectionLeading,
            reserveQuickAddEndInset = reserveQuickAddEndInset,
        )
        trailingOverlayEnd?.invoke(this@Box)
    }
}

@Composable
private fun GImageCardContent(
    cardWidth: Dp,
    imageSlotHeight: Dp,
    imageClip: RoundedCornerShape,
    cardSize: CardSize,
    model: Any?,
    contentDescription: String?,
    sizeStyle: CardSizeStyle,
    firstLineText: String,
    secondLineText: String,
    badge: (@Composable BoxScope.() -> Unit)?,
    badgeChrome: GImageCardBadgeChrome,
    imageOverlayEnd: (@Composable BoxScope.() -> Unit)?,
    textSectionLeading: (@Composable RowScope.() -> Unit)?,
    reserveQuickAddEndInset: Boolean,
) {
    val token = GymTheme.token
    val isLibraryTile = cardSize == CardSize.ExerciseLibraryTile
    val textTopInset = if (isLibraryTile) token.spacing.xxs else token.bodyAnalysis.gImageCardTextSectionTopPadding
    val textHorizontalPadding = if (isLibraryTile) token.spacing.xxs else token.spacing.xs
    val glassBorderColor = token.colors.outlineSoft
    val subtitleColor = if (isLibraryTile) {
        token.colors.textSecondary
    } else {
        token.colors.textSecondary.copy(alpha = 0.7f)
    }
    val textRowEndPadding = if (reserveQuickAddEndInset) {
        token.bodyAnalysis.exerciseLibraryQuickAddTextInset
    } else {
        0.dp
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageSlotHeight),
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
                                        width = token.borderWidth.hairline,
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
            if (imageOverlayEnd != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(token.spacing.xs),
                ) {
                    imageOverlayEnd.invoke(this)
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = textHorizontalPadding)
                .padding(top = textTopInset, end = textRowEndPadding),
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
                    color = subtitleColor,
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

@Preview(showBackground = true, name = "GImageCard — Selected")
@Composable
private fun PreviewSelected() {
    GymTheme {
        val token = GymTheme.token
        Box(modifier = Modifier.padding(token.spacing.md)) {
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = "Selected card",
                firstLineText = "Added to workout",
                secondLineText = "Quick add",
                cardSize = CardSize.Medium,
                selectionHighlight = true,
                onClick = {},
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GImageCard — Selected dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewSelectedDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        Box(modifier = Modifier.padding(token.spacing.md)) {
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = null,
                firstLineText = "Added to workout",
                secondLineText = "Quick add",
                cardSize = CardSize.Medium,
                selectionHighlight = true,
                onClick = {},
            )
        }
    }
}

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
                textSectionLeading = { GImageCardAccentDot() },
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

@Preview(showBackground = true, name = "GImageCard — Library tile + Smart-Add")
@Composable
private fun PreviewLibraryTileQuickAdd() {
    GymTheme {
        val token = GymTheme.token
        Box(modifier = Modifier.padding(token.spacing.md)) {
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = "Bench",
                firstLineText = "Very long exercise name truncation test",
                secondLineText = "Chest · Barbell",
                cardSize = CardSize.ExerciseLibraryTile,
                trailingOverlayEnd = {
                    Surface(
                        onClick = {},
                        shape = RoundedCornerShape(token.radius.sm),
                        color = token.colors.surface.copy(alpha = 0.92f),
                        shadowElevation = token.elevation.level0,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(token.spacing.xs)
                            .sizeIn(
                                minWidth = token.spacing.xxl,
                                minHeight = token.spacing.xxl,
                            ),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier.size(token.bodyAnalysis.exerciseLibraryQuickAddIconContainerSize),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = token.colors.primary,
                                )
                            }
                        }
                    }
                },
                selectionHighlight = true,
                onClick = {},
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
