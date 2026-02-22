package com.hoabui.virtualbody3d.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel

@Composable
fun AppNavigationRoot() {
    val navController = rememberNavController()
    val sharedViewModel: BodyViewModel = hiltViewModel()
    AppScaffoldLayout(
        navController = navController,
        sharedViewModel = sharedViewModel
    )
}
