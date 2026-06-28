package com.lordj.fitnessapp.ui.screens.exercises

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.data.model.SetLog
import com.lordj.fitnessapp.data.model.WorkoutSession
import com.lordj.fitnessapp.data.repository.WorkoutRepository
import com.lordj.fitnessapp.ui.theme.categoryColor
import com.lordj.fitnessapp.ui.viewmodel.ExerciseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickExerciseScreen(
    exerciseId: Long,
    onBack: () -> Unit
) {
    val app = LocalContext.current.applicationContext as FitnessApp
    val exVm: ExerciseViewModel = viewModel(
        factory = ExerciseViewModel.Factory(app.exerciseRepository)
    )
    val repo = app.workoutRepository
    val scope = rememberCoroutineScope()

    LaunchedEffect(exerciseId) { exVm.loadExercise(exerciseId) }

    val exercise by exVm.exercise.collectAsStateWithLifecycle()
    val allSets by exVm.sets.collectAsStateWithLifecycle()

    var sessionId by remember { mutableStateOf<Long?>(null) }
    var loggedSets by remember { mutableStateOf<List<SetLog>>(emptyList()) }

    var reps by remember { mutableStateOf("10") }
    var weight by remember { mutableStateOf("") }
    var isWarmup by remember { mutableStateOf(false) }

    val catColor = exercise?.let { categoryColor(it.category) } ?: MaterialTheme.colorScheme.primary
    val lastWeight = remember(allSets) {
        allSets.filter { !it.isWarmup }.maxOfOrNull { it.weightKg }
    }

    LaunchedEffect(exercise, sessionId) {
        if (exercise != null && sessionId == null) {
            val startTime = System.currentTimeMillis()
            val id = repo.startSession(
                WorkoutSession(
                    workoutId = null,
                    workoutName = exercise!!.name,
                    startTime = startTime
                )
            )
            sessionId = id
            if (lastWeight != null && lastWeight > 0) weight = lastWeight.toString()
        }
    }

    fun logSet() {
        val sid = sessionId ?: return
        val ex = exercise ?: return
        val r = reps.toIntOrNull() ?: return
        val w = weight.toDoubleOrNull() ?: 0.0
        val setNum = loggedSets.count { !it.isWarmup } + (if (isWarmup) 0 else 1)
        val warmNum = loggedSets.count { it.isWarmup } + (if (isWarmup) 1 else 0)
        val newSet = SetLog(
            sessionId = sid,
            exerciseId = ex.id,
            exerciseName = ex.name,
            setNumber = if (isWarmup) warmNum else setNum,
            reps = r,
            weightKg = w,
            isWarmup = isWarmup,
            completedAt = System.currentTimeMillis()
        )
        scope.launch {
            val setId = repo.logSet(newSet)
            loggedSets = loggedSets + newSet.copy(id = setId)
            val totalVol = loggedSets.sumOf { it.volume }
            val session = repo.getSessionById(sid)
            session?.let {
                repo.updateSession(it.copy(
                    totalVolumeKg = totalVol,
                    totalSets = loggedSets.filter { s -> !s.isWarmup }.size
                ))
            }
        }
    }

    fun finishSession() {
        val sid = sessionId ?: return
        scope.launch {
            val session = repo.getSessionById(sid)
            session?.let {
                repo.updateSession(it.copy(endTime = System.currentTimeMillis()))
            }
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exercise?.name ?: "Ejercicio") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (loggedSets.isEmpty()) {
                            sessionId?.let { sid ->
                                scope.launch {
                                    val s = repo.getSessionById(sid)
                                    s?.let { repo.deleteSession(it) }
                                    onBack()
                                }
                            } ?: onBack()
                        } else {
                            finishSession()
                        }
                    }) { Icon(Icons.Filled.ArrowBack, "Volver") }
                },
                actions = {
                    if (loggedSets.isNotEmpty()) {
                        TextButton(onClick = { finishSession() }) {
                            Text("Terminar", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = catColor.copy(alpha = 0.08f)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input card
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Registrar serie",
                            style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = reps,
                                onValueChange = { reps = it },
                                label = { Text("Reps") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = weight,
                                onValueChange = { weight = it },
                                label = { Text("Peso (kg)") },
                                placeholder = { Text(if (lastWeight != null) "${lastWeight}kg" else "0") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = isWarmup, onCheckedChange = { isWarmup = it })
                                Text("Serie de calentamiento",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            Button(
                                onClick = { logSet() },
                                enabled = reps.toIntOrNull() != null && reps.toIntOrNull()!! > 0
                            ) {
                                Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Añadir")
                            }
                        }
                    }
                }
            }

            // Logged sets
            if (loggedSets.isNotEmpty()) {
                item {
                    Text("Series de hoy (${loggedSets.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold)
                }
                itemsIndexed(loggedSets) { idx, set ->
                    LoggedSetRow(index = idx + 1, set = set)
                }

                item {
                    val workSets = loggedSets.filter { !it.isWarmup }
                    if (workSets.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                SummaryChip("Series", "${workSets.size}")
                                SummaryChip("Volumen", "${workSets.sumOf { it.volume }.toInt()} kg")
                                workSets.maxOfOrNull { it.e1RM }?.let {
                                    SummaryChip("1RM est.", "${it.toInt()} kg")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoggedSetRow(index: Int, set: SetLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (set.isWarmup)
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        else CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$index",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(28.dp))
            if (set.isWarmup) {
                Text("Calent.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.width(60.dp))
            } else {
                Spacer(Modifier.width(60.dp))
            }
            Text("${set.reps} reps",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f))
            if (set.weightKg > 0) {
                Text("${set.weightKg} kg",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary)
            } else {
                Text("Peso corporal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
    }
}
