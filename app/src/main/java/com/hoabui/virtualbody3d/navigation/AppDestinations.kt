package com.hoabui.virtualbody3d.navigation

import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.body.state.BodyRegion

sealed class AppDestination(
    val route: Any,
    val labelResId: Int,
    val iconResId: Int? = null
) {
    data object Onboarding : AppDestination(OnboardingRoute, R.string.app_name)
    data object Login : AppDestination(LoginRoute, R.string.login_sign_in)
    data object InitialSetup : AppDestination(InitialSetupRoute, R.string.initial_setup_title)
    data object CreateBaseline : AppDestination(CreateBaselineRoute, R.string.create_baseline_title, R.drawable.camera)
    data object Home : AppDestination(HomeRoute, R.string.tab_home, R.drawable.home)
    data object Add : AppDestination(AddRoute, R.string.tab_add)
    data object MealCapture : AppDestination(MealCaptureRoute, R.string.tab_meal_capture, R.drawable.camera)
    data object Messages : AppDestination(MessagesRoute, R.string.tab_messages, R.drawable.envelope)
    data class MessageDetail(val messageId: String) : AppDestination(MessageDetailRoute(messageId), R.string.tab_messages, R.drawable.envelope)
    data object CenfitCoach : AppDestination(CenfitCoachRoute, R.string.tab_cenfit_coach, R.drawable.dumbbell_fitness)
    data object Profile : AppDestination(ProfileRoute, R.string.tab_profile, R.drawable.user)
    data object BodyScanResult : AppDestination(BodyScanResultRoute, R.string.body_scan_result_title)
    data object BodyDetailAnalyst : AppDestination(BodyDetailAnalystRoute, R.string.body_detail_analyst_title)
    data object ExerciseLibrary : AppDestination(ExerciseLibraryRoute, R.string.exercise_library_title, R.drawable.dumbbell_fitness)
    data class BodyRegionDetail(private val bodyRegion: BodyRegion) : AppDestination(
        BodyRegionDetailRoute(bodyRegion.name),
        R.string.body_scan_result_title
    )
    data class ExerciseDetail(val exerciseId: String) : AppDestination(
        ExerciseDetailRoute(exerciseId),
        R.string.exercise_library_title
    )
    data class AddWorkout(val exerciseId: String) : AppDestination(
        AddWorkoutRoute(exerciseId),
        R.string.add_workout_title,
        R.drawable.dumbbell_fitness
    )

    companion object {
        val startDestination: AppDestination = Home
        val bottomBarDestinations: List<AppDestination> = listOf(
            Home,
            CenfitCoach,
            MealCapture,
            Messages,
            Profile
        )
    }
}
