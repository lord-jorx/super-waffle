package com.lordj.fitnessapp.ui.screens.history

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
import com.lordj.fitnessapp.data.model.SetLog
import com.lordj.fitnessapp.data.model.WorkoutSession
import com.lordj.fitnessapp.ui.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    padding: PaddingValues,
    onExport: () -> Unit
) {
    val app = LocalContext.current.applicationContext as FitnessApp
    val vm: WorkoutViewModel = viewModel(factory = WorkoutViewModel.Factory(app.workoutRepository))
    val sessions by vm.allSessions.collectAsStateWithLifecycle()
    var expandedId by remember { mutableStateOf<Long?>(null) }
    var expandedSets by remember { mutableStateOf<List<SetLog>>(emptyList()) }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            TopAppBar(
                title = { Text("Historial", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onExport) {
                        Icon(Icons.Filled.FileDownload, "Exportar")
                    }
                }
            )
        }
    ) { inner ->
        if (sessions.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.History, null, modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    Spacer(Modifier.height(16.dp))
                    Text("Sin sesiones registradas", style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text("Completa un entrenamiento para verlo aquí",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(inner),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    // Summary stats
                    val totalVolume = sessions.sumOf { it.totalVolumeKg }
                    val totalSets = sessions.sumOf { it.totalSets }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SummaryCard("Sesiones", "${sessions.size}", Modifier.weight(1f))
                        SummaryCard("Series Total", "$totalSets", Modifier.weight(1f))
                        SummaryCard("Volumen Total", "${(totalVolume / 1000).toInt()}t", Modifier.weight(1f))
                    }
                }

                items(sessions, key = { it.id }) { session ->
                    val isExpanded = expandedId == session.id
                    SessionHistoryCard(
                        session = session,
                        isExpanded = isExpanded,
                        sets = if (isExpanded) expandedSets else emptyList(),
                        onToggle = {
                            if (isExpanded) {
                                expandedId = null
                            } else {
                                expandedId = session.id
                                scope.launch {
                                    vm.getSessionSets(session.id).collect { sets ->
                                        expandedSets = sets
                                    }
                                }
                            }
                        },
                        onDelete = { vm.deleteSession(session) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun SessionHistoryCard(
    session: WorkoutSession,
    isExpanded: Boolean,
    sets: List<SetLog>,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val fmt = SimpleDateFormat("EEEE dd MMM yyyy · HH:mm", Locale("es"))
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar sesión?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(session.workoutName, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold)
                    Text(fmt.format(Date(session.startTime)), style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${session.durationMinutes} min", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary)
                    Text("${session.totalSets} series", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }

            if (session.totalVolumeKg > 0) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ChipInfo("⚖️ ${session.totalVolumeKg.toInt()} kg volumen")
                    ChipInfo("🔢 ${session.totalSets} series")
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(4.dp))
                    Text("Eliminar", color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium)
                }
                TextButton(onClick = onToggle) {
                    Text(if (isExpanded) "Ocultar detalles" else "Ver detalles",
                        style = MaterialTheme.typography.labelMedium)
                    Icon(if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, null,
                        modifier = Modifier.size(16.dp))
                }
            }

            if (isExpanded && sets.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                val byExercise = sets.groupBy { it.exerciseName }
                byExercise.forEach { (name, exSets) ->
                    Text(name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary)
                    exSets.forEach { set ->
                        Row(modifier = Modifier.padding(start = 8.dp, top = 2.dp)) {
                            Text("Serie ${set.setNumber}", Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall)
                            Text("${set.reps} reps", Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall)
                            Text("${set.weightKg} kg", Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ChipInfo(text: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall)
    }
}
