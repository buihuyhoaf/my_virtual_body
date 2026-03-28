package com.hoabui.virtualbody3d.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Light
import com.adamglin.phosphoricons.light.Barbell
import com.adamglin.phosphoricons.light.Camera
import com.adamglin.phosphoricons.light.ChatCircleText
import com.adamglin.phosphoricons.light.House
import com.adamglin.phosphoricons.light.User
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.body.state.BodyRegion

sealed class AppDestination(
    val route: Any,
    val labelResId: Int,
    val iconResId: Int? = null,
    /** Phosphor (or other) vector for the main bottom navigation; optional for non-tab routes. */
    val bottomBarIcon: ImageVector? = null,
) {
    data object Onboarding : AppDestination(OnboardingRoute, R.string.app_name)
    data object Login : AppDestination(LoginRoute, R.string.login_sign_in)
    data object InitialSetup : AppDestination(InitialSetupRoute, R.string.initial_setup_title)
    data object CreateBaseline : AppDestination(CreateBaselineRoute, R.string.create_baseline_title, R.drawable.camera)
    data object Home : AppDestination(
        HomeRoute,
        R.string.tab_home,
        bottomBarIcon = PhosphorIcons.Light.House,
    )
    data object Add : AppDestination(AddRoute, R.string.tab_add)
    data object MealCapture : AppDestination(
        MealCaptureRoute,
        R.string.tab_meal_capture,
        bottomBarIcon = PhosphorIcons.Light.Camera,
    )
    data object Messages : AppDestination(
        MessagesRoute,
        R.string.tab_messages,
        bottomBarIcon = PhosphorIcons.Light.ChatCircleText,
    )
    data class MessageDetail(val messageId: String) : AppDestination(MessageDetailRoute(messageId), R.string.tab_messages, R.drawable.envelope)
    data object CenfitCoach : AppDestination(
        CenfitCoachRoute,
        R.string.tab_cenfit_coach,
        bottomBarIcon = PhosphorIcons.Light.Barbell,
    )
    data object Profile : AppDestination(
        ProfileRoute,
        R.string.tab_profile,
        bottomBarIcon = PhosphorIcons.Light.User,
    )
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
