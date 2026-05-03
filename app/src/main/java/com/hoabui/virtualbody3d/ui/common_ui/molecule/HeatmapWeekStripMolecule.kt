package com.hoabui.virtualbody3d.ui.common_ui.molecule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hoabui.virtualbody3d.ui.common_ui.atom.HeatmapNode
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.WeekStripDayUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun HeatmapWeekStripMolecule(
    days: List<WeekStripDayUiModel>,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val dash = token.dashboardExercise
    val bodyToken = token.bodyAnalysis
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(bodyToken.exerciseLibraryHeatmapDayItemSpacing),
        verticalAlignment = Alignment.Bottom,
    ) {
        days.forEach { cell ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).wrapContentHeight(),
            ) {
                val alpha = when (cell.densityLevel) {
                    0 -> dash.heatmapPrimaryLevel0Alpha
                    1 -> dash.heatmapPrimaryLevel1Alpha
                    2 -> dash.heatmapPrimaryLevel2Alpha
                    else -> dash.heatmapPrimaryLevel3Alpha
                }
                HeatmapNode(
                    primaryColor = token.colors.primary,
                    backgroundAlpha = alpha,
                )
                Spacer(modifier = Modifier.height(token.spacing.xxs))
                GText(
                    text = cell.dayAbbrev.replaceFirstChar { it.titlecaseChar() },
                    style = token.typography.labelSmall,
                    color = token.colors.textSecondary,
                )
            }
        }
    }
}
