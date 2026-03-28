package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hoabui.virtualbody3d.R

private sealed class TopBarConfig {
    data object None : TopBarConfig()
    data object Home : TopBarConfig()
}

@Composable
fun AppScaffoldLayout(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any,
    onOnboardingCompleted: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBars = currentDestination != null &&
        !currentDestination.hasRoute(OnboardingRoute::class) &&
        !currentDestination.hasRoute(LoginRoute::class) &&
        !currentDestination.hasRoute(InitialSetupRoute::class) &&
        !currentDestination.hasRoute(CreateBaselineRoute::class) &&
        !currentDestination.hasRoute(BodyScanResultRoute::class)

    val showBottomBar = showBars && (
        currentDestination?.hasRoute(HomeRoute::class) == true ||
            currentDestination?.hasRoute(MessagesRoute::class) == true ||
            currentDestination?.hasRoute(CenfitCoachRoute::class) == true ||
            currentDestination?.hasRoute(MealCaptureRoute::class) == true ||
            currentDestination?.hasRoute(ProfileRoute::class) == true ||
            currentDestination?.hasRoute(BodyDetailAnalystRoute::class) == true
        )

    val topBarConfig = when {
        !showBars -> TopBarConfig.None
        currentDestination?.hasRoute(HomeRoute::class) == true -> TopBarConfig.Home
        currentDestination?.hasRoute(MessagesRoute::class) == true -> TopBarConfig.Home
        currentDestination?.hasRoute(ProfileRoute::class) == true -> TopBarConfig.Home
        currentDestination?.hasRoute(CenfitCoachRoute::class) == true -> TopBarConfig.Home
        currentDestination?.hasRoute(MealCaptureRoute::class) == true -> TopBarConfig.Home
        currentDestination?.hasRoute(AddRoute::class) == true -> TopBarConfig.Home
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
            onOnboardingCompleted = onOnboardingCompleted
        )
    }
}

