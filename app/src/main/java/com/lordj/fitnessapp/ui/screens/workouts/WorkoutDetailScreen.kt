package com.lordj.fitnessapp.ui.screens.workouts

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.lordj.fitnessapp.data.model.Exercise
import com.lordj.fitnessapp.data.model.WorkoutExercise
import com.lordj.fitnessapp.ui.theme.categoryColor
import com.lordj.fitnessapp.ui.viewmodel.WorkoutExerciseDetail
import com.lordj.fitnessapp.ui.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workoutId: Long,
    onBack: () -> Unit,
    onStartWorkout: (Long) -> Unit,
    onExerciseDetail: ((Long) -> Unit)? = null
) {
    val app = LocalContext.current.applicationContext as FitnessApp
    val context = LocalContext.current
    val vm: WorkoutViewModel = viewModel(factory = WorkoutViewModel.Factory(app.workoutRepository))

    LaunchedEffect(workoutId) { vm.loadWorkout(workoutId) }

    val workout by vm.workout.collectAsStateWithLifecycle()
    val exerciseDetails by vm.exerciseDetails.collectAsStateWithLifecycle()

    val color = workout?.let { parseColor(it.colorHex) } ?: MaterialTheme.colorScheme.primary
    var expandedId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout?.name ?: "Rutina") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = color.copy(alpha = 0.1f)
                )
            )
        },
        floatingActionButton = {
            workout?.let { w ->
                ExtendedFloatingActionButton(
                    onClick = { onStartWorkout(w.id) },
                    icon = { Icon(Icons.Filled.PlayArrow, null) },
                    text = { Text("Empezar", fontWeight = FontWeight.Bold) },
                    containerColor = color,
                    contentColor = Color.White
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header card
            workout?.let { w ->
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(color.copy(alpha = 0.12f))
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (w.dayLabel.isNotBlank()) {
                                Text(w.dayLabel, style = MaterialTheme.typography.labelLarge,
                                    color = color, fontWeight = FontWeight.Bold)
                            }
                            Text(w.name, style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold)
                            if (w.description.isNotBlank()) {
                                Text(w.description, style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                            HorizontalDivider(color = color.copy(alpha = 0.2f))
                            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                InfoStat(Icons.Filled.FitnessCenter, "${exerciseDetails.size} ejercicios")
                                InfoStat(Icons.Filled.Timer, "~${w.estimatedMinutes} min")
                            }
                        }
                    }
                }

                item {
                    Text("Plan de entrenamiento",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                }
            }

            itemsIndexed(exerciseDetails, key = { _, d -> d.workoutExercise.id }) { idx, detail ->
                val isCardio = detail.exercise.category == "Cardio"
                WorkoutExerciseCard(
                    index = idx + 1,
                    detail = detail,
                    isCardio = isCardio,
                    expanded = expandedId == detail.workoutExercise.id,
                    onToggle = {
                        expandedId = if (expandedId == detail.workoutExercise.id) null
                                    else detail.workoutExercise.id
                    },
                    onYouTube = {
                        val ex = detail.exercise
                        val query = Uri.encode("${ex.nameEn.ifBlank { ex.name }} exercise tutorial")
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/results?search_query=$query"))
                        )
                    },
                    onExerciseDetail = onExerciseDetail?.let { nav -> { nav(detail.exercise.id) } }
                )
            }
        }
    }
}

@Composable
private fun WorkoutExerciseCard(
    index: Int,
    detail: WorkoutExerciseDetail,
    isCardio: Boolean,
    expanded: Boolean,
    onToggle: () -> Unit,
    onYouTube: () -> Unit,
    onExerciseDetail: (() -> Unit)?
) {
    val we = detail.workoutExercise
    val ex = detail.exercise
    val catColor = categoryColor(ex.category)
    val secondary = ex.secondaryMuscles.split(",").filter { it.isNotBlank() }.map { it.trim() }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            // ── Collapsed header ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Index badge
                Box(
                    modifier = Modifier.size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(catColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCardio) {
                        Icon(Icons.Filled.DirectionsRun, null,
                            modifier = Modifier.size(18.dp), tint = catColor)
                    } else {
                        Text("$index", style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold, color = catColor)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(ex.name, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(3.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatPill("${we.targetSets}×${we.targetReps}", catColor)
                        if (we.restSeconds > 0) {
                            StatPill("${we.restSeconds}s descanso", MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                        }
                    }
                }
                // YouTube icon
                IconButton(onClick = onYouTube, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.PlayCircle, "YouTube",
                        tint = Color(0xFFFF0000), modifier = Modifier.size(22.dp))
                }
                Icon(
                    if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                )
            }

            // ── Expanded body ────────────────────────────────────────────────
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider()
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Muscle activation
                        if (!isCardio) {
                            MuscleActivationSection(ex, catColor, secondary)
                        }

                        // Description
                        if (ex.description.isNotBlank()) {
                            Text(ex.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }

                        // Execution steps
                        if (ex.executionSteps.isNotBlank()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Ejecución",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = catColor)
                                ex.executionSteps.split("\n").filter { it.isNotBlank() }
                                    .forEachIndexed { i, step ->
                                        Row(verticalAlignment = Alignment.Top) {
                                            Text("${i + 1}.",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = catColor,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.width(20.dp))
                                            Text(
                                                step.trimStart { it.isDigit() || it == '.' || it == ' ' },
                                                style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                            }
                        }

                        // Coach note
                        if (we.notes.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(catColor.copy(alpha = 0.08f))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.Lightbulb, null,
                                    modifier = Modifier.size(16.dp), tint = catColor)
                                Column {
                                    Text("Nota de coach",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold, color = catColor)
                                    Spacer(Modifier.height(2.dp))
                                    Text(we.notes, style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                                }
                            }
                        }

                        // Tips
                        if (ex.tips.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.Star, null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.tertiary)
                                Text(ex.tips, style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer)
                            }
                        }

                        // Bottom actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onYouTube,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.PlayCircle, null,
                                    modifier = Modifier.size(16.dp), tint = Color(0xFFFF0000))
                                Spacer(Modifier.width(4.dp))
                                Text("Ver demo", style = MaterialTheme.typography.labelMedium)
                            }
                            if (onExerciseDetail != null) {
                                OutlinedButton(
                                    onClick = onExerciseDetail,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.BarChart, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Mi progreso", style = MaterialTheme.typography.labelMedium)
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
private fun MuscleActivationSection(ex: Exercise, color: Color, secondary: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Músculos",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color)
        MuscleBar(ex.primaryMuscle, 1f, color, "Principal")
        secondary.take(2).forEachIndexed { i, muscle ->
            MuscleBar(muscle, if (i == 0) 0.6f else 0.35f, color.copy(alpha = 0.6f), "Secundario")
        }
    }
}

@Composable
private fun MuscleBar(muscle: String, fraction: Float, color: Color, label: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(muscle, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(50)),
            color = color,
            trackColor = color.copy(alpha = 0.12f),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun StatPill(text: String, color: Color) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    )
}

@Composable
private fun InfoStat(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Text(text, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}
