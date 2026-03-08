package com.hoabui.virtualbody3d.core.extensions

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.hoabui.virtualbody3d.core.utils.Constants

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