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
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.hoabui.virtualbody3d.ui.addworkout.AddWorkoutScreen
import com.hoabui.virtualbody3d.ui.body.screen.BodyDetailAnalystScreen
import com.hoabui.virtualbody3d.ui.body.screen.BodyRegionDetailScreen
import com.hoabui.virtualbody3d.ui.body.screen.HomeScreen
import com.hoabui.virtualbody3d.ui.createbaseline.CreateBaselineScreen
import com.hoabui.virtualbody3d.ui.exercisedetail.ExerciseDetailScreen
import com.hoabui.virtualbody3d.ui.exerciselibrary.ExerciseLibraryScreen
import com.hoabui.virtualbody3d.ui.initialsetup.InitialSetupScreen
import com.hoabui.virtualbody3d.ui.login.LoginScreen
import com.hoabui.virtualbody3d.ui.mealcapture.MealCaptureScreen
import com.hoabui.virtualbody3d.ui.messages.MessageDetailScreen
import com.hoabui.virtualbody3d.ui.messages.MessagesScreen
import com.hoabui.virtualbody3d.ui.onboarding.OnboardingScreen
import com.hoabui.virtualbody3d.ui.profile.ProfileScreen
import com.hoabui.virtualbody3d.ui.scanresult.BodyScanResultScreen
import com.hoabui.virtualbody3d.ui.workoutfeed.WorkoutFeedScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any,
    onOnboardingCompleted: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<OnboardingRoute> {
            OnboardingScreen(
                onComplete = {
                    onOnboardingCompleted()
                    navController.navigate(LoginRoute) {
                        popUpTo<OnboardingRoute> { inclusive = true }
                    }
                }
            )
        }
        composable<LoginRoute> {
            LoginScreen(
                onSignIn = { _, _ ->
                    navController.navigate(InitialSetupRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onForgotPassword = { },
                onSignUp = {
                    navController.navigate(InitialSetupRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onSignInWithGoogle = {
                    navController.navigate(InitialSetupRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onSignInWithApple = {
                    navController.navigate(InitialSetupRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                }
            )
        }
        composable<InitialSetupRoute> {
            InitialSetupScreen(
                onComplete = {
                    navController.navigate(CreateBaselineRoute) {
                        popUpTo<InitialSetupRoute> { inclusive = true }
                    }
                }
            )
        }
        composable<CreateBaselineRoute> {
            CreateBaselineScreen(
                onNavigateToScanResult = {
                    navController.navigate(BodyScanResultRoute)
                }
            )
        }
        composable<HomeRoute> {
            HomeScreen(
                onViewBodyDetailClick = {
                    navController.navigate(BodyDetailAnalystRoute)
                },
                onNavigateToExerciseLibrary = {
                    navController.navigate(ExerciseLibraryRoute)
                }
            )
        }
        composable<BodyRegionDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<BodyRegionDetailRoute>()
            BodyRegionDetailScreen(
                regionName = route.region,
                onBack = { navController.popBackStack() }
            )
        }
        composable<BodyDetailAnalystRoute> {
            BodyDetailAnalystScreen(onBack = { navController.popBackStack() })
        }
        composable<ExerciseLibraryRoute> {
            ExerciseLibraryScreen(
                onBack = { navController.popBackStack() },
                onAddToWorkout = { exerciseId ->
                    navController.navigate(AddWorkoutRoute(exerciseId))
                }
            )
        }
        composable<ExerciseDetailRoute>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "myvirtualbody://exercise/{exerciseId}"
                }
            )
        ) {
            ExerciseDetailScreen(
                onBack = { navController.popBackStack() },
                onAddToWorkout = { exerciseId ->
                    navController.navigate(AddWorkoutRoute(exerciseId))
                }
            )
        }
        composable<AddWorkoutRoute> {
            AddWorkoutScreen(
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable<AddRoute> {
            AppDestinationPlaceholder(labelResId = AppDestination.Add.labelResId)
        }
        composable<MealCaptureRoute> {
            MealCaptureScreen()
        }
        composable<MessagesRoute> {
            MessagesScreen(
                onMessageClick = { message ->
                    navController.navigate(MessageDetailRoute(message.id))
                }
            )
        }
        composable<MessageDetailRoute> {
            MessageDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<CenfitCoachRoute> {
            ExerciseLibraryScreen(
                onBack = { navController.popBackStack() },
                onAddToWorkout = { exerciseId ->
                    navController.navigate(AddWorkoutRoute(exerciseId))
                }
            )
        }
        composable<ProfileRoute> {
            ProfileScreen(
                onNavigateToBodyAnalysis = {
                    navController.navigate(HomeRoute) { launchSingleTop = true }
                },
                onLogout = {
                    navController.navigate(LoginRoute) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable<BodyScanResultRoute> {
            BodyScanResultScreen(
                onBeginClick = {
                    navController.navigate(HomeRoute) {
                        popUpTo<BodyScanResultRoute> { inclusive = true }
                    }
                },
                onBackClick = {
                    if (!navController.popBackStack()) {
                        navController.navigate(HomeRoute) {
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
