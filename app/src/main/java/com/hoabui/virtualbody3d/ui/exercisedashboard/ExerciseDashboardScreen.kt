package com.hoabui.virtualbody3d.ui.exercisedashboard

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.navigation.ExerciseLibraryRoute
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.exercisedashboard.components.DashboardAchievementRecap
import com.hoabui.virtualbody3d.ui.exercisedashboard.components.DashboardCategoryGrid
import com.hoabui.virtualbody3d.ui.exercisedashboard.components.DashboardCoachPanel
import com.hoabui.virtualbody3d.ui.exercisedashboard.components.DashboardHeatmapSection
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.DashboardAchievementUiModel
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.DashboardCoachUiModel
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.ExerciseDashboardUiState
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.ExerciseLibraryWeekStripUiState
import com.hoabui.virtualbody3d.ui.exercisedashboard.viewmodel.ExerciseDashboardViewModel
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.LocalDate

private const val TopSectionWeight = 0.25f
private const val MiddleSectionWeight = 0.60f
private const val BottomSectionWeight = 0.15f

@Composable
fun ExerciseDashboardScreen(
    onNavigateToExerciseLibrary: (ExerciseLibraryRoute) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExerciseDashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    UiStateContent(
        state = state,
        modifier = modifier,
        successContent = { outerMod, ui ->
            GScaffold(
                modifier = outerMod,
                contentWindowInsets = WindowInsets(0),
            ) {
                ExerciseDashboardLoadedContent(ui = ui, onNavigateToExerciseLibrary = onNavigateToExerciseLibrary)
            }
        },
    )
}

@Composable
private fun ExerciseDashboardLoadedContent(
    ui: ExerciseDashboardUiState,
    onNavigateToExerciseLibrary: (ExerciseLibraryRoute) -> Unit,
) {
    val token = GymTheme.token
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(TopSectionWeight)
                    .clip(RoundedCornerShape(token.radius.lg))
                    .background(token.colors.surfaceSubtle),
        ) {
            DashboardAchievementRecap(achievement = ui.achievement)
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(MiddleSectionWeight),
        ) {
            DashboardCoachPanel(
                coach = ui.coach,
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
            )
            DashboardCategoryGrid(
                tiles = ui.categories,
                onNavigateToExerciseLibrary = onNavigateToExerciseLibrary,
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
            )
        }
        DashboardHeatmapSection(
            heatmapState = ui.heatmap,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(BottomSectionWeight),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExerciseDashboardPreviewLight() {
    GymTheme(darkTheme = false) {
        PreviewDashboardBody()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ExerciseDashboardPreviewDark() {
    GymTheme(darkTheme = true) {
        PreviewDashboardBody()
    }
}

@Composable
private fun PreviewDashboardBody() {
    val today = LocalDate.now().toEpochDay()
    val ui =
        ExerciseDashboardUiState(
            achievement =
                DashboardAchievementUiModel(
                    anchorEpochDay = today,
                    exerciseTitlesLine = "Bench · Squat",
                    totalKcal = 820,
                    durationMinutes = 48,
                ),
            coach =
                DashboardCoachUiModel(
                    speechText = "Coach preview",
                    coachImageRes = R.drawable.whitecat,
                ),
            categories = DashboardCategoryTiles.categories,
            heatmap = ExerciseLibraryWeekStripUiState.Loading,
        )
    GScaffold(contentWindowInsets = WindowInsets(0)) {
        ExerciseDashboardLoadedContent(ui = ui, onNavigateToExerciseLibrary = {})
    }
}
