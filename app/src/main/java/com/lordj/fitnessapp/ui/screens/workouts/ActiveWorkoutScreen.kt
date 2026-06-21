package com.lordj.fitnessapp.ui.screens.workouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.ui.viewmodel.ActiveWorkoutViewModel
import com.lordj.fitnessapp.ui.viewmodel.WorkoutPhase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(workoutId: Long, onFinish: () -> Unit, onBack: () -> Unit) {
    val app = LocalContext.current.applicationContext as FitnessApp
    val vm: ActiveWorkoutViewModel = viewModel(factory = ActiveWorkoutViewModel.Factory(app.workoutRepository))

    val phase by vm.phase.collectAsStateWithLifecycle()
    val exerciseStates by vm.exerciseStates.collectAsStateWithLifecycle()
    val elapsed by vm.elapsedSeconds.collectAsStateWithLifecycle()
    var showExitDialog by remember { mutableStateOf(false) }

    // Start workout
    LaunchedEffect(workoutId) {
        if (phase == WorkoutPhase.NotStarted) {
            val workout = app.workoutRepository.getWorkoutById(workoutId) ?: return@LaunchedEffect
            val wes = app.workoutRepository.getWorkoutExercisesList(workoutId)
            vm.startWorkout(workout, wes)
        }
    }

    // Navigate on finish
    LaunchedEffect(phase) {
        if (phase == WorkoutPhase.Finished) onFinish()
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("¿Salir del entrenamiento?") },
            text = { Text("El progreso guardado se mantendrá, pero el entrenamiento quedará sin finalizar.") },
            confirmButton = {
                TextButton(onClick = { vm.finishWorkout() }) { Text("Finalizar y guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("Continuar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Entrenamiento activo", style = MaterialTheme.typography.titleMedium)
                        Text(formatTime(elapsed), style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(Icons.Filled.Close, null)
                    }
                },
                actions = {
                    Button(
                        onClick = { vm.finishWorkout() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Finalizar")
                    }
                }
            )
        }
    ) { padding ->
        when (val p = phase) {
            is WorkoutPhase.NotStarted -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is WorkoutPhase.InProgress, is WorkoutPhase.Resting -> {
                val currentIndex = when (p) {
                    is WorkoutPhase.InProgress -> p.exerciseIndex
                    is WorkoutPhase.Resting -> p.exerciseIndex
                    else -> 0
                }
                val isResting = p is WorkoutPhase.Resting
                val restLeft = if (isResting) (p as WorkoutPhase.Resting).secondsLeft else 0

                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    // Progress indicator
                    LinearProgressIndicator(
                        progress = { (currentIndex.toFloat() + 1f) / exerciseStates.size.coerceAtLeast(1) },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Exercise navigation tabs
                    ScrollableTabRow(
                        selectedTabIndex = currentIndex,
                        edgePadding = 8.dp,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        exerciseStates.forEachIndexed { i, state ->
                            Tab(
                                selected = i == currentIndex,
                                onClick = { vm.goToExercise(i) },
                                text = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${i + 1}", style = MaterialTheme.typography.labelSmall)
                                        if (state.loggedSets.isNotEmpty()) {
                                            Box(Modifier.size(6.dp).clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary))
                                        }
                                    }
                                }
                            )
                        }
                    }

                    val currentState = exerciseStates.getOrNull(currentIndex)
                    if (currentState != null) {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                // Exercise header
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(currentState.exercise.name,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                                        Text("Objetivo: ${currentState.workoutExercise.targetSets} series × ${currentState.workoutExercise.targetReps} reps",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                        if (currentState.suggestedWeight > 0) {
                                            Text("Último peso: ${currentState.suggestedWeight} kg",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                        }
                                    }
                                }
                            }

                            // Rest timer overlay
                            if (isResting && restLeft > 0) {
                                item {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("Descanso", style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.tertiary)
                                            Text("$restLeft", style = MaterialTheme.typography.displaySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.tertiary,
                                                fontSize = 56.sp)
                                            Text("segundos", style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                            Spacer(Modifier.height(12.dp))
                                            OutlinedButton(onClick = { vm.skipRest() }) {
                                                Icon(Icons.Filled.SkipNext, null)
                                                Spacer(Modifier.width(4.dp))
                                                Text("Saltar descanso")
                                            }
                                        }
                                    }
                                }
                            }

                            // Previously logged sets
                            if (currentState.loggedSets.isNotEmpty()) {
                                item {
                                    Text("Series registradas", style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold)
                                }
                                itemsIndexed(currentState.loggedSets) { idx, set ->
                                    LoggedSetRow(
                                        setNumber = idx + 1,
                                        reps = set.reps,
                                        weight = set.weightKg,
                                        isWarmup = set.isWarmup,
                                        onDelete = if (idx == currentState.loggedSets.size - 1) {
                                            { vm.removeLastSet(currentIndex) }
                                        } else null
                                    )
                                }
                            }

                            // Input new set
                            if (!isResting || restLeft == 0) {
                                item {
                                    LogSetInput(
                                        suggestedWeight = currentState.suggestedWeight,
                                        setNumber = currentState.loggedSets.size + 1,
                                        onLogSet = { reps, weight, warmup ->
                                            vm.logSet(currentIndex, reps, weight, warmup)
                                        }
                                    )
                                }
                            }

                            // Prev/Next navigation
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    OutlinedButton(
                                        onClick = { vm.goToExercise(currentIndex - 1) },
                                        enabled = currentIndex > 0
                                    ) {
                                        Icon(Icons.Filled.NavigateBefore, null)
                                        Text("Anterior")
                                    }
                                    if (currentIndex < exerciseStates.size - 1) {
                                        Button(onClick = { vm.goToExercise(currentIndex + 1) }) {
                                            Text("Siguiente")
                                            Icon(Icons.Filled.NavigateNext, null)
                                        }
                                    } else {
                                        Button(
                                            onClick = { vm.finishWorkout() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.secondary
                                            )
                                        ) {
                                            Icon(Icons.Filled.CheckCircle, null)
                                            Spacer(Modifier.width(4.dp))
                                            Text("Finalizar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun LoggedSetRow(
    setNumber: Int,
    reps: Int,
    weight: Double,
    isWarmup: Boolean,
    onDelete: (() -> Unit)?
) {
    val bg = if (isWarmup)
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)

    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(bg).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(28.dp).clip(CircleShape)
                .background(if (isWarmup) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text("$setNumber", style = MaterialTheme.typography.labelSmall,
                color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Text(if (isWarmup) "(Calentamiento) " else "", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Text("$reps reps", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold)
        Text("$weight kg", style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        if (onDelete != null) {
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Filled.DeleteOutline, null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun LogSetInput(suggestedWeight: Double, setNumber: Int, onLogSet: (Int, Double, Boolean) -> Unit) {
    var repsText by remember(setNumber) { mutableStateOf("10") }
    var weightText by remember(setNumber) { mutableStateOf(if (suggestedWeight > 0) suggestedWeight.toString() else "") }
    var isWarmup by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Serie $setNumber", style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = repsText,
                    onValueChange = { repsText = it.filter { c -> c.isDigit() } },
                    label = { Text("Reps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    trailingIcon = {
                        Column {
                            IconButton(onClick = { repsText = ((repsText.toIntOrNull() ?: 0) + 1).toString() },
                                modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Filled.KeyboardArrowUp, null, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = {
                                val v = (repsText.toIntOrNull() ?: 1) - 1
                                if (v > 0) repsText = v.toString()
                            }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Filled.KeyboardArrowDown, null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                )
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    trailingIcon = {
                        Column {
                            IconButton(onClick = {
                                val v = (weightText.toDoubleOrNull() ?: 0.0) + 2.5
                                weightText = v.toBigDecimal().stripTrailingZeros().toPlainString()
                            }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Filled.KeyboardArrowUp, null, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = {
                                val v = (weightText.toDoubleOrNull() ?: 2.5) - 2.5
                                if (v >= 0) weightText = v.toBigDecimal().stripTrailingZeros().toPlainString()
                            }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Filled.KeyboardArrowDown, null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isWarmup, onCheckedChange = { isWarmup = it })
                Text("Calentamiento", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    val reps = repsText.toIntOrNull() ?: return@Button
                    val weight = weightText.toDoubleOrNull() ?: 0.0
                    if (reps > 0) onLogSet(reps, weight, isWarmup)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Check, null)
                Spacer(Modifier.width(8.dp))
                Text("Registrar Serie")
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}
