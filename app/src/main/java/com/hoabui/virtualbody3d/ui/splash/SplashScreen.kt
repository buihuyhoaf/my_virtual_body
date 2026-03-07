package com.hoabui.virtualbody3d.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import kotlinx.coroutines.launch

private const val LOGO_ANIMATION_DURATION_MS = 1_200

/**
 * Splash screen: plum background, centered rounded card with app logo (no text).
 * Design: ~44% width square card, plum-accent background, logo inside with scale + fade-in animation.
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing

    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        val animSpec = tween<Float>(
            durationMillis = LOGO_ANIMATION_DURATION_MS,
            easing = FastOutSlowInEasing
        )
        launch {
            scale.animateTo(targetValue = 1f, animationSpec = animSpec)
        }
        launch {
            alpha.animateTo(targetValue = 1f, animationSpec = animSpec)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.splashBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.44f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(token.radius.xl))
                .background(colors.splashCardBackground),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.whitecat),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.xl)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        this.alpha = alpha.value
                    },
                contentScale = ContentScale.Fit
            )
        }
    }
}
