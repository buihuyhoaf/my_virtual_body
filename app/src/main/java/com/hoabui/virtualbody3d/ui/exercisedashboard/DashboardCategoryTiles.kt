package com.hoabui.virtualbody3d.ui.exercisedashboard

import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.DashboardCategoryUiModel
import kotlinx.collections.immutable.persistentListOf

internal object DashboardCategoryTiles {
    val categories = persistentListOf(
        DashboardCategoryUiModel(
            id = "cat_strength",
            titleRes = R.string.exercise_category_strength,
            imageRes = R.drawable.bench_press,
            initialExerciseCategoryName = ExerciseCategory.Strength.name,
            initialBodyRegionNames = null,
        ),
        DashboardCategoryUiModel(
            id = "cat_cardio",
            titleRes = R.string.exercise_category_cardio,
            imageRes = R.drawable.bells,
            initialExerciseCategoryName = ExerciseCategory.Cardio.name,
            initialBodyRegionNames = null,
        ),
        DashboardCategoryUiModel(
            id = "cat_mobility",
            titleRes = R.string.exercise_category_mobility,
            imageRes = R.drawable.aperture,
            initialExerciseCategoryName = ExerciseCategory.Mobility.name,
            initialBodyRegionNames = null,
        ),
        DashboardCategoryUiModel(
            id = "cat_stretching",
            titleRes = R.string.exercise_category_stretching,
            imageRes = R.drawable.ruler_vertical,
            initialExerciseCategoryName = ExerciseCategory.Stretching.name,
            initialBodyRegionNames = null,
        ),
        DashboardCategoryUiModel(
            id = "region_upper",
            titleRes = R.string.exercise_dashboard_region_upper,
            imageRes = R.drawable.chest_normal,
            initialExerciseCategoryName = null,
            initialBodyRegionNames = multiRegionUpper(),
        ),
        DashboardCategoryUiModel(
            id = "region_lower",
            titleRes = R.string.exercise_dashboard_region_lower,
            imageRes = R.drawable.lat_pulldown,
            initialExerciseCategoryName = null,
            initialBodyRegionNames = listOf(BodyRegion.Legs.name),
        ),
        DashboardCategoryUiModel(
            id = "region_core",
            titleRes = R.string.exercise_dashboard_region_core,
            imageRes = R.drawable.belly_rectus_abdominis,
            initialExerciseCategoryName = null,
            initialBodyRegionNames = listOf(BodyRegion.Core.name, BodyRegion.Belly.name),
        ),
    )

    private fun multiRegionUpper(): List<String> =
        listOf(BodyRegion.Chest, BodyRegion.Back, BodyRegion.Shoulders, BodyRegion.Arms).map { it.name }
}
