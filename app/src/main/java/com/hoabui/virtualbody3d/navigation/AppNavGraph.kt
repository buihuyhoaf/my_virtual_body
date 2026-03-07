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
import com.hoabui.virtualbody3d.ui.camera.screens.createbaseline.CreateBaselineScreen
import com.hoabui.virtualbody3d.ui.camera.screens.mealcapture.MealCaptureScreen
import com.hoabui.virtualbody3d.ui.initialsetup.InitialSetupScreen
import com.hoabui.virtualbody3d.ui.login.LoginScreen
import com.hoabui.virtualbody3d.ui.onboarding.OnboardingScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    sharedViewModel: BodyViewModel,
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
                    navController.navigate(AppDestination.InitialSetup.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                    }
                },
                onForgotPassword = { /* TODO: handle forgot password navigation */ },
                onSignUp = {
                    navController.navigate(AppDestination.InitialSetup.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                    }
                },
                onSignInWithGoogle = {
                    navController.navigate(AppDestination.InitialSetup.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                    }
                },
                onSignInWithApple = {
                    navController.navigate(AppDestination.InitialSetup.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = AppDestination.InitialSetup.route) {
            InitialSetupScreen(
                onComplete = {
                    navController.navigate(AppDestination.CreateBaseline.route) {
                        popUpTo(AppDestination.InitialSetup.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = AppDestination.CreateBaseline.route) {
            CreateBaselineScreen(
                onComplete = {
                    navController.navigate(AppDestination.startDestination.route) {
                        popUpTo(AppDestination.CreateBaseline.route) { inclusive = true }
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
        composable(route = AppDestination.MealCapture.route) {
            MealCaptureScreen()
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
