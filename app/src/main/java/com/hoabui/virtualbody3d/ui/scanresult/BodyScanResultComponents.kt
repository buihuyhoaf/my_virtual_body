package com.hoabui.virtualbody3d.ui.scanresult

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Reusable section header for grouped metrics (e.g. "Body Composition", "Muscle–Fat Analysis").
 */
@Composable
fun MetricSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val token = GymTheme.token
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.md)
    ) {
        Text(
            text = title,
            style = token.typography.titleMedium,
            color = token.colors.textPrimary
        )
        content()
    }
}

/**
 * Simple row: label on the left, value on the right.
 */
@Composable
fun MetricRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = token.spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary
        )
        Text(
            text = value,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
    }
}

/**
 * Row with label, value, and a progress bar indicating position within [range].
 * [currentValue] is used to compute progress within the range (clamped to 0..1 for display).
 */
@Composable
fun ProgressMetricRow(
    label: String,
    value: String,
    currentValue: Float,
    range: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val rangeSpan = range.endInclusive - range.start
    val progress = if (rangeSpan > 0f) {
        ((currentValue - range.start) / rangeSpan).coerceIn(0f, 1f)
    } else 0.5f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = token.spacing.xs),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = token.typography.bodyMedium,
                color = token.colors.textSecondary
            )
            Text(
                text = value,
                style = token.typography.bodyMedium,
                color = token.colors.textPrimary
            )
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(token.radius.sm)),
            color = token.colors.primary,
            trackColor = token.colors.surfaceOverlay
        )
    }
}

/**
 * Data for a single segment in segmental analysis (e.g. Left Arm, Right Arm).
 */
data class SegmentalMetric(
    val label: String,
    val value: String
)

/**
 * Grid of segmental metrics (e.g. Left Arm, Right Arm, Trunk, Left Leg, Right Leg).
 */
@Composable
fun SegmentalGrid(
    items: List<SegmentalMetric>,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val columns = 2
    val rows = (items.size + columns - 1) / columns

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.md)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.md)
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < items.size) {
                        val item = items[index]
                        SegmentalGridItem(
                            label = item.label,
                            value = item.value,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SegmentalGridItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(token.radius.md))
            .background(token.colors.surfaceOverlay)
            .padding(token.spacing.md),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
    ) {
        Text(
            text = label,
            style = token.typography.labelMedium,
            color = token.colors.textSecondary
        )
        Text(
            text = value,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
    }
}
