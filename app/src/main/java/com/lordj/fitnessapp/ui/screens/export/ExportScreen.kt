package com.lordj.fitnessapp.ui.screens.export

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.ui.viewmodel.WorkoutViewModel
import com.lordj.fitnessapp.util.ExcelExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as FitnessApp
    val vm: WorkoutViewModel = viewModel(factory = WorkoutViewModel.Factory(app.workoutRepository))
    val sessions by vm.allSessions.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var isExporting by remember { mutableStateOf(false) }
    var exportDone by remember { mutableStateOf(false) }
    var exportedFile by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exportar Datos", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Info card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.TableChart, null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Exportar a Excel (.xlsx)", style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("${sessions.size} sesiones disponibles",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }

            item {
                Text("El archivo Excel incluirá:", style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
            }

            item {
                SheetInfoCard(
                    icon = Icons.Filled.CalendarMonth,
                    title = "Hoja 1: Resumen de Sesiones",
                    description = "Fecha, rutina, duración, series totales y volumen por sesión"
                )
            }

            item {
                SheetInfoCard(
                    icon = Icons.Filled.TableRows,
                    title = "Hoja 2: Detalle de Series",
                    description = "Cada serie registrada con fecha, ejercicio, reps, peso, volumen y e1RM estimado"
                )
            }

            item {
                SheetInfoCard(
                    icon = Icons.Filled.EmojiEvents,
                    title = "Hoja 3: Records Personales",
                    description = "Peso máximo alcanzado y e1RM estimado por ejercicio"
                )
            }

            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Info, null, tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Consejo", style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Abre el archivo en Excel o Google Sheets y usa las herramientas de gráficos " +
                            "para crear visualizaciones personalizadas. Los datos ya están formateados " +
                            "para análisis inmediato.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item {
                if (exportDone) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CheckCircle, null,
                                    tint = MaterialTheme.colorScheme.secondary)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("¡Exportado correctamente!", style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer)
                                    Text(exportedFile, style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                                }
                            }
                        }
                        Button(
                            onClick = {
                                scope.launch {
                                    val allSets = app.workoutRepository.getAllSets().first()
                                    withContext(Dispatchers.IO) {
                                        val file = ExcelExporter.export(context, sessions, allSets)
                                        withContext(Dispatchers.Main) {
                                            ExcelExporter.shareFile(context, file)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Share, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Compartir archivo")
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            isExporting = true
                            scope.launch {
                                try {
                                    val allSets = app.workoutRepository.getAllSets().first()
                                    withContext(Dispatchers.IO) {
                                        val file = ExcelExporter.export(context, sessions, allSets)
                                        withContext(Dispatchers.Main) {
                                            isExporting = false
                                            exportDone = true
                                            exportedFile = file.name
                                            ExcelExporter.shareFile(context, file)
                                        }
                                    }
                                } catch (e: Exception) {
                                    isExporting = false
                                }
                            }
                        },
                        enabled = !isExporting && sessions.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(Modifier.width(8.dp))
                            Text("Generando...")
                        } else {
                            Icon(Icons.Filled.FileDownload, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Exportar a Excel", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (sessions.isEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("Necesitas tener al menos una sesión completada",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetInfoCard(icon: ImageVector, title: String, description: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp).padding(top = 2.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        }
    }
}
