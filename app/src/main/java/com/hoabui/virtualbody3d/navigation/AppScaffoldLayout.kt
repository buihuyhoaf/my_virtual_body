package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

private val fullScreenRoutes = setOf(
    Routes.ONBOARDING,
    Routes.INITIAL_SETUP,
    Routes.CREATE_BASELINE,
    Routes.BODY_SCAN_RESULT
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

    Scaffold(
        modifier = modifier,
        topBar = {
            if (showBars) {
                AppTopBar(onNotificationClick = {})
            }
        },
        bottomBar = {
            if (showBars) {
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
