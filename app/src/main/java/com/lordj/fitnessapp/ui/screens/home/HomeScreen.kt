package com.lordj.fitnessapp.ui.screens.home

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.data.model.Workout
import com.lordj.fitnessapp.data.model.WorkoutSession
import com.lordj.fitnessapp.ui.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    padding: PaddingValues,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onStartWorkout: (Long) -> Unit,
    onNavigateToGarmin: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val app = LocalContext.current.applicationContext as FitnessApp
    val vm: WorkoutViewModel = viewModel(factory = WorkoutViewModel.Factory(app.workoutRepository))

    val workouts by vm.workouts.collectAsStateWithLifecycle()
    val recentSessions by vm.recentSessions.collectAsStateWithLifecycle()

    val todayWorkout = suggestTodayWorkout(workouts, recentSessions)
    val weekSessions = recentSessionsThisWeek(recentSessions)
    val totalVolume = weekSessions.sumOf { it.totalVolumeKg }
    val streak = calculateStreak(recentSessions)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(greeting(), style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                            Text("¡A entrenar!", style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Filled.Settings, "Ajustes",
                                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatChip("🔥 $streak días", "Racha")
                        StatChip("📅 ${weekSessions.size}", "Esta semana")
                        StatChip("⚖️ ${(totalVolume / 1000).roundToInt()}t", "Volumen")
                    }
                }
            }
        }

        item {
            GarminSyncCard(onClick = onNavigateToGarmin)
        }

        item {
            Text("Entrenamiento sugerido", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
        }

        if (todayWorkout != null) {
            item {
                TodayWorkoutCard(workout = todayWorkout, onClick = { onStartWorkout(todayWorkout.id) })
            }
        } else {
            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("No tienes rutinas configuradas", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onNavigateToWorkouts) { Text("Ver rutinas") }
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Sesiones recientes", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                TextButton(onClick = onNavigateToHistory) { Text("Ver todo") }
            }
        }

        if (recentSessions.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.FitnessCenter, null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            Spacer(Modifier.height(8.dp))
                            Text("¡Empieza tu primer entrenamiento!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        } else {
            items(recentSessions.take(5)) { session ->
                SessionCard(session = session)
            }
        }
    }
}

@Composable
private fun GarminSyncCard(onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Watch, null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f)) {
                Text("Sincronizar Garmin", style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
                Text("Importa tus actividades de Health Connect",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Icon(Icons.Filled.ChevronRight, null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}

@Composable
private fun StatChip(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
    }
}

@Composable
private fun TodayWorkoutCard(workout: Workout, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(workout.dayLabel.ifEmpty { "Hoy" }, style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary)
                Text(workout.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(workout.description, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Spacer(Modifier.height(4.dp))
                Text("⏱ ~${workout.estimatedMinutes} min", style = MaterialTheme.typography.labelMedium)
            }
            FloatingActionButton(
                onClick = onClick,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, "Empezar", tint = Color.White)
            }
        }
    }
}

@Composable
private fun SessionCard(session: WorkoutSession) {
    val fmt = SimpleDateFormat("dd MMM", Locale("es"))
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.FitnessCenter, null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(session.workoutName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(fmt.format(Date(session.startTime)), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${session.totalSets} series", style = MaterialTheme.typography.labelMedium)
                Text("${session.durationMinutes} min", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

private fun greeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Buenos días"
        in 12..17 -> "Buenas tardes"
        else -> "Buenas noches"
    }
}

private fun suggestTodayWorkout(workouts: List<Workout>, sessions: List<WorkoutSession>): Workout? {
    val userRoutines = workouts.filter { it.isUserRoutine }.sortedBy { it.orderIndex }
    if (userRoutines.isEmpty()) return workouts.firstOrNull()
    if (sessions.isEmpty()) return userRoutines.first()

    val lastRoutine = sessions.firstOrNull { s -> userRoutines.any { it.id == s.workoutId } }
    val lastIndex = userRoutines.indexOfFirst { it.id == lastRoutine?.workoutId }
    return userRoutines.getOrElse((lastIndex + 1) % userRoutines.size) { userRoutines.first() }
}

private fun recentSessionsThisWeek(sessions: List<WorkoutSession>): List<WorkoutSession> {
    val weekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L
    return sessions.filter { it.startTime >= weekAgo }
}

private fun calculateStreak(sessions: List<WorkoutSession>): Int {
    if (sessions.isEmpty()) return 0
    val cal = Calendar.getInstance()
    val today = cal.get(Calendar.DAY_OF_YEAR)
    val year = cal.get(Calendar.YEAR)

    val daySet = sessions.map { s ->
        cal.timeInMillis = s.startTime
        cal.get(Calendar.YEAR) * 1000 + cal.get(Calendar.DAY_OF_YEAR)
    }.toSet()

    var streak = 0
    var day = today
    var yr = year
    while (daySet.contains(yr * 1000 + day)) {
        streak++
        day--
        if (day <= 0) { yr--; day = 365 }
    }
    return streak
}
