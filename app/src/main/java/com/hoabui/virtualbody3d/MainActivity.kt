package com.hoabui.virtualbody3d

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hoabui.virtualbody3d.core.utils.Constants
import com.hoabui.virtualbody3d.domain.repository.ResourceProvider
import com.hoabui.virtualbody3d.navigation.AppDestination
import com.hoabui.virtualbody3d.navigation.AppNavigationRoot
import com.hoabui.virtualbody3d.ui.body.provider.BodyModelProvider
import com.hoabui.virtualbody3d.ui.body.provider.BodyModelPreload
import com.hoabui.virtualbody3d.ui.body.provider.LocalBodyModelProvider
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.splash.SplashScreen
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var bodyModelProvider: BodyModelProvider

    @Inject
    lateinit var resourceProvider: ResourceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTheme {
                CompositionLocalProvider(
                    LocalBodyModelProvider provides bodyModelProvider,
                    LocalResourceProvider provides resourceProvider
                ) {
                    BodyModelPreload()
                    var showSplash by remember { mutableStateOf(true) }
                    var startDestination by remember { mutableStateOf(AppDestination.startDestination.route) }

                    LaunchedEffect(Unit) {
                        delay(SPLASH_DURATION_MS)
                        showSplash = false
                        // Tạm thời luôn vào Home. Sau này bỏ comment và xóa dòng startDestination = Home.route
                        // để dùng lại logic: đã onboarding -> Login, chưa -> Onboarding.
                        startDestination = AppDestination.Home.route
//                        startDestination =
//                            if (sharedPreferences.getBoolean(
//                                    Constants.KEY_ONBOARDING_COMPLETED,
//                                    false
//                                )
//                            ) {
//                                AppDestination.Login.route
//                            } else {
//                                AppDestination.Onboarding.route
//                            }
                    }

                    when {
                        showSplash -> SplashScreen()
                        else -> AppNavigationRoot(
                            startDestination = startDestination,
                            onOnboardingCompleted = {
                                sharedPreferences.edit()
                                    .putBoolean(Constants.KEY_ONBOARDING_COMPLETED, true)
                                    .apply()
                            }
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val SPLASH_DURATION_MS = 3_500L
    }
}