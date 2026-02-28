package com.hoabui.virtualbody3d

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi

/**
 * Launcher activity that starts MainActivity using the official Splash Screen API.
 * On API 33+, uses ActivityOptions.setSplashScreenStyle(SPLASH_SCREEN_STYLE_SOLID_COLOR)
 * so the splash shown for MainActivity is solid color only (no icon), per Google's recommendation.
 */
class SplashHostActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startActivityWithSolidColorSplash(intent)
        } else {
            startActivity(intent)
        }
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun startActivityWithSolidColorSplash(intent: Intent) {
        val options = android.app.ActivityOptions.makeBasic()
        options.setSplashScreenStyle(android.window.SplashScreen.SPLASH_SCREEN_STYLE_SOLID_COLOR)
        startActivity(intent, options.toBundle())
    }
}
