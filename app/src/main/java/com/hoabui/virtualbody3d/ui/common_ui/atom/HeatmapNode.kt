package com.hoabui.virtualbody3d.ui.common_ui.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Compact heat blob for week strips; sizing comes from caller for flexible layout context.
 */
@Composable
fun HeatmapNode(
    primaryColor: Color,
    backgroundAlpha: Float,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Box(
        modifier = modifier
            .size(token.bodyAnalysis.exerciseLibraryHeatmapDayItemHeight)
            .clip(RoundedCornerShape(token.bodyAnalysis.exerciseLibraryHeatmapDayItemCornerRadius))
            .background(primaryColor.copy(alpha = backgroundAlpha)),
        contentAlignment = Alignment.Center,
    ) {
    }
}
