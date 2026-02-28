package com.hoabui.virtualbody3d.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.hoabui.virtualbody3d.R

object Routes {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val HOME = "home"
    const val ADD = "add"
    const val CALENDAR = "calendar"
}

sealed class AppDestination(
    val route: String,
    val labelResId: Int,
    val icon: ImageVector
) {
    data object Onboarding : AppDestination(
        route = Routes.ONBOARDING,
        labelResId = R.string.app_name,
        icon = Icons.Default.Info
    )

    data object Login : AppDestination(
        route = Routes.LOGIN,
        labelResId = R.string.login_sign_in,
        icon = Icons.Default.Person
    )

    data object Home : AppDestination(
        route = Routes.HOME,
        labelResId = R.string.tab_home,
        icon = Icons.Default.Home
    )

    data object Add : AppDestination(
        route = Routes.ADD,
        labelResId = R.string.tab_add,
        icon = Icons.Default.AddCircle
    )

    data object Calendar : AppDestination(
        route = Routes.CALENDAR,
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

