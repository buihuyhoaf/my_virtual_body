package com.hoabui.virtualbody3d.ui.body.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.ui.body.components.HomeContent
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel
import com.hoabui.virtualbody3d.ui.body.viewmodel.ExercisesViewModel
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.mealcapture.MealsViewModel

@Composable
fun HomeScreen(
    onViewBodyDetailClick: () -> Unit = {},
    onNavigateToWorkoutCalendarClick: () -> Unit = {},
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
                onNavigateToWorkoutCalendarClick = onNavigateToWorkoutCalendarClick,
            )
        }
    )
}
