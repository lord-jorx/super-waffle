package com.lordj.fitnessapp.ui.screens.garmin

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lordj.fitnessapp.ui.theme.FitnessTrackerTheme

class GarminPermissionRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitnessTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Watch, null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "¿Por qué necesitamos acceso?",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "FitnessTracker lee tus sesiones de ejercicio, frecuencia cardíaca y calorías " +
                            "desde Health Connect para mostrarte tus entrenamientos de Garmin junto a tus " +
                            "rutinas de gimnasio. Nunca escribimos ni compartimos tus datos de salud.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { setResult(Activity.RESULT_OK); finish() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Entendido")
                        }
                    }
                }
            }
        }
    }
}
