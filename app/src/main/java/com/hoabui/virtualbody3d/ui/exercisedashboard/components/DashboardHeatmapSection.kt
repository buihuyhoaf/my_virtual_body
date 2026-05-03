package com.hoabui.virtualbody3d.ui.exercisedashboard.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.progress.GCircularProgress
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.molecule.HeatmapWeekStripMolecule
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.ExerciseLibraryWeekStripUiState
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun DashboardHeatmapSection(
    heatmapState: ExerciseLibraryWeekStripUiState,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val dash = token.dashboardExercise
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = token.spacing.md, vertical = dash.heatmapSectionTopSpacing),
    ) {
        when (heatmapState) {
            ExerciseLibraryWeekStripUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    GCircularProgress()
                }
            }

            is ExerciseLibraryWeekStripUiState.Loaded ->
                HeatmapWeekStripMolecule(days = heatmapState.days)

            is ExerciseLibraryWeekStripUiState.Error ->
                GText(
                    text = heatmapState.message.ifBlank {
                        stringResource(R.string.exercise_library_weekly_heatmap_error)
                    },
                    style = token.typography.labelSmall,
                    color = token.colors.error,
                )
        }
    }
}
