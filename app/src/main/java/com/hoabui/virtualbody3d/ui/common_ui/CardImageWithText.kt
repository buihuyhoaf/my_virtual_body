package com.hoabui.virtualbody3d.ui.common_ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.core.extensions.badgeLevelBackground
import com.hoabui.virtualbody3d.core.extensions.badgeLevelBorder
import com.hoabui.virtualbody3d.domain.model.exercise.Difficulty
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.component.CardImageWithTextSizeTokens

/**
 * Visual scale for [CardImageWithText]. Dimensions come from [CardImageWithTextSizeTokens] on `bodyAnalysis`.
 */
enum class CardSize {
    Small,
    Medium,
    Large
}

/**
 * Resolves [CardImageWithTextSizeTokens] to width/height for [CardImageWithText] and matching add tiles.
 */
fun CardImageWithTextSizeTokens.cardDimensions(cardSize: CardSize): Pair<Dp, Dp> =
    when (cardSize) {
        CardSize.Small -> smallWidth to smallHeight
        CardSize.Medium -> mediumWidth to mediumHeight
        CardSize.Large -> largeWidth to largeHeight
    }

/**
 * Reusable card with image (optional badge overlay) and two lines of text.
 * Badge: soft background (surfaceSubtle), difficulty-colored border and text for semantic meaning.
 *
 * @deprecated Migrate to [com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCard].
 * Key improvements in [GImageCard]:
 * - `model: Any?` supports remote URLs, not just `@DrawableRes`
 * - `badge` is a composable slot — decoupled from the [Difficulty] domain model
 * - Single shape application (no double `.clip()` + `shape` bug)
 * - Uses [GText] internally for token-consistent typography
 */
@Deprecated(
    message = "Use GImageCard from ui.common_ui.molecule.card instead. " +
        "GImageCard supports remote image URLs (model: Any?), uses a composable badge slot, " +
        "and fixes the double clip/shape bug.",
    replaceWith = ReplaceWith(
        expression = "GImageCard(model = imageRes, contentDescription = null, firstLineText = firstLineText, secondLineText = secondLineText, cardSize = cardSize, onClick = onClick)",
        imports = ["com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCard"],
    ),
    level = DeprecationLevel.WARNING,
)
@Composable
fun CardImageWithText(
    modifier: Modifier = Modifier,
    imageRes: Int,
    firstLineText: String,
    secondLineText: String,
    badgeText: String? = null,
    badgeLevel: Enum<*>? = null,
    cardSize: CardSize = CardSize.Medium,
    onClick: () -> Unit = {}
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val (cardWidth, cardHeight) = bodyToken.cardImageWithText.cardDimensions(cardSize)
    val firstLineStyle: TextStyle
    val secondLineStyle: TextStyle
    val textPaddingH: Dp
    val textPaddingV: Dp
    val textLineSpacing: Dp
    when (cardSize) {
        CardSize.Small -> {
            firstLineStyle = token.typography.labelMedium
            secondLineStyle = token.typography.labelSmall
            textPaddingH = token.spacing.xxs
            textPaddingV = token.spacing.xxs
            textLineSpacing = token.spacing.xxxs
        }
        CardSize.Medium -> {
            firstLineStyle = token.typography.titleSmall
            secondLineStyle = token.typography.labelSmall
            textPaddingH = token.spacing.xs
            textPaddingV = token.spacing.xxs
            textLineSpacing = token.spacing.xxs
        }
        CardSize.Large -> {
            firstLineStyle = token.typography.titleSmall
            secondLineStyle = token.typography.labelSmall
            textPaddingH = token.spacing.xs
            textPaddingV = token.spacing.xs
            textLineSpacing = token.spacing.xxs
        }
    }
    val badgeOuterPadding = when (cardSize) {
        CardSize.Small -> token.spacing.xxs
        else -> token.spacing.xs
    }
    val badgeInnerHorizontal = when (cardSize) {
        CardSize.Small -> token.spacing.xxs
        else -> token.spacing.xs
    }
    val badgeInnerVertical = when (cardSize) {
        CardSize.Small -> token.spacing.xxxs
        else -> token.spacing.xxs
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val targetScale = if (isPressed) 0.96f else 1f
    val scale by animateFloatAsState(targetValue = targetScale, label = "card_image_scale")
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "card_image_alpha"
    )

    Card(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .clip(RoundedCornerShape(token.radius.lg)),
        shape = RoundedCornerShape(token.radius.lg),
        colors = CardDefaults.cardColors(containerColor = token.colors.surface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = token.elevation.level2,
            pressedElevation = token.elevation.level1
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background image filling the card
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay: transparent at top -> semi-dark at bottom
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                token.colors.backgroundScrim.copy(alpha = 0f),
                                token.colors.backgroundScrim.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // Text overlay at bottom on top of image
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = textPaddingH, vertical = textPaddingV),
                verticalArrangement = Arrangement.spacedBy(textLineSpacing),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = firstLineText,
                    style = firstLineStyle,
                    color = token.colors.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = secondLineText,
                    style = secondLineStyle,
                    color = token.colors.onPrimary.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (badgeText != null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(badgeOuterPadding),
                    shape = RoundedCornerShape(token.radius.sm),
                    color = Color.Transparent,
                    shadowElevation = token.card.elevation
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(token.radius.sm))
                            .badgeLevelBackground(badgeLevel)
                            .padding(
                                horizontal = badgeInnerHorizontal,
                                vertical = badgeInnerVertical
                            )
                    ) {
                        Text(
                            text = badgeText,
                            style = token.typography.labelSmall,
                            color = token.colors.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BadgePreview() {
    val badgeText = "Beginner"
    val token = GymTheme.token

    Box(
        modifier = Modifier
            .badgeLevelBackground(Difficulty.Beginner)
            .badgeLevelBorder(Difficulty.Beginner)
    ) {
        Text(
            text = badgeText,
            style = token.typography.labelSmall,
            color = token.colors.surface
        )
    }
}
