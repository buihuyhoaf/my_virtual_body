package com.hoabui.virtualbody3d

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hoabui.virtualbody3d.core.utils.Constants
import com.hoabui.virtualbody3d.navigation.AppDestination
import com.hoabui.virtualbody3d.navigation.AppNavigationRoot
import com.hoabui.virtualbody3d.ui.splash.SplashScreen
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTheme {
                var showSplash by remember { mutableStateOf(true) }
                var startDestination by remember { mutableStateOf(AppDestination.startDestination.route) }

                LaunchedEffect(Unit) {
                    delay(SPLASH_DURATION_MS)
                    showSplash = false
                    startDestination =
                        if (sharedPreferences.getBoolean(
                                Constants.KEY_ONBOARDING_COMPLETED,
                                false
                            )
                        ) {
                            AppDestination.Login.route
                        } else {
                            AppDestination.Onboarding.route
                        }
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

    companion object {
        private const val SPLASH_DURATION_MS = 2_000L
    }
}