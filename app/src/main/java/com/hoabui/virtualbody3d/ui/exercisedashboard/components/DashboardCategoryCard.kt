package com.hoabui.virtualbody3d.ui.exercisedashboard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.ui.common_ui.atom.image.GradientScrim
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens

@Composable
fun DashboardCategoryCard(
    title: String,
    imageRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentAboveImage: (@Composable BoxScope.() -> Unit)? = null,
) {
    val token = GymTheme.token
    val dash = token.dashboardExercise
    val haptics = LocalHapticFeedback.current
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) {
            dash.categoryCardPressScaleTarget
        } else {
            PrimitiveAlphaTokens.GRAPHICS_SCALE_NEUTRAL
        },
        animationSpec = tween(
            durationMillis = token.motion.duration.standard,
            easing = token.motion.easing.standard,
        ),
        label = "dash_category_press_scale",
    )
    val clipShape = RoundedCornerShape(dash.categoryCardCornerRadius)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dash.categoryCardHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(clipShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                    onClick()
                },
            ),
    ) {
        AsyncImage(
            model = imageRes,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        GradientScrim(
            modifier = Modifier.fillMaxSize(),
            overlayBaseColor = token.colors.textBlack,
            gradientStartAlpha = dash.categoryCardTextScrimStartAlpha,
            gradientEndAlpha = dash.categoryCardTextScrimEndAlpha,
        )
        if (contentAboveImage != null) {
            Box(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()) {
                contentAboveImage()
            }
        }
        GText(
            text = title,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = token.spacing.sm),
            style = token.typography.labelLarge,
            color = token.colors.surface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
