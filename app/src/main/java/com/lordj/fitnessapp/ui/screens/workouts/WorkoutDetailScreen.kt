package com.lordj.fitnessapp.ui.screens.workouts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.data.model.WorkoutExercise
import com.lordj.fitnessapp.ui.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workoutId: Long,
    onBack: () -> Unit,
    onStartWorkout: (Long) -> Unit
) {
    val app = LocalContext.current.applicationContext as FitnessApp
    val vm: WorkoutViewModel = viewModel(factory = WorkoutViewModel.Factory(app.workoutRepository))

    LaunchedEffect(workoutId) { vm.loadWorkout(workoutId) }

    val workout by vm.workout.collectAsStateWithLifecycle()
    val exercises by vm.workoutExercises.collectAsStateWithLifecycle()
    val exerciseNames = remember { mutableStateMapOf<Long, String>() }

    LaunchedEffect(exercises) {
        exercises.forEach { we ->
            if (!exerciseNames.containsKey(we.exerciseId)) {
                val ex = app.workoutRepository.getExerciseById(we.exerciseId)
                ex?.let { exerciseNames[we.exerciseId] = it.name }
            }
        }
    }

    val color = workout?.let { parseColor(it.colorHex) } ?: MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout?.name ?: "Rutina") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) }
                }
            )
        },
        floatingActionButton = {
            workout?.let { w ->
                ExtendedFloatingActionButton(
                    onClick = { onStartWorkout(w.id) },
                    icon = { Icon(Icons.Filled.PlayArrow, null) },
                    text = { Text("Empezar Entrenamiento") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            workout?.let { w ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            if (w.dayLabel.isNotBlank()) {
                                Text(w.dayLabel, style = MaterialTheme.typography.labelLarge,
                                    color = color, fontWeight = FontWeight.Bold)
                            }
                            Text(w.name, style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold)
                            if (w.description.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(w.description, style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                InfoBadge(Icons.Filled.FitnessCenter, "${exercises.size} ejercicios")
                                InfoBadge(Icons.Filled.Timer, "~${w.estimatedMinutes} min")
                            }
                        }
                    }
                }

                item {
                    Text("Ejercicios", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                }
            }

            itemsIndexed(exercises) { index, we ->
                WorkoutExerciseRow(
                    index = index + 1,
                    workoutExercise = we,
                    exerciseName = exerciseNames[we.exerciseId] ?: "Cargando..."
                )
            }
        }
    }
}

@Composable
private fun InfoBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun WorkoutExerciseRow(index: Int, workoutExercise: WorkoutExercise, exerciseName: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Number badge
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("$index", style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(exerciseName, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("${workoutExercise.targetSets} x ${workoutExercise.targetReps}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    if (workoutExercise.restSeconds > 0) {
                        Text("Descanso: ${workoutExercise.restSeconds}s",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}
