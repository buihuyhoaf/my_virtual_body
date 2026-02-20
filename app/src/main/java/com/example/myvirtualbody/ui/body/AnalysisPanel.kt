package com.example.myvirtualbody.ui.body

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myvirtualbody.R
import com.example.myvirtualbody.ui.theme.*

private val AnalysisPanelOuterShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
private val AnalysisPanelInnerShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
private val AnalysisSectionShape = RectangleShape
private val AnalysisPillShape = RectangleShape

@Composable
fun AnalysisPanel(
    uiState: BodyUiState,
    modifier: Modifier = Modifier,
    bodyScore: Int = ((uiState.bmiScalePosition ?: 0.76f) * 100f).toInt()
) {
    val bodyFatValue = formatPercentMeasurement(uiState.bodyFat)
    val muscleValue = formatMeasurement(uiState.muscleMass, uiState.muscleMassUnit)
    val bmiValue = uiState.bmi.ifBlank { stringResource(R.string.body_placeholder) }
    val bmiMarker = uiState.bmiScalePosition?.coerceIn(0f, 1f) ?: (bodyScore.coerceIn(0, 100) / 100f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        shape = AnalysisPanelOuterShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp, bottom = 8.dp),
            shape = AnalysisPanelInnerShape,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.22f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 48.dp, height = 5.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = AnalysisPillShape
                        )
                )
                BodyCompositionSection(
                    fatValue = bodyFatValue,
                    fatProgress = uiState.bodyFatProgress ?: percentToProgress(uiState.bodyFat),
                    muscleValue = muscleValue,
                    muscleProgress = uiState.muscleMassProgress ?: 0f
                )
                BmiAnalysisSection(
                    bmi = bmiValue,
                    bmiStatus = uiState.bmiStatus.orEmpty(),
                    markerPosition = bmiMarker
                )
            }
        }
    }
}

@Composable
private fun BodyCompositionSection(
    fatValue: String,
    fatProgress: Float,
    muscleValue: String,
    muscleProgress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AnalysisSectionShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.24f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                shape = AnalysisSectionShape
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.analysis_body_composition),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.analysis_info),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            CompositionBar(
                label = stringResource(R.string.analysis_fat_percentage),
                value = fatValue.ifBlank { stringResource(R.string.body_placeholder) },
                progress = fatProgress,
                colors = listOf(FatGradientStart, FatGradientEnd)
            )
            CompositionBar(
                label = stringResource(R.string.body_muscle_mass),
                value = muscleValue.ifBlank { stringResource(R.string.body_placeholder) },
                progress = muscleProgress,
                colors = listOf(BodyPrimary, MuscleGradientEnd)
            )
        }
    }
}

@Composable
private fun CompositionBar(
    label: String,
    value: String,
    progress: Float,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = AnalysisPillShape
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .background(
                        brush = Brush.horizontalGradient(colors),
                        shape = AnalysisPillShape
                    )
            )
        }
    }
}

@Composable
private fun BmiAnalysisSection(
    bmi: String,
    bmiStatus: String,
    markerPosition: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AnalysisSectionShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.24f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                shape = AnalysisSectionShape
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.analysis_bmi_analysis),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = AnalysisPillShape,
                    color = BmiNormalColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = bmiStatus.ifBlank { stringResource(R.string.analysis_normal_range) },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = BmiNormalColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val markerOffset = maxWidth * markerPosition.coerceIn(0f, 1f)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = markerOffset - 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = bmi,
                        style = MaterialTheme.typography.labelSmall,
                        color = BmiNormalColor,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(width = 4.dp, height = 14.dp)
                            .background(BmiNormalColor, AnalysisPillShape)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp)
                        .height(10.dp)
                        .clip(AnalysisPillShape)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            shape = AnalysisPillShape
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.25f)
                            .fillMaxHeight()
                            .background(BmiUnderColor)
                    )
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                            .background(BmiNormalColor)
                    )
                    Box(
                        modifier = Modifier
                            .weight(0.35f)
                            .fillMaxHeight()
                            .background(BmiOverColor)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 38.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.analysis_under),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.analysis_normal),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.analysis_over),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(top = 6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            )
        }
    }
}

data class DistributionMetric(
    val label: String,
    val progress: Float
)

private fun formatMeasurement(value: String, unit: String): String {
    val trimmedValue = value.trim()
    val trimmedUnit = unit.trim()
    return listOf(trimmedValue, trimmedUnit).filter { it.isNotEmpty() }.joinToString(" ")
}

private fun formatPercentMeasurement(value: String): String {
    val trimmed = value.trim()
    if (trimmed.isBlank()) return ""
    return if (trimmed.contains("%")) trimmed else "$trimmed%"
}

private fun percentToProgress(value: String): Float {
    return value
        .replace("%", "")
        .trim()
        .toFloatOrNull()
        ?.div(100f)
        ?.coerceIn(0f, 1f)
        ?: 0f
}
