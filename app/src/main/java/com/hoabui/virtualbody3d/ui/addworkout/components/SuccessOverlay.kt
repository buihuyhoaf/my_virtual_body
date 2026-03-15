package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Full-screen success overlay shown after workout is saved.
 * Blocks interaction; center column with scale + fade-in check icon, title, subtitle.
 * Uses [GymTheme.token] for colors, spacing, typography.
 */
@Composable
fun SuccessOverlay(
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(token.colors.surfaceOverlay.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(token.spacing.xxxl * 2),
                tint = token.colors.primary
            )
            Spacer(modifier = Modifier.height(token.spacing.lg))
            Text(
                text = stringResource(R.string.add_workout_success_title),
                style = token.typography.headlineSmall,
                color = token.colors.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(token.spacing.xs))
            Text(
                text = stringResource(R.string.add_workout_success_subtitle),
                style = token.typography.bodyMedium,
                color = token.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
