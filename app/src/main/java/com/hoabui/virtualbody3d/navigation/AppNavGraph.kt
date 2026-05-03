package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.hoabui.virtualbody3d.ui.body.screen.BodyDetailAnalystScreen
import com.hoabui.virtualbody3d.ui.body.screen.BodyRegionDetailScreen
import com.hoabui.virtualbody3d.ui.body.screen.HomeScreen
import com.hoabui.virtualbody3d.ui.exercisedashboard.ExerciseDashboardScreen
import com.hoabui.virtualbody3d.ui.exerciselibrary.ExerciseLibraryScreen
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingEditorScreen
import com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel.ExerciseLibraryViewModel
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
        navigation<ExerciseLibraryGraphRoute>(startDestination = ExerciseDashboardRoute) {
            composable<ExerciseDashboardRoute> {
                ExerciseDashboardScreen(
                    onNavigateToExerciseLibrary = { route ->
                        navController.navigate(route)
                    },
                )
            }
            composable<ExerciseLibraryRoute> { backStackEntry ->
                // Use the route *instance* (not KClass) — getBackStackEntry(KClass) triggers ClassReference serializer failure.
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(ExerciseLibraryGraphRoute)
                }
                val viewModel: ExerciseLibraryViewModel = hiltViewModel(parentEntry)
                val route = backStackEntry.toRoute<ExerciseLibraryRoute>()
                ExerciseLibraryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToWorkoutCalendar = {
                        navController.navigate(WorkoutCalendarRoute)
                    },
                    scheduleRowIdToEdit = route.scheduleRowIdToEdit,
                    initialExerciseCategory = route.initialExerciseCategory,
                    initialBodyRegions = route.initialBodyRegions,
                )
            }
            composable<SessionBookingEditorRoute> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(ExerciseLibraryGraphRoute)
                }
                val viewModel: ExerciseLibraryViewModel = hiltViewModel(parentEntry)
                SessionBookingEditorScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToWorkoutCalendar = {
                        val poppedToLibrary =
                            navController.popBackStack<ExerciseLibraryRoute>(inclusive = false)
                        if (!poppedToLibrary) {
                            navController.popBackStack<ExerciseLibraryGraphRoute>(inclusive = false)
                        }
                        navController.navigate(WorkoutCalendarRoute)
                    },
                )
            }
        }
        composable<WorkoutCalendarRoute> { backStackEntry ->
            val exerciseGraphEntry =
                remember(backStackEntry) {
                    runCatching { navController.getBackStackEntry(ExerciseLibraryGraphRoute) }
                        .getOrNull()
                }
            val exerciseLibraryViewModel: ExerciseLibraryViewModel =
                if (exerciseGraphEntry != null) {
                    hiltViewModel(exerciseGraphEntry)
                } else {
                    hiltViewModel(backStackEntry)
                }
            val onWorkoutCalendarBack = {
                navController.popWorkoutCalendarOrFallback()
            }
            WorkoutCalendarScreen(
                onBack = onWorkoutCalendarBack,
                onNavigateToSessionBookingEditor = {
                    navigateToSessionBookingFromWorkoutCalendar(navController)
                },
                exerciseLibraryViewModel = exerciseLibraryViewModel,
            )
        }
        composable<AddRoute> {
            AppDestinationPlaceholder(labelResId = AppDestination.Add.labelResId)
        }
        composable<MealCaptureRoute> {
            MealCaptureScreen()
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

fun NavHostController.popWorkoutCalendarOrFallback() {
    if (!popBackStack()) {
        navigate(ExerciseLibraryGraphRoute) {
            popUpTo<WorkoutCalendarRoute> { inclusive = true }
            launchSingleTop = true
        }
    }
}

private fun navigateToSessionBookingFromWorkoutCalendar(navController: NavHostController) {
    try {
        navController.getBackStackEntry(ExerciseLibraryGraphRoute)
    } catch (_: IllegalArgumentException) {
        navController.navigate(ExerciseLibraryGraphRoute) {
            launchSingleTop = true
        }
    }
    navController.navigate(SessionBookingEditorRoute)
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
