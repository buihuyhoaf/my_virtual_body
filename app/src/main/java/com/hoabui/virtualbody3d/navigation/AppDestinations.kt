package com.hoabui.virtualbody3d.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.graphics.vector.ImageVector
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
}

sealed class AppDestination(
    val route: String,
    val labelResId: Int,
    val icon: ImageVector
) {
    data object Onboarding : AppDestination(
        route = Routes.ONBOARDING,
        labelResId = R.string.app_name,
        icon = Icons.Default.Info
    )

    data object Login : AppDestination(
        route = Routes.LOGIN,
        labelResId = R.string.login_sign_in,
        icon = Icons.Default.Person
    )

    data object InitialSetup : AppDestination(
        route = Routes.INITIAL_SETUP,
        labelResId = R.string.initial_setup_title,
        icon = Icons.Default.Info
    )

    data object CreateBaseline : AppDestination(
        route = Routes.CREATE_BASELINE,
        labelResId = R.string.create_baseline_title,
        icon = Icons.Default.CameraAlt
    )

    data object Home : AppDestination(
        route = Routes.HOME,
        labelResId = R.string.tab_home,
        icon = Icons.Default.Home
    )

    data object Add : AppDestination(
        route = Routes.ADD,
        labelResId = R.string.tab_add,
        icon = Icons.Default.AddCircle
    )

    data object MealCapture : AppDestination(
        route = Routes.MEAL_CAPTURE,
        labelResId = R.string.tab_meal_capture,
        icon = Icons.Default.PhotoCamera
    )

    data object Messages : AppDestination(
        route = Routes.MESSAGES,
        labelResId = R.string.tab_messages,
        icon = Icons.Default.Message
    )

    data class MessageDetail(val messageId: String) : AppDestination(
        route = "${Routes.MESSAGE_DETAIL}/$messageId",
        labelResId = R.string.tab_messages,
        icon = Icons.Default.Message
    )

    data object CenfitCoach : AppDestination(
        route = Routes.CENFIT_COACH,
        labelResId = R.string.tab_cenfit_coach,
        icon = Icons.Default.FitnessCenter
    )

    data object Profile : AppDestination(
        route = Routes.PROFILE,
        labelResId = R.string.tab_profile,
        icon = Icons.Default.Person
    )

    data object BodyScanResult : AppDestination(
        route = Routes.BODY_SCAN_RESULT,
        labelResId = R.string.body_scan_result_title,
        icon = Icons.Default.Person
    )

    data object BodyDetailAnalyst : AppDestination(
        route = Routes.BODY_DETAIL_ANALYST,
        labelResId = R.string.body_detail_analyst_title,
        icon = Icons.Default.Info
    )

    data class BodyRegionDetail(private val bodyRegion: BodyRegion) : AppDestination(
        route = "${Routes.BODY_REGION_DETAIL}/${bodyRegion.name}",
        labelResId = R.string.body_scan_result_title,
        icon = Icons.Default.Person
    )

    companion object {
        val startDestination: AppDestination = Home

        val         bottomBarDestinations: List<AppDestination> = listOf(
            Home,
            CenfitCoach,
            MealCapture,
            Messages,
            Profile
        )
    }
}

