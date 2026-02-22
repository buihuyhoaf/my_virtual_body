package com.hoabui.virtualbody3d.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.hoabui.virtualbody3d.R

sealed class AppDestination(
    val route: String,
    val labelResId: Int,
    val icon: ImageVector
) {
    data object Home : AppDestination(
        route = "home",
        labelResId = R.string.tab_home,
        icon = Icons.Default.Home
    )

    data object Add : AppDestination(
        route = "add",
        labelResId = R.string.tab_add,
        icon = Icons.Default.AddCircle
    )

    data object Calendar : AppDestination(
        route = "calendar",
        labelResId = R.string.tab_calendar,
        icon = Icons.Default.CalendarMonth
    )

    companion object {
        val startDestination: AppDestination = Home

        val bottomBarDestinations: List<AppDestination> = listOf(
            Home,
            Add,
            Calendar
        )
    }
}
