package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hoabui.virtualbody3d.ui.body.screen.BodyAnalysisRoute
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel
import com.hoabui.virtualbody3d.ui.calendar.screen.CalendarScreen
import com.hoabui.virtualbody3d.ui.login.LoginScreen
import com.hoabui.virtualbody3d.ui.onboarding.OnboardingScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sharedViewModel: BodyViewModel,
    modifier: Modifier = Modifier,
    startDestination: String,
    onOnboardingCompleted: () -> Unit
) {
    val screenState by sharedViewModel.screenState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = AppDestination.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    onOnboardingCompleted()
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(AppDestination.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = AppDestination.Login.route) {
            LoginScreen(
                onSignIn = { _, _ ->
                    navController.navigate(AppDestination.startDestination.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                    }
                },
                onForgotPassword = { /* TODO: handle forgot password navigation */ },
                onSignUp = {
                    navController.navigate(AppDestination.startDestination.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                    }
                },
                onSignInWithGoogle = {
                    navController.navigate(AppDestination.startDestination.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                    }
                },
                onSignInWithApple = {
                    navController.navigate(AppDestination.startDestination.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = AppDestination.Home.route) {
            BodyAnalysisRoute(viewModel = sharedViewModel)
        }
        composable(route = AppDestination.Add.route) {
            AppDestinationPlaceholder(labelResId = AppDestination.Add.labelResId)
        }
        composable(route = AppDestination.Calendar.route) {
            CalendarScreen(
                months = screenState.calendarMonths,
                selectedDate = screenState.selectedDate,
                dailyItemsByDate = screenState.dailyItemsByDate,
                onDateSelected = sharedViewModel::onDateSelected,
                onLoadMoreMonths = sharedViewModel::loadMoreCalendarMonths
            )
        }
    }
}

@Composable
private fun AppDestinationPlaceholder(labelResId: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(labelResId))
    }
}
