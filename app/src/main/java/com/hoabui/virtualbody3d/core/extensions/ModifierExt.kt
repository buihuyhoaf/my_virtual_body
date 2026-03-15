package com.hoabui.virtualbody3d.core.extensions

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.core.utils.Constants
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Hero section layer animation: alpha + scale for crossfade between 3D body and photo.
 * Use [forImageLayer] = false for BodyModelPreview, true for Image layer.
 */
@Composable
fun Modifier.heroLayerAnimation(
    showImageMode: Boolean,
    forImageLayer: Boolean
): Modifier {
    val heroAnimSpec = tween<Float>(
        durationMillis = Constants.HERO_TRANSITION_DURATION_MS,
        easing = FastOutSlowInEasing
    )
    val modelAlpha by animateFloatAsState(
        targetValue = if (showImageMode) 0f else 1f,
        animationSpec = heroAnimSpec,
        label = "hero_model_alpha"
    )
    val modelScale by animateFloatAsState(
        targetValue = if (showImageMode) Constants.HERO_MODEL_SCALE_HIDDEN else 1f,
        animationSpec = heroAnimSpec,
        label = "hero_model_scale"
    )
    val imageAlpha by animateFloatAsState(
        targetValue = if (showImageMode) 1f else 0f,
        animationSpec = heroAnimSpec,
        label = "hero_image_alpha"
    )
    val imageScale by animateFloatAsState(
        targetValue = if (showImageMode) 1f else Constants.HERO_IMAGE_SCALE_HIDDEN,
        animationSpec = heroAnimSpec,
        label = "hero_image_scale"
    )
    val alpha = if (forImageLayer) imageAlpha else modelAlpha
    val scale = if (forImageLayer) imageScale else modelScale
    return this.graphicsLayer {
        this.alpha = alpha
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Reusable modifier that shows a border when [selected] is true.
 * Use with design tokens for [color], [width], and [shape] (e.g. from GymTheme.token).
 *
 * @param selected When true, the border is applied; when false, the modifier is unchanged.
 * @param color Border color (e.g. token.colors.primary).
 * @param width Border width; prefer token.spacing.xxs for design-system consistency.
 * @param shape Shape for the border (e.g. RoundedCornerShape(token.radius.md)).
 */
fun Modifier.selectedBorder(
    selected: Boolean,
    color: Color,
    width: Dp = 2.dp,
    shape: Shape
): Modifier = if (selected) this.border(width, color, shape) else this

/**
 * Soft background for difficulty badge/chip (surfaceSubtle). Border and text use difficulty color for semantic meaning.
 */
@Composable
fun Modifier.badgeLevelBackground(level: Enum<*>?): Modifier {
    val token = GymTheme.token
    val color = when (level?.name?.lowercase()) {
        "beginner" -> token.colors.difficultyBeginnerText
        "intermediate" -> token.colors.difficultyIntermediateText
        "advanced" -> token.colors.difficultyAdvancedText
        else -> token.colors.difficultyBeginnerText
    }
    return this.background(color,)
}

/**
 * Viền theo level (cùng màu nền đậm).
 */
@Composable
fun Modifier.badgeLevelBorder(level: Enum<*>?): Modifier {
    val token = GymTheme.token
    val color = when (level?.name?.lowercase()) {
        "beginner" -> token.colors.difficultyBeginnerText
        "intermediate" -> token.colors.difficultyIntermediateText
        "advanced" -> token.colors.difficultyAdvancedText
        else -> token.colors.difficultyBeginnerText
    }
    return this.border(token.spacing.xxs, color)
}