package com.lordj.fitnessapp.ui.screens.exercises

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.data.model.SetLog
import com.lordj.fitnessapp.ui.components.VolumeBarChart
import com.lordj.fitnessapp.ui.components.WeightProgressChart
import com.lordj.fitnessapp.ui.theme.categoryColor
import com.lordj.fitnessapp.ui.viewmodel.DayProgress
import com.lordj.fitnessapp.ui.viewmodel.ExerciseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: Long,
    onBack: () -> Unit,
    onQuickTrain: ((Long) -> Unit)? = null
) {
    val app = LocalContext.current.applicationContext as FitnessApp
    val context = LocalContext.current
    val vm: ExerciseViewModel = viewModel(factory = ExerciseViewModel.Factory(app.exerciseRepository))

    LaunchedEffect(exerciseId) { vm.loadExercise(exerciseId) }

    val exercise by vm.exercise.collectAsStateWithLifecycle()
    val sets by vm.sets.collectAsStateWithLifecycle()
    val maxWeight by vm.maxWeight.collectAsStateWithLifecycle()

    val catColor = exercise?.let { categoryColor(it.category) } ?: MaterialTheme.colorScheme.primary

    val progressData = remember(sets) {
        sets.filter { !it.isWarmup }
            .groupBy { it.completedAt / 86400000L }
            .entries.sortedBy { it.key }
            .map { (_, daySets) ->
                DayProgress(
                    dateLabel = SimpleDateFormat("dd/MM", Locale.getDefault())
                        .format(Date(daySets.first().completedAt)),
                    maxWeightKg = daySets.maxOf { it.weightKg },
                    totalVolume = daySets.sumOf { it.volume },
                    totalReps = daySets.sumOf { it.reps },
                    e1RM = daySets.maxOf { it.e1RM }
                )
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exercise?.name ?: "Ejercicio") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) }
                },
                actions = {
                    exercise?.let { ex ->
                        IconButton(onClick = {
                            val query = Uri.encode("${ex.nameEn.ifBlank { ex.name }} exercise tutorial")
                            val uri = Uri.parse("https://www.youtube.com/results?search_query=$query")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }) {
                            Icon(Icons.Filled.PlayCircle, "Ver demostración en YouTube")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = catColor.copy(alpha = 0.1f)
                )
            )
        },
        floatingActionButton = {
            if (onQuickTrain != null && exercise != null) {
                ExtendedFloatingActionButton(
                    onClick = { onQuickTrain(exerciseId) },
                    icon = { Icon(Icons.Filled.FitnessCenter, null) },
                    text = { Text("Entrenar solo") },
                    containerColor = catColor,
                    contentColor = Color.White
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            exercise?.let { ex ->
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                            .background(catColor.copy(alpha = 0.12f)).padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(categoryEmoji(ex.category),
                                        style = MaterialTheme.typography.displaySmall)
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(ex.name, style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold)
                                        Text(ex.category, style = MaterialTheme.typography.labelLarge,
                                            color = catColor)
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    MuscleChip(ex.primaryMuscle, catColor)
                                    ex.secondaryMuscles.split(",").filter { it.isNotBlank() }.take(2).forEach {
                                        MuscleChip(it.trim(), catColor.copy(alpha = 0.6f))
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                EquipmentBadge(ex.equipment)
                            }
                            ExercisePictogram(
                                exerciseName = ex.name,
                                color = catColor,
                                size = 96.dp,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }
                    }
                }

                // Muscle activation visualization
                item {
                    MuscleActivationCard(
                        primaryMuscle = ex.primaryMuscle,
                        secondaryMuscles = ex.secondaryMuscles,
                        color = catColor
                    )
                }

                // Stats row
                if (maxWeight > 0) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatBox("Máx Peso", "${maxWeight} kg", Modifier.weight(1f))
                            StatBox("Sesiones", "${sets.map { it.sessionId }.distinct().size}", Modifier.weight(1f))
                            StatBox("Series", "${sets.filter { !it.isWarmup }.size}", Modifier.weight(1f))
                        }
                    }
                }

                item {
                    SectionTitle("Descripción")
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(ex.description, style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp))
                    }
                }

                if (ex.executionSteps.isNotBlank()) {
                    item {
                        SectionTitle("Cómo realizarlo")
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ex.executionSteps.split("\n").filter { it.isNotBlank() }
                                    .forEachIndexed { i, step ->
                                        Row(verticalAlignment = Alignment.Top) {
                                            Text("${i + 1}.",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = catColor,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.width(24.dp))
                                            Text(step.trimStart { it.isDigit() || it == '.' || it == ' ' },
                                                style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                            }
                        }
                    }
                }

                if (ex.tips.isNotBlank()) {
                    item {
                        SectionTitle("Consejos")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Row(Modifier.padding(16.dp)) {
                                Icon(Icons.Filled.Lightbulb, null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer)
                                Spacer(Modifier.width(8.dp))
                                Text(ex.tips, style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer)
                            }
                        }
                    }
                }

                // YouTube button
                item {
                    OutlinedButton(
                        onClick = {
                            val query = Uri.encode("${ex.nameEn.ifBlank { ex.name }} exercise tutorial")
                            val uri = Uri.parse("https://www.youtube.com/results?search_query=$query")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.PlayCircle, null, modifier = Modifier.size(20.dp),
                            tint = Color(0xFFFF0000))
                        Spacer(Modifier.width(8.dp))
                        Text("Ver demostración en YouTube")
                    }
                }

                if (progressData.isNotEmpty()) {
                    item {
                        SectionTitle("Progresión de Peso")
                        Card(modifier = Modifier.fillMaxWidth()) {
                            WeightProgressChart(
                                data = progressData,
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                lineColor = catColor
                            )
                        }
                    }
                    item {
                        SectionTitle("Volumen por Sesión")
                        Card(modifier = Modifier.fillMaxWidth()) {
                            VolumeBarChart(
                                data = progressData,
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                barColor = catColor
                            )
                        }
                    }
                }

                val recentSets = sets.filter { !it.isWarmup }.take(20)
                if (recentSets.isNotEmpty()) {
                    item { SectionTitle("Historial reciente") }
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text("Fecha", Modifier.weight(2f), style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary)
                                    Text("Serie", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary)
                                    Text("Reps", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary)
                                    Text("Peso", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                val fmt = SimpleDateFormat("dd/MM", Locale.getDefault())
                                recentSets.take(15).forEach { set ->
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                        Text(fmt.format(Date(set.completedAt)), Modifier.weight(2f),
                                            style = MaterialTheme.typography.bodySmall)
                                        Text("${set.setNumber}", Modifier.weight(1f),
                                            style = MaterialTheme.typography.bodySmall)
                                        Text("${set.reps}", Modifier.weight(1f),
                                            style = MaterialTheme.typography.bodySmall)
                                        Text("${set.weightKg} kg", Modifier.weight(1f),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold)
                                    }
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
private fun MuscleActivationCard(primaryMuscle: String, secondaryMuscles: String, color: Color) {
    val secondaries = secondaryMuscles.split(",").filter { it.isNotBlank() }.map { it.trim() }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Músculos trabajados",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold)

            MuscleBar(muscle = primaryMuscle, fraction = 1f, color = color, label = "Principal")

            secondaries.take(3).forEachIndexed { i, muscle ->
                val frac = when (i) { 0 -> 0.6f; 1 -> 0.4f; else -> 0.25f }
                MuscleBar(muscle = muscle, fraction = frac, color = color.copy(alpha = 0.6f), label = "Secundario")
            }
        }
    }
}

@Composable
private fun MuscleBar(muscle: String, fraction: Float, color: Color, label: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(muscle, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
            color = color,
            trackColor = color.copy(alpha = 0.15f),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun MuscleChip(label: String, color: Color) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
}

private fun categoryEmoji(cat: String) = when (cat) {
    "Pecho" -> "💪"; "Espalda" -> "🔷"; "Hombros" -> "🏋️"
    "Brazos" -> "💪"; "Piernas" -> "🦵"; "Core" -> "🎯"; "Cardio" -> "❤️"
    else -> "🏃"
}
