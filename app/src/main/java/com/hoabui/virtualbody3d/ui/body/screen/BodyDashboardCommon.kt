package com.hoabui.virtualbody3d.ui.body.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun FloatingMetricChip(
    icon: ImageVector,
    value: String,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val chipShape = RoundedCornerShape(token.radius.lg)
    Surface(
        modifier = modifier.widthIn(min = 92.dp),
        shape = chipShape,
        color = Color.Transparent,
        border = BorderStroke(bodyToken.topBarBorderWidth, token.colors.surfaceBorder),
        shadowElevation = token.card.elevation
    ) {
        Row(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        listOf(token.colors.surfaceOverlay, token.colors.surfaceOverlay)
                    ),
                    shape = chipShape
                )
                .padding(
                    horizontal = token.spacing.xs,
                    vertical = bodyToken.bottomBarSelectedVerticalPadding + token.spacing.xxs * 0.5f
                ),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xxs + token.spacing.xxs * 0.5f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        color = token.colors.primarySoft,
                        shape = chipShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = token.colors.primary,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BodyScoreChip(
    score: Int,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val chipShape = RoundedCornerShape(token.radius.lg)
    val progressShape = RoundedCornerShape(token.radius.sm)
    val clamped = score.coerceIn(0, 100)
    Surface(
        modifier = modifier.widthIn(min = 92.dp),
        shape = chipShape,
        color = Color.Transparent,
        border = BorderStroke(bodyToken.topBarBorderWidth, token.colors.surfaceBorder),
        shadowElevation = token.card.elevation
    ) {
        Row(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        listOf(token.colors.surfaceOverlay, token.colors.surfaceOverlay)
                    ),
                    shape = chipShape
                )
                .padding(
                    horizontal = token.spacing.xs,
                    vertical = bodyToken.bottomBarSelectedVerticalPadding + token.spacing.xxs * 0.5f
                ),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xxs + token.spacing.xxs * 0.5f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { clamped / 100f },
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 4.dp,
                    color = token.colors.primary,
                    trackColor = token.colors.outlineSoft,
                    strokeCap = StrokeCap.Round
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(token.colors.primarySoft, progressShape)
                )
            }
            Text(
                text = clamped.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = token.colors.primary
            )
        }
    }
}
