package com.lordj.fitnessapp.ui.screens.programs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.data.programs.Difficulty
import com.lordj.fitnessapp.data.programs.ProgramExercise
import com.lordj.fitnessapp.data.programs.ProgramLibrary
import com.lordj.fitnessapp.data.programs.ProgramTemplate
import com.lordj.fitnessapp.data.model.Workout
import com.lordj.fitnessapp.data.model.WorkoutExercise
import com.lordj.fitnessapp.ui.viewmodel.ExerciseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailScreen(
    programId: String,
    onBack: () -> Unit,
    onSavedAsRoutine: () -> Unit
) {
    val program = ProgramLibrary.all.find { it.id == programId } ?: run { onBack(); return }
    val context = LocalContext.current
    val app = context.applicationContext as FitnessApp
    val repo = app.workoutRepository
    val exRepo = app.exerciseRepository
    val scope = rememberCoroutineScope()

    val brandColor = parseProgramColor(program.colorHex)
    var saving by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

    LaunchedEffect(saved) {
        if (saved) onSavedAsRoutine()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(program.title, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Volver") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = brandColor.copy(alpha = 0.12f)
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            if (!saving) {
                                saving = true
                                scope.launch {
                                    saveAsRoutine(program, repo, exRepo)
                                    saved = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = brandColor),
                        enabled = !saving
                    ) {
                        if (saving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                        } else {
                            Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                        }
                        Text("Guardar como mi rutina", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(brandColor.copy(alpha = 0.12f))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(program.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(program.subtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetaChip(Icons.Filled.FitnessCenter, program.bodyPart, brandColor)
                            MetaChip(Icons.Filled.BarChart, program.goal, brandColor)
                            MetaChip(Icons.Filled.Star, program.difficulty.label, brandColor)
                        }

                        HorizontalDivider(color = brandColor.copy(alpha = 0.2f))

                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            InfoStat(Icons.Filled.Timer, "${program.estimatedMinutes} min")
                            InfoStat(Icons.Filled.DateRange, program.frequency)
                            InfoStat(Icons.Filled.List, "${program.exercises.size} ejercicios")
                        }
                    }
                }
            }

            // Principle note
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Filled.Science, null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Por qué funciona",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.height(4.dp))
                            Text(program.principleNote,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }

            // Exercises header
            item {
                Text("Ejercicios del programa",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
            }

            // Exercise list
            itemsIndexed(program.exercises) { idx, ex ->
                ProgramExerciseCard(
                    index = idx + 1,
                    exercise = ex,
                    color = brandColor,
                    onYouTube = {
                        val query = Uri.encode("${ex.exerciseName} exercise tutorial")
                        val uri = Uri.parse("https://www.youtube.com/results?search_query=$query")
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                )
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun ProgramExerciseCard(
    index: Int,
    exercise: ProgramExercise,
    color: Color,
    onYouTube: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier.size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$index",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = color)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(exercise.exerciseName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatPill("${exercise.sets} series")
                        StatPill("${exercise.reps} reps")
                        if (exercise.restSeconds > 0) {
                            StatPill("${exercise.restSeconds}s descanso")
                        }
                    }
                }
                IconButton(
                    onClick = onYouTube,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Filled.PlayCircle, "Ver en YouTube",
                        tint = Color(0xFFFF0000),
                        modifier = Modifier.size(22.dp))
                }
            }

            if (exercise.coachNote.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Filled.Lightbulb, null,
                        modifier = Modifier.size(14.dp),
                        tint = color.copy(alpha = 0.8f))
                    Spacer(Modifier.width(6.dp))
                    Text(exercise.coachNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun MetaChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(12.dp), tint = color)
        Text(text, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun InfoStat(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Text(text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
private fun StatPill(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

private fun parseProgramColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) {
    Color(0xFF6366F1)
}

private suspend fun saveAsRoutine(
    program: ProgramTemplate,
    repo: com.lordj.fitnessapp.data.repository.WorkoutRepository,
    exRepo: com.lordj.fitnessapp.data.repository.ExerciseRepository
) {
    val workout = Workout(
        name = program.title,
        description = program.subtitle,
        dayLabel = "${program.bodyPart} · ${program.goal}",
        colorHex = program.colorHex,
        estimatedMinutes = program.estimatedMinutes,
        isUserRoutine = true,
        orderIndex = 999
    )
    val workoutId = repo.insertWorkout(workout)

    program.exercises.forEachIndexed { idx, programEx ->
        val exercise = exRepo.searchExerciseByName(programEx.exerciseName)
        val exerciseId = exercise?.id ?: return@forEachIndexed
        repo.insertWorkoutExercise(
            WorkoutExercise(
                workoutId = workoutId,
                exerciseId = exerciseId,
                orderIndex = idx,
                targetSets = programEx.sets,
                targetReps = programEx.reps,
                restSeconds = programEx.restSeconds
            )
        )
    }
}
