package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hoabui.virtualbody3d.ui.body.screen.BodyAnalysisScreen
import com.hoabui.virtualbody3d.ui.body.screen.BodyRegionDetailScreen
import com.hoabui.virtualbody3d.ui.calendar.screen.CalendarScreen
import com.hoabui.virtualbody3d.ui.cenfitcoach.CenfitCoachScreen
import com.hoabui.virtualbody3d.ui.createbaseline.CreateBaselineScreen
import com.hoabui.virtualbody3d.ui.messages.MessageDetailScreen
import com.hoabui.virtualbody3d.ui.messages.MessagesScreen
import com.hoabui.virtualbody3d.ui.initialsetup.InitialSetupScreen
import com.hoabui.virtualbody3d.ui.login.LoginScreen
import com.hoabui.virtualbody3d.ui.mealcapture.MealCaptureScreen
import com.hoabui.virtualbody3d.ui.onboarding.OnboardingScreen
import com.hoabui.virtualbody3d.ui.scanresult.BodyScanResultScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
    onOnboardingCompleted: () -> Unit
) {
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
                onNavigateToScanResult = {
                    navController.navigate(AppDestination.BodyScanResult.route)
                }
            )
        }
        composable(route = AppDestination.Home.route) {
            BodyAnalysisScreen(
                onRegionClick = { region ->
                    navController.navigate(AppDestination.BodyRegionDetail(region).route)
                }
            )
        }
        composable(
            route = "${Routes.BODY_REGION_DETAIL}/{region}",
            arguments = listOf(navArgument("region") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val regionName = backStackEntry.arguments?.getString("region") ?: return@composable
            BodyRegionDetailScreen(
                regionName = regionName,
                onBack = { navController.popBackStack() }
            )
        }
        composable(route = AppDestination.Add.route) {
            AppDestinationPlaceholder(labelResId = AppDestination.Add.labelResId)
        }
        composable(route = AppDestination.MealCapture.route) {
            MealCaptureScreen()
        }
        composable(route = AppDestination.Messages.route) {
            MessagesScreen(
                onMessageClick = { message ->
                    navController.navigate(AppDestination.MessageDetail(message.id).route)
                }
            )
        }
        composable(
            route = "${Routes.MESSAGE_DETAIL}/{messageId}",
            arguments = listOf(navArgument("messageId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val messageId = backStackEntry.arguments?.getString("messageId") ?: return@composable
            MessageDetailScreen(
                messageId = messageId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(route = AppDestination.CenfitCoach.route) {
            CenfitCoachScreen()
        }
        composable(route = AppDestination.Calendar.route) {
            CalendarScreen()
        }
        composable(route = AppDestination.BodyScanResult.route) {
            BodyScanResultScreen(
                onBeginClick = {
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.BodyScanResult.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    if (!navController.popBackStack()) {
                        navController.navigate(AppDestination.Home.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
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
