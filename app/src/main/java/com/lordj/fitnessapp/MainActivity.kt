package com.lordj.fitnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lordj.fitnessapp.ui.navigation.FitnessNavGraph
import com.lordj.fitnessapp.ui.theme.FitnessTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = applicationContext as FitnessApp
            val themeMode by app.preferences.themeMode.collectAsState()
            FitnessTrackerTheme(themeMode = themeMode) {
                FitnessNavGraph()
            }
        }
    }
}
