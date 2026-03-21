package com.hoabui.virtualbody3d.ui.body.screen

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.domain.model.BodyScanResult
import com.hoabui.virtualbody3d.ui.body.components.CaloriePremiumCard
import com.hoabui.virtualbody3d.ui.body.components.HeroSection
import com.hoabui.virtualbody3d.ui.body.components.MetricType
import com.hoabui.virtualbody3d.ui.body.components.ProgressSnapshotUiModel
import com.hoabui.virtualbody3d.ui.body.components.ProgressTimelineRow
import com.hoabui.virtualbody3d.ui.body.data.CalorieGoalUiModel
import com.hoabui.virtualbody3d.ui.body.data.CalorieUiModel
import com.hoabui.virtualbody3d.ui.body.data.FavoriteExerciseUiItem
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.body.state.toUiState
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel
import com.hoabui.virtualbody3d.ui.body.viewmodel.FavoriteExercisesViewModel
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun HomeScreen(
    onViewBodyDetailClick: () -> Unit = {},
    onNavigateToExerciseLibrary: () -> Unit = {},
    viewModel: BodyViewModel = hiltViewModel(),
    favoriteExercisesViewModel: FavoriteExercisesViewModel = hiltViewModel(),
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val favoriteExercises by favoriteExercisesViewModel.exercises.collectAsStateWithLifecycle()

    UiStateContent(
        state = screenState,
        modifier = Modifier.fillMaxSize(),
        successContent = { mod, data ->
            HomeContent(
                modifier = mod,
                scanResult = data.scanResult,
                nutritionToday = data.nutritionToday,
                favoriteExercises = favoriteExercises,
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
    favoriteExercises: List<FavoriteExerciseUiItem>,
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

            CaloriePremiumCard(
                modifier = Modifier
                    .wrapContentHeight(),
                data = CalorieUiModel(
                    intake = nutritionToday.intake,
                    burned = nutritionToday.burned,
                    intakeGoal = nutritionToday.intakeGoal,
                    burnGoal = nutritionToday.burnGoal
                ),
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
                items = remember {
                    listOf(
                        ProgressSnapshotUiModel("Mar 1", null, 75.0f, 20.0f, 32.4f, null),
                        ProgressSnapshotUiModel("Mar 5", null, 74.2f, 19.5f, 32.7f, -0.8f),
                        ProgressSnapshotUiModel("Mar 10", null, 73.5f, 19.0f, 33.0f, -0.7f),
                        ProgressSnapshotUiModel("Mar 15", null, 72.8f, 18.6f, 33.2f, -0.7f),
                        ProgressSnapshotUiModel("Mar 20", null, 72.0f, 18.2f, 33.6f, -0.8f)
                    )
                },
                selectedIndex = 3,
                metricType = MetricType.WEIGHT
            )
        }
    }
}
