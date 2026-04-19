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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.Barbell
import com.adamglin.phosphoricons.fill.Drop
import com.adamglin.phosphoricons.fill.Scales
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult
import com.hoabui.virtualbody3d.core.extensions.formatMeasurement
import com.hoabui.virtualbody3d.core.utils.Constants
import com.hoabui.virtualbody3d.ui.body.data.CalorieGoalUiModel
import com.hoabui.virtualbody3d.ui.body.data.ProgressSnapshotUiModel
import com.hoabui.virtualbody3d.ui.body.data.UpcomingWorkoutUiItem
import com.hoabui.virtualbody3d.ui.body.screen.BodyModelPreview
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.body.state.heroBmiIndicatorColor
import com.hoabui.virtualbody3d.ui.body.state.heroBmiStatusLabel
import com.hoabui.virtualbody3d.ui.body.state.toUiState
import com.hoabui.virtualbody3d.ui.common_ui.organism.body.GBodyHeroPanel
import com.hoabui.virtualbody3d.ui.common_ui.organism.body.GBodyHeroPanelUiModel
import com.hoabui.virtualbody3d.ui.common_ui.organism.body.GHeroMetricChipUiModel
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
    onNavigateToWorkoutCalendarClick: () -> Unit = {},
) {
    val token = GymTheme.token
    val contentHeight = LocalConfiguration.current.screenHeightDp.dp
    val uiState = scanResult?.toUiState() ?: BodyUiState()
    var isModelInteracting by remember { mutableStateOf(false) }

    val bmiStatusLabel = uiState.heroBmiStatusLabel()
    val bmiIndicatorColor = uiState.heroBmiIndicatorColor()

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
                    .padding(horizontal = token.spacing.md),
                verticalArrangement = Arrangement.spacedBy(token.spacing.lg),
            ) {
                GBodyHeroPanel(
                    uiModel = GBodyHeroPanelUiModel(
                        title = stringResource(R.string.home_section_body),
                        actionText = stringResource(R.string.home_section_see_more),
                        bmiStatus = bmiStatusLabel,
                        bmiIndicatorColor = bmiIndicatorColor,
                        metrics = listOf(
                            GHeroMetricChipUiModel(
                                id = "weight",
                                icon = PhosphorIcons.Fill.Scales,
                                value = uiState.weight.formatMeasurement(Constants.KILOGRAM),
                            ),
                            GHeroMetricChipUiModel(
                                id = "bodyFat",
                                icon = PhosphorIcons.Fill.Drop,
                                value = uiState.bodyFat.formatMeasurement(Constants.PERCENT),
                            ),
                            GHeroMetricChipUiModel(
                                id = "muscleMass",
                                icon = PhosphorIcons.Fill.Barbell,
                                value = uiState.muscleMass.formatMeasurement(Constants.PERCENT),
                            ),
                        ),
                    ),
                    onActionClick = onViewBodyDetailClick,
                    modelContent = { mod ->
                        BodyModelPreview(
                            modifier = mod,
                            onInteractionChanged = { isModelInteracting = it },
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(contentHeight * 0.45f),
                )
                NutritionSummaryCard(
                    nutritionToday = nutritionToday,
                    mealsForToday = mealsForToday,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
                UpcomingExercisesRow(
                    modifier = Modifier.fillMaxWidth(),
                    exercises = upcomingWorkouts,
                    onSeeMoreClick = onNavigateToWorkoutCalendarClick,
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
