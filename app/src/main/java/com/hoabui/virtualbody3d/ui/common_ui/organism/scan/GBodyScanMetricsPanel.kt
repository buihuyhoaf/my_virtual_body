package com.hoabui.virtualbody3d.ui.common_ui.organism.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.progress.GProgressBar
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

data class GSegmentalMetricUiModel(
    val label: String,
    val value: String,
)

sealed interface GMetricRowUiModel {
    val id: String

    data class ValueRow(
        override val id: String,
        val label: String,
        val value: String,
    ) : GMetricRowUiModel

    data class ProgressRow(
        override val id: String,
        val label: String,
        val value: String,
        val progress: Float,
    ) : GMetricRowUiModel

    data class SegmentalGridRow(
        override val id: String,
        val items: List<GSegmentalMetricUiModel>,
    ) : GMetricRowUiModel
}

data class GMetricSectionUiModel(
    val id: String,
    val title: String,
    val rows: List<GMetricRowUiModel>,
)

@Composable
fun GBodyScanMetricsPanel(
    sections: List<GMetricSectionUiModel>,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.lg),
    ) {
        sections.forEach { section ->
            GMetricSectionCard(section = section)
        }
    }
}

@Composable
private fun GMetricSectionCard(section: GMetricSectionUiModel) {
    val token = GymTheme.token
    GCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = token.colors.surfaceOverlay,
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md),
        ) {
            GText(
                text = section.title,
                style = token.typography.titleMedium,
                color = token.colors.textPrimary,
            )
            section.rows.forEach { row ->
                when (row) {
                    is GMetricRowUiModel.ValueRow -> {
                        GMetricValueRow(label = row.label, value = row.value)
                    }

                    is GMetricRowUiModel.ProgressRow -> {
                        GMetricProgressRow(
                            label = row.label,
                            value = row.value,
                            progress = row.progress.coerceIn(0f, 1f),
                        )
                    }

                    is GMetricRowUiModel.SegmentalGridRow -> {
                        GSegmentalGrid(items = row.items)
                    }
                }
            }
        }
    }
}

@Composable
private fun GMetricValueRow(label: String, value: String) {
    val token = GymTheme.token
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GText(
            text = label,
            style = token.typography.bodySmall,
            color = token.colors.textSecondary,
        )
        GText(
            text = value,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary,
        )
    }
}

@Composable
private fun GMetricProgressRow(
    label: String,
    value: String,
    progress: Float,
) {
    val token = GymTheme.token
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
    ) {
        GMetricValueRow(label = label, value = value)
        GProgressBar(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            indicatorColor = token.colors.primary,
            trackColor = token.colors.surfaceSubtle,
            height = 6.dp,
        )
    }
}

@Composable
private fun GSegmentalGrid(items: List<GSegmentalMetricUiModel>) {
    val token = GymTheme.token
    val rows = (items.size + 1) / 2
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.md),
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
            ) {
                val left = items.getOrNull(row * 2)
                val right = items.getOrNull(row * 2 + 1)

                if (left != null) {
                    GSegmentalCell(
                        label = left.label,
                        value = left.value,
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    Box(modifier = Modifier.weight(1f))
                }

                if (right != null) {
                    GSegmentalCell(
                        label = right.label,
                        value = right.value,
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun GSegmentalCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(token.radius.md))
            .background(token.colors.surfaceSubtle)
            .padding(token.spacing.md),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
    ) {
        GText(text = label, style = token.typography.labelMedium, color = token.colors.textSecondary)
        GText(text = value, style = token.typography.bodyMedium, color = token.colors.textPrimary)
        GProgressBar(
            progress = 1f,
            modifier = Modifier.fillMaxWidth(),
            indicatorColor = token.colors.primary.copy(alpha = 0.5f),
            trackColor = token.colors.surfaceOverlay,
            height = 4.dp,
        )
    }
}
