package com.lordj.fitnessapp.ui.screens.workouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.data.model.Workout
import com.lordj.fitnessapp.ui.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutListScreen(
    padding: PaddingValues,
    onWorkoutClick: (Long) -> Unit,
    onCreateWorkout: () -> Unit = {}
) {
    val app = LocalContext.current.applicationContext as FitnessApp
    val vm: WorkoutViewModel = viewModel(factory = WorkoutViewModel.Factory(app.workoutRepository))
    val workouts by vm.workouts.collectAsStateWithLifecycle()

    val userRoutines = workouts.filter { it.isUserRoutine }
    val otherRoutines = workouts.filter { !it.isUserRoutine }

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            TopAppBar(title = { Text("Rutinas", fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateWorkout) {
                Icon(Icons.Filled.Add, "Crear rutina")
            }
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (userRoutines.isNotEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Tu Rutina PPL", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold)
                    }
                }
                items(userRoutines) { workout ->
                    WorkoutCard(workout = workout, onClick = { onWorkoutClick(workout.id) }, highlighted = true)
                }
            }

            if (otherRoutines.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(4.dp))
                    Text("Otras Rutinas", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                }
                items(otherRoutines) { workout ->
                    WorkoutCard(workout = workout, onClick = { onWorkoutClick(workout.id) })
                }
            }
        }
    }
}

@Composable
fun WorkoutCard(workout: Workout, onClick: () -> Unit, highlighted: Boolean = false) {
    val color = parseColor(workout.colorHex)
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(if (highlighted) 4.dp else 2.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.width(6.dp).height(IntrinsicSize.Min)
                .background(color).fillMaxHeight())
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                if (workout.dayLabel.isNotBlank()) {
                    Text(workout.dayLabel, style = MaterialTheme.typography.labelMedium,
                        color = color, fontWeight = FontWeight.Bold)
                }
                Text(workout.name, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                if (workout.description.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(workout.description, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Timer, null, modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Spacer(Modifier.width(4.dp))
                    Text("~${workout.estimatedMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
            Icon(Icons.Filled.ChevronRight, null,
                modifier = Modifier.align(Alignment.CenterVertically).padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}

fun parseColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) {
    Color(0xFF6200EE)
}
