package com.lordj.fitnessapp.ui.screens.body

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.data.model.BodyWeight
import com.lordj.fitnessapp.ui.viewmodel.BodyWeightViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyWeightScreen(onBack: () -> Unit) {
    val app = LocalContext.current.applicationContext as FitnessApp
    val vm: BodyWeightViewModel = viewModel(
        factory = BodyWeightViewModel.Factory(app.bodyWeightRepository)
    )
    val entries by vm.entries.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        LogWeightDialog(
            onDismiss = { showDialog = false },
            onConfirm = { kg, notes ->
                vm.log(kg, notes)
                showDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Peso Corporal") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, "Registrar peso")
            }
        }
    ) { padding ->
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Filled.MonitorWeight,
                        null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    Text(
                        "Sin registros de peso",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        "Toca + para registrar tu peso de hoy",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    val chartEntries = entries.sortedBy { it.timestamp }
                    WeightLineChart(
                        entries = chartEntries,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    val latest = entries.first()
                    val oldest = entries.last()
                    val diff = latest.weightKg - oldest.weightKg
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        WeightStatCard(
                            label = "Actual",
                            value = "${"%.1f".format(latest.weightKg)} kg",
                            modifier = Modifier.weight(1f)
                        )
                        WeightStatCard(
                            label = "Cambio total",
                            value = "${if (diff >= 0) "+" else ""}${"%.1f".format(diff)} kg",
                            modifier = Modifier.weight(1f)
                        )
                        WeightStatCard(
                            label = "Registros",
                            value = "${entries.size}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        "Historial",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(entries, key = { it.id }) { entry ->
                    WeightEntryCard(
                        entry = entry,
                        onDelete = { vm.delete(entry) }
                    )
                }

                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }
}

@Composable
private fun WeightLineChart(
    entries: List<BodyWeight>,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    if (entries.size < 2) return
    val minW = entries.minOf { it.weightKg }.toFloat() - 1f
    val maxW = entries.maxOf { it.weightKg }.toFloat() + 1f
    val range = (maxW - minW).coerceAtLeast(1f)

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Evolución del peso",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                val w = size.width
                val h = size.height
                val pts = entries.mapIndexed { i, e ->
                    val x = if (entries.size == 1) w / 2f else i * w / (entries.size - 1).toFloat()
                    val y = h - ((e.weightKg.toFloat() - minW) / range) * h * 0.85f - h * 0.05f
                    Offset(x, y)
                }

                // Fill under curve
                val fillPath = Path().apply {
                    moveTo(pts.first().x, h)
                    pts.forEach { lineTo(it.x, it.y) }
                    lineTo(pts.last().x, h)
                    close()
                }
                drawPath(fillPath, color.copy(alpha = 0.12f))

                // Line
                for (i in 1 until pts.size) {
                    drawLine(color, pts[i - 1], pts[i], strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                }

                // Dots
                pts.forEach { drawCircle(color, 5.dp.toPx(), it) }
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val fmt = SimpleDateFormat("dd/MM", Locale.getDefault())
                Text(
                    fmt.format(Date(entries.first().timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    fmt.format(Date(entries.last().timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun WeightStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun WeightEntryCard(entry: BodyWeight, onDelete: () -> Unit) {
    val fmt = SimpleDateFormat("EEE dd MMM yyyy, HH:mm", Locale("es"))
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${"%.1f".format(entry.weightKg)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${"%.1f".format(entry.weightKg)} kg",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    fmt.format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                if (entry.notes.isNotBlank()) {
                    Text(
                        entry.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Filled.Delete,
                    null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
private fun LogWeightDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var weightText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar peso") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it; error = false },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = error,
                    supportingText = if (error) {{ Text("Introduce un peso válido") }} else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val kg = weightText.replace(",", ".").toDoubleOrNull()
                if (kg != null && kg > 0) {
                    onConfirm(kg, notes.trim())
                } else {
                    error = true
                }
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
