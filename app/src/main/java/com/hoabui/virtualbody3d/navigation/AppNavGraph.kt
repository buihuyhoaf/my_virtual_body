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
import androidx.navigation.toRoute
import com.hoabui.virtualbody3d.ui.body.screen.BodyDetailAnalystScreen
import com.hoabui.virtualbody3d.ui.body.screen.BodyRegionDetailScreen
import com.hoabui.virtualbody3d.ui.body.screen.HomeScreen
import com.hoabui.virtualbody3d.ui.exerciselibrary.ExerciseLibraryScreen
import com.hoabui.virtualbody3d.ui.workoutcalendar.WorkoutCalendarScreen
import com.hoabui.virtualbody3d.ui.login.LoginScreen
import com.hoabui.virtualbody3d.ui.mealcapture.MealCaptureScreen
import com.hoabui.virtualbody3d.ui.profile.ProfileScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<LoginRoute> {
            LoginScreen(
                onSignIn = { _, _ ->
                    navController.navigate(HomeRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onForgotPassword = { },
                onSignUp = {
                    navController.navigate(HomeRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onSignInWithGoogle = {
                    navController.navigate(HomeRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onSignInWithApple = {
                    navController.navigate(HomeRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                }
            )
        }
        composable<HomeRoute> {
            HomeScreen(
                onViewBodyDetailClick = {
                    navController.navigate(BodyDetailAnalystRoute)
                },
                onNavigateToWorkoutCalendarClick = {
                    navController.navigate(WorkoutCalendarRoute)
                },
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
                onNavigateToWorkoutCalendar = { navController.navigate(WorkoutCalendarRoute) },
            )
        }
        composable<WorkoutCalendarRoute> {
            WorkoutCalendarScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable<AddRoute> {
            AppDestinationPlaceholder(labelResId = AppDestination.Add.labelResId)
        }
        composable<MealCaptureRoute> {
            MealCaptureScreen()
        }
        composable<CenfitCoachRoute> {
            ExerciseLibraryScreen(
                onNavigateToWorkoutCalendar = { navController.navigate(WorkoutCalendarRoute) },
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
