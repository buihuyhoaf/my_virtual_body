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
import androidx.compose.material3.Icon
import com.hoabui.virtualbody3d.ui.common_ui.atom.progress.GCircularProgress
import androidx.compose.material3.Surface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun FloatingMetricChip(
    modifier: Modifier = Modifier,
    iconResId: Int? = null,
    value: String? = null
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val chipShape = RoundedCornerShape(token.radius.lg)
    Surface(
        modifier = modifier.widthIn(min = bodyToken.metricChipMinWidth),
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
                    vertical = bodyToken.bottomBarSelectedVerticalPadding
                ),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconResId != null) {
                Box(
                    modifier = Modifier
                        .size(bodyToken.metricChipIconContainerSize)
                        .background(
                            color = token.colors.primarySoft,
                            shape = chipShape
                        ),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = null,
                        tint = token.colors.primary,
                        modifier = Modifier.size(bodyToken.metricChipIconSize)
                    )
                }
            }
            if (!value.isNullOrEmpty()){
                GText(
                    text = value,
                    style = token.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BodyScoreChip(
    modifier: Modifier = Modifier,
    score: Int,
    prominent: Boolean = false
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val chipShape = RoundedCornerShape(token.radius.lg)
    val progressShape = RoundedCornerShape(token.radius.sm)
    val clamped = score.coerceIn(0, 100)
    val textStyle = if (prominent) token.typography.titleMedium else token.typography.labelLarge
    Surface(
        modifier = modifier.widthIn(
            min = if (prominent) bodyToken.scoreChipProminentMinWidth else bodyToken.scoreChipMinWidth
        ),
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
                    horizontal = if (prominent) token.spacing.md else token.spacing.xs,
                    vertical = bodyToken.bottomBarSelectedVerticalPadding
                ),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                GCircularProgress(
                    progress = clamped / 100f,
                    modifier = Modifier.size(
                        if (prominent) bodyToken.scoreChipProminentProgressSize else bodyToken.scoreChipProgressSize
                    ),
                    strokeWidth = if (prominent) {
                        bodyToken.scoreChipProminentStrokeWidth
                    } else {
                        bodyToken.scoreChipStrokeWidth
                    },
                    trackColor = token.colors.outlineSoft,
                )
                Box(
                    modifier = Modifier
                        .size(
                            if (prominent) {
                                bodyToken.scoreChipProminentInnerSize
                            } else {
                                bodyToken.scoreChipInnerSize
                            }
                        )
                        .background(token.colors.primarySoft, progressShape)
                )
            }
            GText(
                text = clamped.toString(),
                style = textStyle,
                fontWeight = FontWeight.Bold,
                color = token.colors.primary
            )
        }
    }
}
