package com.hoabui.virtualbody3d.navigation

import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.body.state.BodyRegion

object Routes {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val INITIAL_SETUP = "initial_setup"
    const val CREATE_BASELINE = "create_baseline"
    const val HOME = "home"
    const val ADD = "add"
    const val MEAL_CAPTURE = "meal_capture"
    const val MESSAGES = "messages"
    const val MESSAGE_DETAIL = "message_detail"
    const val CENFIT_COACH = "cenfit_coach"
    const val PROFILE = "profile"
    const val BODY_SCAN_RESULT = "body_scan_result"
    const val BODY_DETAIL_ANALYST = "body_detail_analyst"
    const val BODY_REGION_DETAIL = "body_region_detail"
    const val EXERCISE_LIBRARY = "exercise_library"
    const val ADD_WORKOUT = "add_workout"
}

sealed class AppDestination(
    val route: String,
    val labelResId: Int,
    val iconResId: Int? = null
) {
    data object Onboarding : AppDestination(
        route = Routes.ONBOARDING,
        labelResId = R.string.app_name,
    )

    data object Login : AppDestination(
        route = Routes.LOGIN,
        labelResId = R.string.login_sign_in,
    )

    data object InitialSetup : AppDestination(
        route = Routes.INITIAL_SETUP,
        labelResId = R.string.initial_setup_title,
    )

    data object CreateBaseline : AppDestination(
        route = Routes.CREATE_BASELINE,
        labelResId = R.string.create_baseline_title,
        iconResId = R.drawable.camera
    )

    data object Home : AppDestination(
        route = Routes.HOME,
        labelResId = R.string.tab_home,
        iconResId = R.drawable.home
    )

    data object Add : AppDestination(
        route = Routes.ADD,
        labelResId = R.string.tab_add,
    )

    data object MealCapture : AppDestination(
        route = Routes.MEAL_CAPTURE,
        labelResId = R.string.tab_meal_capture,
        iconResId = R.drawable.camera
    )

    data object Messages : AppDestination(
        route = Routes.MESSAGES,
        labelResId = R.string.tab_messages,
        iconResId = R.drawable.envelope
    )

    data class MessageDetail(val messageId: String) : AppDestination(
        route = "${Routes.MESSAGE_DETAIL}/$messageId",
        labelResId = R.string.tab_messages,
        iconResId = R.drawable.envelope
    )

    data object CenfitCoach : AppDestination(
        route = Routes.CENFIT_COACH,
        labelResId = R.string.tab_cenfit_coach,
        iconResId = R.drawable.dumbbell_fitness
    )

    data object Profile : AppDestination(
        route = Routes.PROFILE,
        labelResId = R.string.tab_profile,
        iconResId = R.drawable.user
    )

    data object BodyScanResult : AppDestination(
        route = Routes.BODY_SCAN_RESULT,
        labelResId = R.string.body_scan_result_title,
    )

    data object BodyDetailAnalyst : AppDestination(
        route = Routes.BODY_DETAIL_ANALYST,
        labelResId = R.string.body_detail_analyst_title,
    )

    data object ExerciseLibrary : AppDestination(
        route = Routes.EXERCISE_LIBRARY,
        labelResId = R.string.exercise_library_title,
        iconResId = R.drawable.dumbbell_fitness
    )

    data class BodyRegionDetail(private val bodyRegion: BodyRegion) : AppDestination(
        route = "${Routes.BODY_REGION_DETAIL}/${bodyRegion.name}",
        labelResId = R.string.body_scan_result_title,
    )

    data class AddWorkout(val exerciseId: String) : AppDestination(
        route = "${Routes.ADD_WORKOUT}/$exerciseId",
        labelResId = R.string.add_workout_title,
        iconResId = R.drawable.dumbbell_fitness
    ) {
        companion object {
            const val EXERCISE_ID_ARG = "exerciseId"
        }
    }

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

