package com.hoabui.virtualbody3d.ui.common_ui.molecule.card

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Surface
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GIconButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GIconButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.icon.GIcon
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.icons.ExerciseLibraryPhosphorIcons
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import com.hoabui.virtualbody3d.ui.theme.tokens.component.CardImageWithTextSizeTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens
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

private fun Modifier.cardPressAndSelectionScale(
    pressScale: Float,
    selectionScale: Float,
): Modifier =
    this.graphicsLayer {
        val s = selectionScale * pressScale
        scaleX = s
        scaleY = s
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
 * @param trailingOverlayEnd Optional overlay on the **whole** card [BoxScope]; caller sets alignment (e.g. [Alignment.TopEnd] or [Alignment.BottomEnd]).
 * @param reserveExerciseLibraryTextEndInset When `true` and [cardSize] is [CardSize.ExerciseLibraryTile], reserves right padding on the text row for a **bottom-end** overlay. Use `false` for **top-end** actions so titles use full width.
 *
 * @param imageOverlayBottomFullWidth Optional overlay pinned to the bottom of the image, full width (e.g. gradient scrim + title).
 * @param showTextSection When `false`, only the image area is shown; card height collapses to [imageSlotHeight].
 * @param selectionScale Persistent scale when [selectionHighlight] (e.g. 1.05); multiplied with press feedback.
 * @param selectionBorderStroke When non-null and [selectionHighlight], used instead of the default primary border.
 * @param weakSelectionBorderless When `true` with [weakSelectionHighlight], uses the default subtle border (no in-cart ring).
 * @param selectionTintAlpha When non-null, overrides alpha for the strong selection primary overlay.
 * @param weakSelectionTintAlpha When non-null, overrides alpha for the weak selection primary overlay.
 */
@Composable
fun GImageCard(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    firstLineText: String,
    secondLineText: String,
    /**
     * When non-null, replaces plain [secondLineText] for the second line (e.g. multi-color [AnnotatedString]).
     * [secondLineColor] is ignored in that case.
     */
    secondLineAnnotatedText: AnnotatedString? = null,
    /** When non-null, used for [secondLineText] instead of the default muted subtitle color. */
    secondLineColor: Color? = null,
    cardSize: CardSize = CardSize.Medium,
    badge: (@Composable BoxScope.() -> Unit)? = null,
    badgeChrome: GImageCardBadgeChrome = GImageCardBadgeChrome.Holistic,
    /** Optional overlay on the image bottom, full width; drawn above the image, below top badges. */
    imageOverlayBottomFullWidth: (@Composable BoxScope.() -> Unit)? = null,
    /** Optional overlay on the image (e.g. bottom-end quick action); drawn above the image, below badge hit-testing order depends on declaration order. */
    imageOverlayEnd: (@Composable BoxScope.() -> Unit)? = null,
    /** Optional overlay on the image top-trailing corner (e.g. library list toggle). Composed after [badge] so it wins z-order in that corner. */
    imageOverlayTopEnd: (@Composable BoxScope.() -> Unit)? = null,
    /**
     * When `true`, offsets [imageOverlayTopEnd] with [GymTheme.token.spacing.xs] from the image edge.
     * Set `false` to anchor the overlay flush to the image top-end (sticker / 48dp hit-zone alignment).
     */
    imageOverlayTopEndEdgeInset: Boolean = true,
    /** Optional overlay on the full card (library Smart-Add); drawn above text and image. */
    trailingOverlayEnd: (@Composable BoxScope.() -> Unit)? = null,
    reserveExerciseLibraryTextEndInset: Boolean = false,
    textSectionLeading: (@Composable RowScope.() -> Unit)? = null,
    showTextSection: Boolean = true,
    selectionScale: Float = 1f,
    selectionBorderStroke: BorderStroke? = null,
    weakSelectionBorderless: Boolean = false,
    selectionTintAlpha: Float? = null,
    weakSelectionTintAlpha: Float? = null,
    selectionHighlight: Boolean = false,
    weakSelectionHighlight: Boolean = false,
    onClick: (() -> Unit)? = null,
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
    val pressScaleAnimated by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.96f else 1f,
        label = "g_image_card_scale",
    )
    val pressScaleForModifier = if (onClick != null) pressScaleAnimated else 1f

    val effectiveCardHeight = if (showTextSection) cardHeight else imageSlotHeight

    val cardModifier = modifier
        .width(cardWidth)
        .height(effectiveCardHeight)
        .cardPressAndSelectionScale(pressScaleForModifier, selectionScale)

    val cardElevation = if (selectionHighlight) token.elevation.level1 else token.elevation.level0
    val borderStroke = when {
        selectionHighlight ->
            selectionBorderStroke ?: BorderStroke(token.borderWidth.thin, token.colors.primary)
        weakSelectionHighlight && weakSelectionBorderless ->
            BorderStroke(token.borderWidth.hairline, token.colors.borderSubtle)
        weakSelectionHighlight -> BorderStroke(token.borderWidth.thin, token.colors.outlineSoft)
        else -> BorderStroke(token.borderWidth.hairline, token.colors.borderSubtle)
    }

    when {
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
                    secondLineAnnotatedText = secondLineAnnotatedText,
                    secondLineColor = secondLineColor,
                    badge = badge,
                    badgeChrome = badgeChrome,
                    imageOverlayBottomFullWidth = imageOverlayBottomFullWidth,
                    imageOverlayEnd = imageOverlayEnd,
                    imageOverlayTopEnd = imageOverlayTopEnd,
                    imageOverlayTopEndEdgeInset = imageOverlayTopEndEdgeInset,
                    trailingOverlayEnd = trailingOverlayEnd,
                    reserveExerciseLibraryTextEndInset = reserveExerciseLibraryTextEndInset,
                    textSectionLeading = textSectionLeading,
                    showTextSection = showTextSection,
                    selectionHighlight = selectionHighlight,
                    weakSelectionHighlight = weakSelectionHighlight,
                    selectionTintAlpha = selectionTintAlpha,
                    weakSelectionTintAlpha = weakSelectionTintAlpha,
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
                    secondLineAnnotatedText = secondLineAnnotatedText,
                    secondLineColor = secondLineColor,
                    badge = badge,
                    badgeChrome = badgeChrome,
                    imageOverlayBottomFullWidth = imageOverlayBottomFullWidth,
                    imageOverlayEnd = imageOverlayEnd,
                    imageOverlayTopEnd = imageOverlayTopEnd,
                    imageOverlayTopEndEdgeInset = imageOverlayTopEndEdgeInset,
                    trailingOverlayEnd = trailingOverlayEnd,
                    reserveExerciseLibraryTextEndInset = reserveExerciseLibraryTextEndInset,
                    textSectionLeading = textSectionLeading,
                    showTextSection = showTextSection,
                    selectionHighlight = selectionHighlight,
                    weakSelectionHighlight = weakSelectionHighlight,
                    selectionTintAlpha = selectionTintAlpha,
                    weakSelectionTintAlpha = weakSelectionTintAlpha,
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
    secondLineAnnotatedText: AnnotatedString?,
    secondLineColor: Color?,
    badge: (@Composable BoxScope.() -> Unit)?,
    badgeChrome: GImageCardBadgeChrome,
    imageOverlayBottomFullWidth: (@Composable BoxScope.() -> Unit)?,
    imageOverlayEnd: (@Composable BoxScope.() -> Unit)?,
    imageOverlayTopEnd: (@Composable BoxScope.() -> Unit)?,
    imageOverlayTopEndEdgeInset: Boolean,
    trailingOverlayEnd: (@Composable BoxScope.() -> Unit)?,
    reserveExerciseLibraryTextEndInset: Boolean,
    textSectionLeading: (@Composable RowScope.() -> Unit)?,
    showTextSection: Boolean,
    selectionHighlight: Boolean,
    weakSelectionHighlight: Boolean,
    selectionTintAlpha: Float?,
    weakSelectionTintAlpha: Float?,
) {
    val token = GymTheme.token
    val strongTint = selectionTintAlpha ?: token.bodyAnalysis.gImageCardSelectedSurfaceTintAlpha
    val weakTint = weakSelectionTintAlpha ?: token.bodyAnalysis.gImageCardWeakSelectionSurfaceTintAlpha
    val reserveQuickAddEndInset =
        trailingOverlayEnd != null &&
            cardSize == CardSize.ExerciseLibraryTile &&
            reserveExerciseLibraryTextEndInset
    Box(modifier = Modifier.fillMaxSize()) {
        if (selectionHighlight) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        color = token.colors.primary.copy(alpha = strongTint),
                    ),
            )
        } else if (weakSelectionHighlight) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        color = token.colors.primary.copy(alpha = weakTint),
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
            secondLineAnnotatedText = secondLineAnnotatedText,
            secondLineColor = secondLineColor,
            badge = badge,
            badgeChrome = badgeChrome,
            imageOverlayBottomFullWidth = imageOverlayBottomFullWidth,
            imageOverlayEnd = imageOverlayEnd,
            imageOverlayTopEnd = imageOverlayTopEnd,
            imageOverlayTopEndEdgeInset = imageOverlayTopEndEdgeInset,
            textSectionLeading = textSectionLeading,
            reserveQuickAddEndInset = reserveQuickAddEndInset,
            showTextSection = showTextSection,
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
    secondLineAnnotatedText: AnnotatedString?,
    secondLineColor: Color?,
    badge: (@Composable BoxScope.() -> Unit)?,
    badgeChrome: GImageCardBadgeChrome,
    imageOverlayBottomFullWidth: (@Composable BoxScope.() -> Unit)?,
    imageOverlayEnd: (@Composable BoxScope.() -> Unit)?,
    imageOverlayTopEnd: (@Composable BoxScope.() -> Unit)?,
    imageOverlayTopEndEdgeInset: Boolean,
    textSectionLeading: (@Composable RowScope.() -> Unit)?,
    reserveQuickAddEndInset: Boolean,
    showTextSection: Boolean,
) {
    val token = GymTheme.token
    val isLibraryTile = cardSize == CardSize.ExerciseLibraryTile
    val textTopInset = if (isLibraryTile) token.spacing.xxs else token.bodyAnalysis.gImageCardTextSectionTopPadding
    val textHorizontalPadding = if (isLibraryTile) token.spacing.xxs else token.spacing.xs
    val glassBorderColor = token.colors.outlineSoft
    val defaultSubtitleColor = if (isLibraryTile) {
        token.colors.textSecondary
    } else {
        token.colors.textSecondary.copy(alpha = PrimitiveAlphaTokens.IMAGE_CARD_OVERLAY_MEDIUM)
    }
    val subtitleColor = secondLineColor ?: defaultSubtitleColor
    val textRowEndPadding = if (reserveQuickAddEndInset) {
        token.bodyAnalysis.exerciseLibraryQuickAddTextInset
    } else {
        token.spacing.none
    }

    val imageBoxModifier = if (showTextSection) {
        Modifier
            .fillMaxWidth()
            .height(imageSlotHeight)
    } else {
        Modifier.fillMaxSize()
    }

    @Composable
    fun ImageSlot() {
        Box(modifier = imageBoxModifier) {
            AsyncImage(
                model = model,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(imageClip),
                contentScale = ContentScale.Crop,
            )
            if (imageOverlayBottomFullWidth != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                ) {
                    imageOverlayBottomFullWidth.invoke(this)
                }
            }
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
                                    .background(
                                        token.colors.surface.copy(
                                            alpha = PrimitiveAlphaTokens.IMAGE_CARD_OVERLAY_MEDIUM,
                                        ),
                                    )
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
            if (imageOverlayTopEnd != null) {
                val overlayModifier =
                    if (imageOverlayTopEndEdgeInset) {
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = token.spacing.xs, end = token.spacing.xs)
                    } else {
                        Modifier.align(Alignment.TopEnd)
                    }
                Box(modifier = overlayModifier) {
                    imageOverlayTopEnd.invoke(this)
                }
            }
        }
    }

    if (showTextSection) {
        Column(modifier = Modifier.fillMaxSize()) {
            ImageSlot()
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
                    if (secondLineAnnotatedText != null) {
                        GText(
                            text = secondLineAnnotatedText,
                            style = sizeStyle.secondLineStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    } else {
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
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            ImageSlot()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

/** Preview-only twin of the exercise library list corner toggle sticker (circular scrim + glyph). */
@Composable
private fun PreviewExerciseLibraryListCornerToggleSticker(inCart: Boolean) {
    val token = GymTheme.token
    Box(
        modifier = Modifier
            .size(token.bodyAnalysis.exerciseLibraryCornerStickerTouchTargetSize)
            .clickable(role = Role.Button, onClick = {}),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(token.bodyAnalysis.exerciseLibraryCornerStickerDiameter)
                .clip(CircleShape)
                .background(
                    token.colors.surfaceSubtle.copy(alpha = PrimitiveAlphaTokens.SUBTLE_LAYER),
                ),
            contentAlignment = Alignment.Center,
        ) {
            GIcon(
                imageVector = if (inCart) {
                    ExerciseLibraryPhosphorIcons.listToggleInCart
                } else {
                    ExerciseLibraryPhosphorIcons.listToggleNotInCart
                },
                contentDescription = null,
                modifier = Modifier.size(token.bodyAnalysis.exerciseLibraryCornerActionGlyphSize),
                tint = token.colors.primary,
            )
        }
    }
}

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

@Preview(showBackground = true, name = "GImageCard — Library tile + top toggle (add)")
@Composable
private fun PreviewLibraryTileTopToggleAdd() {
    GymTheme {
        val token = GymTheme.token
        Box(modifier = Modifier.padding(token.spacing.md)) {
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = "Bench",
                firstLineText = "Very long exercise name truncation test",
                secondLineText = "Chest · Barbell",
                cardSize = CardSize.ExerciseLibraryTile,
                imageOverlayTopEnd = {
                    PreviewExerciseLibraryListCornerToggleSticker(inCart = false)
                },
                imageOverlayTopEndEdgeInset = false,
                reserveExerciseLibraryTextEndInset = false,
                weakSelectionHighlight = false,
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "GImageCard — Library tile + top toggle (in cart)")
@Composable
private fun PreviewLibraryTileTopToggleInCart() {
    GymTheme {
        val token = GymTheme.token
        Box(modifier = Modifier.padding(token.spacing.md)) {
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = "Bench",
                firstLineText = "Very long exercise name truncation test",
                secondLineText = "Chest · Barbell",
                cardSize = CardSize.ExerciseLibraryTile,
                imageOverlayTopEnd = {
                    PreviewExerciseLibraryListCornerToggleSticker(inCart = true)
                },
                imageOverlayTopEndEdgeInset = false,
                reserveExerciseLibraryTextEndInset = false,
                selectionHighlight = true,
                onClick = {},
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GImageCard — Library top toggle (in cart) — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewLibraryTileTopToggleInCartDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        Box(modifier = Modifier.padding(token.spacing.md)) {
            GImageCard(
                model = R.drawable.body_unsplash,
                contentDescription = "Bench",
                firstLineText = "Very long exercise name truncation test",
                secondLineText = "Chest · Barbell",
                cardSize = CardSize.ExerciseLibraryTile,
                imageOverlayTopEnd = {
                    PreviewExerciseLibraryListCornerToggleSticker(inCart = true)
                },
                imageOverlayTopEndEdgeInset = false,
                reserveExerciseLibraryTextEndInset = false,
                selectionHighlight = true,
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "GImageCard — Library tile + bottom overlay (legacy inset)")
@Composable
private fun PreviewLibraryTileBottomOverlayLegacy() {
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
                        color = token.colors.surface.copy(
                            alpha = PrimitiveAlphaTokens.IMAGE_CARD_TRAILING_OVERLAY_SURFACE,
                        ),
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
                            GIcon(
                                imageVector = ExerciseLibraryPhosphorIcons.listToggleNotInCart,
                                contentDescription = null,
                                modifier = Modifier.size(token.bodyAnalysis.exerciseLibraryQuickAddIconContainerSize),
                                tint = token.colors.primary,
                            )
                        }
                    }
                },
                reserveExerciseLibraryTextEndInset = true,
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
