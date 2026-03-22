package com.hoabui.virtualbody3d.ui.exerciselibrary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Difficulty
import com.hoabui.virtualbody3d.navigation.AppTopBarBack
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseDetailDialog
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseFilterChips
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseSearchBar
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseSection
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel.ExerciseLibraryViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun ExerciseLibraryScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onAddToWorkout: (exerciseId: String) -> Unit = {},
    viewModel: ExerciseLibraryViewModel = hiltViewModel()
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    UiStateContent(
        state = screenState,
        modifier = modifier,
        successContent = { mod, data ->
            val token = GymTheme.token
            Column(
                modifier = mod.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                AppTopBarBack(
                    onBack = onBack
                ) {
                    Text(
                        text = stringResource(R.string.exercise_library_title),
                        style = token.typography.titleLarge,
                        color = token.colors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                ExerciseLibraryScreenContent(
                    modifier = Modifier.weight(1f),
                    state = data,
                    onQueryChange = viewModel::updateSearchQuery,
                    onBodyRegionSelect = viewModel::selectBodyRegion,
                    onDifficultySelect = viewModel::selectDifficulty,
                    onExerciseClick = viewModel::selectExerciseForDetail
                )
                data.selectedExerciseForDetail?.let { exercise ->
                    ExerciseDetailDialog(
                        exercise = exercise,
                        onAddClick = {
                            viewModel.clearExerciseDetail()
                            onAddToWorkout(exercise.id)
                        },
                        onDismiss = viewModel::clearExerciseDetail
                    )
                }
            }
        }
    )

}

@Composable
fun ExerciseLibraryScreenContent(
    modifier: Modifier = Modifier,
    state: ExerciseLibraryUiState,
    onQueryChange: (String) -> Unit,
    onBodyRegionSelect: (BodyRegion?) -> Unit,
    onDifficultySelect: (Difficulty?) -> Unit,
    onExerciseClick: (String) -> Unit = {},
) {
    val token = GymTheme.token
    Column(modifier = modifier.fillMaxSize()) {
        ExerciseSearchBar(
            query = state.searchQuery,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(horizontal = token.spacing.md)
        )
        ExerciseFilterChips(
            modifier = Modifier.padding(horizontal = token.spacing.md, vertical = token.spacing.xs),
            selectedBodyRegion = state.selectedBodyRegion,
            selectedDifficulty = state.selectedDifficulty,
            onBodyRegionSelect = onBodyRegionSelect,
            onDifficultySelect = onDifficultySelect,
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = token.spacing.md,
                vertical = token.spacing.md
            ),
            verticalArrangement = Arrangement.spacedBy(token.spacing.lg)
        ) {
            items(
                items = state.sections,
                key = { it.bodyRegion.name }
            ) { section ->
                ExerciseSection(
                    section = section,
                    onExerciseClick = { uiModel -> onExerciseClick(uiModel.id) }
                )
            }
        }
    }
}
