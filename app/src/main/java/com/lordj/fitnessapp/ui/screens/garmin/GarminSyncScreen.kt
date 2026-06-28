package com.lordj.fitnessapp.ui.screens.garmin

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.data.health.GarminSession
import com.lordj.fitnessapp.data.health.HealthConnectManager
import com.lordj.fitnessapp.ui.viewmodel.GarminSyncState
import com.lordj.fitnessapp.ui.viewmodel.GarminSyncViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GarminSyncScreen(
    padding: PaddingValues,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val vm: GarminSyncViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val importMessage by vm.importMessage.collectAsStateWithLifecycle()

    // Re-check permissions every time the screen resumes (e.g. returning from Health Connect settings)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) vm.checkStatus()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Launcher for the Health Connect permission management screen
    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { vm.checkStatus() }

    fun openHealthConnectPermissions() {
        val intents = listOf(
            Intent("android.health.connect.action.MANAGE_HEALTH_PERMISSIONS").apply {
                putExtra(Intent.EXTRA_PACKAGE_NAME, context.packageName)
            },
            Intent("android.health.connect.action.HEALTH_CONNECT_SETTINGS"),
        )
        for (intent in intents) {
            try { settingsLauncher.launch(intent); return } catch (_: Exception) { }
        }
        // Fallback garantizado: ajustes de la app (siempre funciona en Android)
        settingsLauncher.launch(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null))
        )
    }

    fun openAppSettings() {
        settingsLauncher.launch(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null))
        )
    }

    LaunchedEffect(importMessage) {
        if (importMessage != null) {
            kotlinx.coroutines.delay(3000)
            vm.clearImportMessage()
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Sincronizar Garmin") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Atrás")
                    }
                },
                actions = {
                    if (state is GarminSyncState.Ready) {
                        IconButton(onClick = { vm.checkStatus() }) {
                            Icon(Icons.Filled.Refresh, "Actualizar")
                        }
                    }
                }
            )
        },
        snackbarHost = {
            importMessage?.let { msg ->
                Snackbar(modifier = Modifier.padding(16.dp)) { Text(msg) }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(padding)
        ) {
            when (val s = state) {
                is GarminSyncState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is GarminSyncState.Unavailable -> {
                    InfoCard(
                        icon = Icons.Filled.SyncDisabled,
                        title = "Health Connect no disponible",
                        body = "Health Connect no está soportado en este dispositivo (requiere Android 9+).",
                        modifier = Modifier.align(Alignment.Center).padding(24.dp)
                    )
                }

                is GarminSyncState.NeedsInstall -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoCard(
                            icon = Icons.Filled.HealthAndSafety,
                            title = "Instala Health Connect",
                            body = "Garmin Connect sincroniza tus actividades a Google Health Connect. " +
                                    "Instálalo para continuar.",
                        )
                        Button(
                            onClick = { context.startActivity(vm.getInstallIntent()) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.OpenInNew, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Instalar Health Connect")
                        }
                    }
                }

                is GarminSyncState.NeedsPermissions -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoCard(
                            icon = Icons.Filled.Lock,
                            title = "Permisos necesarios",
                            body = "Necesitamos acceso a tus sesiones de ejercicio, frecuencia cardíaca " +
                                    "y calorías en Health Connect para leer tus entrenamientos de Garmin.",
                        )
                        Button(
                            onClick = { openHealthConnectPermissions() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Key, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Conceder permisos")
                        }

                        HorizontalDivider()

                        Text(
                            "¿No funciona el botón? Hazlo manualmente:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("En Samsung / Android 14+:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary)
                                listOf(
                                    "Ajustes → busca 'Health Connect'",
                                    "Permisos de aplicación",
                                    "Selecciona FitnessTracker",
                                    "Activa: Ejercicio, Frecuencia cardíaca, Calorías"
                                ).forEachIndexed { i, step ->
                                    Text("${i + 1}. $step",
                                        style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        OutlinedButton(
                            onClick = { openAppSettings() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Settings, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Abrir ajustes de FitnessTracker")
                        }
                    }
                }

                is GarminSyncState.Ready -> {
                    if (s.sessions.isEmpty()) {
                        InfoCard(
                            icon = Icons.Filled.Watch,
                            title = "Sin actividades",
                            body = "No se encontraron actividades en Health Connect de los últimos 60 días. " +
                                    "Asegúrate de que Garmin Connect esté sincronizando con Health Connect.",
                            modifier = Modifier.align(Alignment.Center).padding(24.dp)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    "${s.sessions.size} actividad${if (s.sessions.size != 1) "es" else ""} de los últimos 60 días",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            items(s.sessions, key = { it.id }) { session ->
                                GarminSessionCard(
                                    session = session,
                                    isImported = session.id in s.importedIds,
                                    onImport = { vm.importSession(session) }
                                )
                            }
                        }
                    }
                }

                is GarminSyncState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoCard(
                            icon = Icons.Filled.ErrorOutline,
                            title = "Error al cargar",
                            body = s.message
                        )
                        OutlinedButton(onClick = { vm.checkStatus() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GarminSessionCard(
    session: GarminSession,
    isImported: Boolean,
    onImport: () -> Unit
) {
    val dateFmt = SimpleDateFormat("dd MMM, HH:mm", Locale("es"))
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(session.exerciseTypeEmoji, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(session.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    dateFmt.format(Date(session.startTime)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatBadge("⏱ ${session.durationMinutes} min")
                    if (session.calories > 0) StatBadge("🔥 ${session.calories.toInt()} kcal")
                    session.avgHeartRateBpm?.let { StatBadge("❤️ $it bpm") }
                }
                Text(
                    session.sourceApp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
            Spacer(Modifier.width(8.dp))
            if (isImported) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Importado",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                FilledTonalIconButton(onClick = onImport, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.Add, "Importar", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun StatBadge(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
    }
}
