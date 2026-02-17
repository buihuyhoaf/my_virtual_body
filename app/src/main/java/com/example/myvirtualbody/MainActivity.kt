package com.example.myvirtualbody

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.myvirtualbody.ui.theme.MyVirtualBodyTheme
import com.example.myvirtualbody.viewer.ModelViewerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyVirtualBodyTheme {
                ModelViewerScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}