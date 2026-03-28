package com.hoabui.virtualbody3d.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigationRoot(
    startDestination: Any,
    onOnboardingCompleted: () -> Unit
) {
    val navController = rememberNavController()
    AppScaffoldLayout(
        navController = navController,
        startDestination = startDestination,
        onOnboardingCompleted = onOnboardingCompleted
    )
}
