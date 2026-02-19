package com.example.myvirtualbody

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.myvirtualbody.ui.body.BodyAnalysisScreen
import com.example.myvirtualbody.ui.body.BodyTab
import com.example.myvirtualbody.ui.body.BodyUiState
import com.example.myvirtualbody.ui.theme.MyVirtualBodyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyVirtualBodyTheme {
                var selectedTab by remember { mutableStateOf(BodyTab.Body) }
                val uiState = remember {
                    BodyUiState(
                        height = "180",
                        heightUnit = "cm",
                        weight = "75.5",
                        weightUnit = "kg",
                        weightProgress = 0.65f,
                        bodyFat = "15.4",
                        bodyFatProgress = 0.4f,
                        muscleMass = "62.0",
                        muscleMassUnit = "kg",
                        muscleMassProgress = 0.78f,
                        bmi = "23.2",
                        bmiStatus = "Normal",
                        bmiScalePosition = 0.45f
                    )
                }

                BodyAnalysisScreen(
                    uiState = uiState,
                    selectedTab = selectedTab,
                    title = "My Body",
                    onBackClick = { finish() },
                    onSettingsClick = { /* Handle settings */ },
                    onTabSelected = { tab: BodyTab -> selectedTab = tab },
                    onEditClick = { /* Handle edit */ },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}