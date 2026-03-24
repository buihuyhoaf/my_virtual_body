package com.hoabui.virtualbody3d.ui.body.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult
import com.hoabui.virtualbody3d.ui.body.data.CalorieGoalUiModel
import com.hoabui.virtualbody3d.ui.body.data.ProgressSnapshotUiModel
import com.hoabui.virtualbody3d.ui.body.data.UpcomingWorkoutUiItem
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.body.state.toUiState
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.mealcapture.MealPageUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    scanResult: BodyScanResult?,
    nutritionToday: CalorieGoalUiModel,
    upcomingWorkouts: List<UpcomingWorkoutUiItem>,
    mealsForToday: List<MealPageUiModel>,
    progressSnapshots: List<ProgressSnapshotUiModel>,
    selectedProgressIndex: Int,
    onProgressTimelineIndexSelected: (Int) -> Unit = {},
    onViewBodyDetailClick: () -> Unit = {},
    onNavigateToExerciseLibrary: () -> Unit,
) {
    val token = GymTheme.token
    val contentHeight = LocalConfiguration.current.screenHeightDp.dp
    val uiState = scanResult?.toUiState() ?: BodyUiState()
    val bodyScore = ((uiState.bmiScalePosition ?: 0.76f) * 100f).toInt().coerceIn(0, 100)
    var isModelInteracting by remember { mutableStateOf(false) }

    GScaffold(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState(),
                        enabled = !isModelInteracting,
                    )
                    .padding(token.spacing.md),
                verticalArrangement = Arrangement.spacedBy(token.spacing.md),
            ) {
                UpcomingExercisesRow(
                    modifier = Modifier.fillMaxWidth(),
                    exercises = upcomingWorkouts,
                )
                HeroSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(contentHeight * 0.45f),
                    uiState = uiState,
                    bodyScore = bodyScore,
                    onViewBodyDetailClick = onViewBodyDetailClick,
                    onModelInteractionChanged = { isModelInteracting = it },
                )
                ProgressTimelineRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    items = progressSnapshots,
                    selectedIndex = selectedProgressIndex,
                    onItemClick = onProgressTimelineIndexSelected,
                )
            }
        }
    }
}
