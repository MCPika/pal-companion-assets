package com.example.palcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.palcompanion.ui.PalCompanionApp
import com.example.palcompanion.ui.theme.PalCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PalCompanionTheme {
                PalCompanionApp()
            }
        }
    }
}
