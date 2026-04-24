package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState

private sealed class TopBarConfig {
    data object None : TopBarConfig()
    data object Home : TopBarConfig()
}

@Composable
fun AppScaffoldLayout(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBars = currentDestination != null &&
        !currentDestination.hasRoute(LoginRoute::class)

    val showBottomBar = showBars && (currentDestination.hasRoute(HomeRoute::class)
            || currentDestination.hasRoute(ExerciseLibraryGraphRoute::class)
            || currentDestination.hasRoute(ExerciseLibraryRoute::class)
            || currentDestination.hasRoute(SessionBookingEditorRoute::class)
            || currentDestination.hasRoute(MealCaptureRoute::class)
            || currentDestination.hasRoute(ProfileRoute::class)
            || currentDestination.hasRoute(BodyDetailAnalystRoute::class)
            || currentDestination.hasRoute(WorkoutCalendarRoute::class))

    val topBarConfig = when {
        !showBars -> TopBarConfig.None
        currentDestination.hasRoute(HomeRoute::class) -> TopBarConfig.Home
        currentDestination.hasRoute(ProfileRoute::class) -> TopBarConfig.Home
        currentDestination.hasRoute(ExerciseLibraryGraphRoute::class) -> TopBarConfig.Home
        currentDestination.hasRoute(MealCaptureRoute::class) -> TopBarConfig.Home
        currentDestination.hasRoute(AddRoute::class) -> TopBarConfig.Home
        else -> TopBarConfig.None
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            when (topBarConfig) {
                TopBarConfig.Home -> {
                    AppTopBar(onNotificationClick = {})
                }
                TopBarConfig.None -> Unit
            }
        },
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = startDestination,
        )
    }
}
