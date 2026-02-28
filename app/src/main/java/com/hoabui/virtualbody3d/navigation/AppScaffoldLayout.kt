package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel

@Composable
fun AppScaffoldLayout(
    navController: NavHostController,
    sharedViewModel: BodyViewModel,
    startDestination: String,
    onOnboardingCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBars = currentRoute in AppDestination.bottomBarDestinations.map { it.route }

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
            navController = navController,
            sharedViewModel = sharedViewModel,
            modifier = Modifier.padding(innerPadding),
            startDestination = startDestination,
            onOnboardingCompleted = onOnboardingCompleted
        )
    }
}
