package com.hoabui.virtualbody3d

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hoabui.virtualbody3d.ui.body.screen.BodyAnalysisRoute
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTheme {
                BodyAnalysisRoute(onBackClick = { finish() })
            }
        }
    }
}