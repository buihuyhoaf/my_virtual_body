package com.hoabui.virtualbody3d.ui.body.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult
import com.hoabui.virtualbody3d.ui.body.components.DailyMealsAutoRow
import com.hoabui.virtualbody3d.ui.body.components.HeroSection
import com.hoabui.virtualbody3d.ui.body.components.ProgressTimelineRow
import com.hoabui.virtualbody3d.ui.body.components.UpcomingExercisesRow
import com.hoabui.virtualbody3d.ui.body.data.CalorieGoalUiModel
import com.hoabui.virtualbody3d.ui.body.data.ProgressSnapshotUiModel
import com.hoabui.virtualbody3d.ui.body.data.UpcomingWorkoutUiItem
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.body.state.toUiState
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel
import com.hoabui.virtualbody3d.ui.body.viewmodel.ExercisesViewModel
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.mealcapture.MealPageUiModel
import com.hoabui.virtualbody3d.ui.mealcapture.MealsViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun HomeScreen(
    onViewBodyDetailClick: () -> Unit = {},
    onNavigateToExerciseLibrary: () -> Unit = {},
    viewModel: BodyViewModel = hiltViewModel(),
    exercisesViewModel: ExercisesViewModel = hiltViewModel(),
    mealsViewModel: MealsViewModel = hiltViewModel(),
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val upcomingWorkouts by exercisesViewModel.upcomingWorkouts.collectAsStateWithLifecycle()
    val mealsForToday by mealsViewModel.mealsForToday.collectAsStateWithLifecycle()

    UiStateContent(
        state = screenState,
        modifier = Modifier.fillMaxSize(),
        successContent = { mod, data ->
            HomeContent(
                modifier = mod,
                scanResult = data.scanResult,
                nutritionToday = data.nutritionToday,
                upcomingWorkouts = upcomingWorkouts,
                mealsForToday = mealsForToday,
                progressSnapshots = data.progressSnapshots,
                selectedProgressIndex = data.selectedProgressIndex,
                onProgressTimelineIndexSelected = viewModel::onProgressTimelineIndexSelected,
                onViewBodyDetailClick = onViewBodyDetailClick,
                onNavigateToExerciseLibrary = onNavigateToExerciseLibrary,
            )
        }
    )
}

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

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState(),
                    enabled = !isModelInteracting
                )
                .padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            UpcomingExercisesRow(
                modifier = Modifier
                    .fillMaxWidth(),
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
