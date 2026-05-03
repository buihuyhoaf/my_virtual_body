package com.hoabui.virtualbody3d.ui.exercisedashboard.state

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class DashboardCategoryUiModel(
    val id: String,
    @StringRes val titleRes: Int,
    @DrawableRes val imageRes: Int,
    val initialExerciseCategoryName: String?,
    val initialBodyRegionNames: List<String>?,
)
