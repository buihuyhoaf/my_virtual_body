package com.hoabui.virtualbody3d.ui.body.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import com.hoabui.virtualbody3d.R

enum class BodyTab(
    val labelResId: Int,
    val icon: ImageVector
) {
    Body(labelResId = R.string.tab_body, icon = Icons.Default.Person),
    Nutrition(labelResId = R.string.tab_nutrition, icon = Icons.Default.Restaurant),
    Workout(labelResId = R.string.tab_workout, icon = Icons.Default.FitnessCenter),
    Progress(labelResId = R.string.tab_progress, icon = Icons.AutoMirrored.Outlined.TrendingUp)
}
