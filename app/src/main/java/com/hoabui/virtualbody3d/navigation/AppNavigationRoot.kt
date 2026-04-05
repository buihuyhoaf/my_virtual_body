package com.hoabui.virtualbody3d.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigationRoot(
    startDestination: Any,
) {
    val navController = rememberNavController()
    AppScaffoldLayout(
        navController = navController,
        startDestination = startDestination,
    )
}
