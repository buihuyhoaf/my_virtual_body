package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hoabui.virtualbody3d.R

private val fullScreenRoutes = setOf(
    Routes.ONBOARDING,
    Routes.LOGIN,
    Routes.INITIAL_SETUP,
    Routes.CREATE_BASELINE,
    Routes.BODY_SCAN_RESULT
)

private sealed class TopBarConfig {
    data object None : TopBarConfig()
    data object Home : TopBarConfig()
}

private val topBarConfigs: Map<String, TopBarConfig> = mapOf(
    // Home-level destinations that use the global AppTopBar
    Routes.HOME to TopBarConfig.Home,
    Routes.MESSAGES to TopBarConfig.Home,
    Routes.PROFILE to TopBarConfig.Home,
    Routes.CENFIT_COACH to TopBarConfig.Home,
    Routes.MEAL_CAPTURE to TopBarConfig.Home,
    Routes.ADD to TopBarConfig.Home
)

/**
 * Exact routes where the bottom bar is visible (main tabs + depth-2 screens with no path args).
 */
private val bottomBarVisibleRoutes = setOf(
    Routes.HOME,
    Routes.MESSAGES,
    Routes.CENFIT_COACH,
    Routes.MEAL_CAPTURE,
    Routes.PROFILE,
    Routes.BODY_DETAIL_ANALYST
)

@Composable
fun AppScaffoldLayout(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
    onOnboardingCompleted: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBars = currentRoute != null && currentRoute !in fullScreenRoutes

    val showBottomBar = showBars && (currentRoute in bottomBarVisibleRoutes)

    val topBarConfig = when {
        !showBars -> TopBarConfig.None
        else -> topBarConfigs[currentRoute] ?: TopBarConfig.None
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

