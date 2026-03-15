package com.hoabui.virtualbody3d.ui.exerciselibrary.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.hoabui.virtualbody3d.core.extensions.badgeLevelBackground
import com.hoabui.virtualbody3d.core.extensions.badgeLevelBorder
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Reusable card with image (optional badge overlay) and two lines of text.
 * Badge: soft background (surfaceSubtle), difficulty-colored border and text for semantic meaning.
 */
@Composable
fun CardImageWithText(
    modifier: Modifier = Modifier,
    imageRes: Int,
    firstLineText: String,
    secondLineText: String,
    badgeText: String? = null,
    badgeLevel: Enum<*>? = null,
    onClick: () -> Unit = {}
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.95f else 1f

    Card(
        modifier = modifier
            .width(bodyToken.bodyRegionItemWidth)
            .height(bodyToken.bodyRegionItemHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .clip(RoundedCornerShape(token.radius.md)),
        shape = RoundedCornerShape(token.radius.md),
        colors = CardDefaults.cardColors(containerColor = token.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = token.elevation.level1)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image section: Box with Image + optional badge overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(token.colors.surfaceSubtle)
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (badgeText != null) {
                    val badgeTextColor = when (badgeLevel?.name?.lowercase()) {
                        "beginner" -> token.colors.difficultyBeginnerText
                        "intermediate" -> token.colors.difficultyIntermediateText
                        "advanced" -> token.colors.difficultyAdvancedText
                        else -> token.colors.textPrimary
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(token.spacing.xxs)
                            .badgeLevelBackground(badgeLevel)
                            .badgeLevelBorder(badgeLevel)
                            .clip(RoundedCornerShape(token.radius.sm))
                            .padding(
                                horizontal = token.spacing.xs,
                                vertical = token.spacing.xxs
                            )
                    ) {
                        Text(
                            text = badgeText,
                            style = token.typography.labelSmall,
                            color = badgeTextColor
                        )
                    }
                }
            }
            // Text section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(token.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = firstLineText,
                    style = token.typography.labelMedium,
                    color = token.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = secondLineText,
                    style = token.typography.bodySmall,
                    color = token.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
