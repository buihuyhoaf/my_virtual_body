package com.hoabui.virtualbody3d.ui.exercisedashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.navigation.ExerciseLibraryRoute
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.DashboardCategoryUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import kotlinx.collections.immutable.ImmutableList

@Composable
fun DashboardCategoryGrid(
    tiles: ImmutableList<DashboardCategoryUiModel>,
    onNavigateToExerciseLibrary: (ExerciseLibraryRoute) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dash = GymTheme.token.dashboardExercise
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dash.categoryCardVerticalSpacing),
        userScrollEnabled = true,
    ) {
        items(tiles, key = { it.id }) { tile ->
            val route = navigationRouteFor(tile)
            val title = stringResource(tile.titleRes)
            DashboardCategoryCard(
                title = title,
                imageRes = tile.imageRes,
                onClick = { onNavigateToExerciseLibrary(route) },
            )
        }
    }
}

private fun navigationRouteFor(tile: DashboardCategoryUiModel): ExerciseLibraryRoute =
    tile.initialBodyRegionNames?.takeIf { it.isNotEmpty() }?.let { regions ->
        ExerciseLibraryRoute(initialBodyRegions = regions)
    } ?: ExerciseLibraryRoute(initialExerciseCategory = checkNotNull(tile.initialExerciseCategoryName))
