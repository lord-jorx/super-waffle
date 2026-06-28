package com.lordj.fitnessapp.ui.screens.progress

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.data.model.Exercise
import com.lordj.fitnessapp.ui.components.VolumeBarChart
import com.lordj.fitnessapp.ui.components.WeightProgressChart
import com.lordj.fitnessapp.ui.theme.categoryColor
import com.lordj.fitnessapp.ui.viewmodel.ExercisePR
import com.lordj.fitnessapp.ui.viewmodel.ProgressViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(padding: PaddingValues) {
    val app = LocalContext.current.applicationContext as FitnessApp
    val vm: ProgressViewModel = viewModel(factory = ProgressViewModel.Factory(
        app.workoutRepository, app.exerciseRepository
    ))

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ejercicio", "Records Personales")

    val exercises by vm.exercises.collectAsStateWithLifecycle()
    val selectedExercise by vm.selectedExercise.collectAsStateWithLifecycle()
    val progress by vm.progress.collectAsStateWithLifecycle()
    val prs by vm.prs.collectAsStateWithLifecycle()

    var showExercisePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.loadPersonalRecords() }

    if (showExercisePicker) {
        ExercisePickerDialog(
            exercises = exercises,
            onSelect = { ex ->
                vm.loadExerciseProgress(ex.id)
                showExercisePicker = false
            },
            onDismiss = { showExercisePicker = false }
        )
    }

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            TopAppBar(title = { Text("Progreso", fontWeight = FontWeight.Bold) })
        }
    ) { inner ->
        Column(modifier = Modifier.fillMaxSize().padding(inner)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, tab ->
                    Tab(selected = i == selectedTab, onClick = { selectedTab = i }, text = { Text(tab) })
                }
            }

            when (selectedTab) {
                0 -> ExerciseProgressTab(
                    selectedExercise = selectedExercise,
                    progress = progress,
                    onSelectExercise = { showExercisePicker = true }
                )
                1 -> PersonalRecordsTab(prs = prs)
            }
        }
    }
}

@Composable
private fun ExerciseProgressTab(
    selectedExercise: Exercise?,
    progress: List<com.lordj.fitnessapp.ui.viewmodel.DayProgress>,
    onSelectExercise: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onSelectExercise)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.FitnessCenter, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Ejercicio seleccionado", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text(selectedExercise?.name ?: "Selecciona un ejercicio",
                            style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }
                    Icon(Icons.Filled.ExpandMore, null)
                }
            }
        }

        if (selectedExercise != null && progress.isNotEmpty()) {
            val catColor = categoryColor(selectedExercise.category)
            val maxWeight = progress.maxOf { it.maxWeightKg }
            val totalVolume = progress.sumOf { it.totalVolume }
            val sessions = progress.size

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PRCard("🏆 Máx Peso", "${maxWeight} kg", Modifier.weight(1f))
                    PRCard("📊 Sesiones", "$sessions", Modifier.weight(1f))
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Progresión de Peso", style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        WeightProgressChart(
                            data = progress,
                            modifier = Modifier.fillMaxWidth(),
                            lineColor = catColor
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Volumen por Sesión", style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold)
                        Text("Total: ${(totalVolume / 1000).roundToInt()} toneladas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(Modifier.height(8.dp))
                        VolumeBarChart(
                            data = progress,
                            modifier = Modifier.fillMaxWidth(),
                            barColor = catColor
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("e1RM Estimado", style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold)
                        Text("Fórmula de Epley: peso × (1 + reps/30)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(Modifier.height(8.dp))
                        val maxE1RM = progress.maxOf { it.e1RM }
                        val latestE1RM = progress.last().e1RM
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PRCard("Mejor e1RM", "${maxE1RM.roundToInt()} kg", Modifier.weight(1f))
                            PRCard("Último e1RM", "${latestE1RM.roundToInt()} kg", Modifier.weight(1f))
                        }
                    }
                }
            }

        } else if (selectedExercise != null) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.BarChart, null, modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                            Spacer(Modifier.height(12.dp))
                            Text("Sin datos para ${selectedExercise.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text("Realiza este ejercicio para ver tu progresión",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalRecordsTab(prs: List<ExercisePR>) {
    if (prs.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.EmojiEvents, null, modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                Spacer(Modifier.height(12.dp))
                Text("Sin records aún", style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Text("Completa entrenamientos para ver tus PRs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            }
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("${prs.size} ejercicios con datos", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        items(prs, key = { it.exercise.id }) { pr ->
            PRRow(pr = pr)
        }
    }
}

@Composable
private fun PRRow(pr: ExercisePR) {
    val catColor = categoryColor(pr.exercise.category)
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(pr.exercise.name, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
                Text(pr.exercise.category, style = MaterialTheme.typography.labelSmall, color = catColor)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${pr.maxWeightKg} kg", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("e1RM: ${pr.bestE1RM.roundToInt()} kg", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Text("${pr.totalSessions} sesiones", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun PRCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
private fun ExercisePickerDialog(
    exercises: List<Exercise>,
    onSelect: (Exercise) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona un ejercicio") },
        text = {
            Column(modifier = Modifier.heightIn(max = 400.dp)) {
                LazyColumn {
                    items(exercises) { ex ->
                        ListItem(
                            headlineContent = { Text(ex.name, style = MaterialTheme.typography.bodyMedium) },
                            supportingContent = { Text(ex.category, style = MaterialTheme.typography.labelSmall,
                                color = categoryColor(ex.category)) },
                            modifier = Modifier.clickable { onSelect(ex) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
