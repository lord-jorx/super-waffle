package com.lordj.fitnessapp.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.data.preferences.ThemeMode
import com.lordj.fitnessapp.ui.viewmodel.GarminSyncState
import com.lordj.fitnessapp.ui.viewmodel.GarminSyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToGarmin: () -> Unit,
    onNavigateToBodyWeight: () -> Unit = {}
) {
    val context = LocalContext.current
    val app = context.applicationContext as FitnessApp
    val lifecycleOwner = LocalLifecycleOwner.current
    val vm: GarminSyncViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val themeMode by app.preferences.themeMode.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) vm.checkStatus()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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
        settingsLauncher.launch(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null))
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ── Appearance ──────────────────────────────────────────────────
            SectionLabel("Aspecto")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Palette, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tema",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium)
                            Text("Apariencia de la aplicación",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            ThemeMode.SYSTEM to "Sistema",
                            ThemeMode.LIGHT to "Claro",
                            ThemeMode.DARK to "Oscuro"
                        ).forEach { (mode, label) ->
                            FilterChip(
                                selected = themeMode == mode,
                                onClick = { app.preferences.setThemeMode(mode) },
                                label = { Text(label) },
                                leadingIcon = if (themeMode == mode) {
                                    { Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Salud & Cuerpo ───────────────────────────────────────────────
            SectionLabel("Salud & Cuerpo")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingsItem(
                        icon = Icons.Filled.MonitorWeight,
                        title = "Peso Corporal",
                        subtitle = "Registra y visualiza tu evolución de peso",
                        onClick = onNavigateToBodyWeight
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Sincronización ───────────────────────────────────────────────
            SectionLabel("Sincronización")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingsItem(
                        icon = Icons.Filled.Watch,
                        title = "Garmin / Health Connect",
                        subtitle = when (state) {
                            is GarminSyncState.Ready -> "Conectado — ver actividades importadas"
                            is GarminSyncState.NeedsPermissions -> "Permisos pendientes de Health Connect"
                            is GarminSyncState.NeedsInstall -> "Health Connect no instalado"
                            is GarminSyncState.Unavailable -> "No disponible en este dispositivo"
                            else -> "Importa tus actividades de Garmin"
                        },
                        trailing = {
                            when (state) {
                                is GarminSyncState.Ready ->
                                    Icon(Icons.Filled.CheckCircle, null,
                                        tint = MaterialTheme.colorScheme.secondary)
                                is GarminSyncState.NeedsPermissions ->
                                    Icon(Icons.Filled.Warning, null,
                                        tint = MaterialTheme.colorScheme.tertiary)
                                else ->
                                    Icon(Icons.Filled.ChevronRight, null,
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f))
                            }
                        },
                        onClick = onNavigateToGarmin
                    )

                    if (state is GarminSyncState.NeedsPermissions) {
                        HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
                        Column(
                            modifier = Modifier.padding(start = 72.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Cómo activar en Samsung / Android 14+:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary)
                            listOf(
                                "Ve a Ajustes del móvil",
                                "Busca 'Health Connect'",
                                "Permisos de aplicación → FitnessTracker",
                                "Activa: Ejercicio, Frecuencia cardíaca, Calorías"
                            ).forEachIndexed { i, step ->
                                Text("${i + 1}. $step", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(Modifier.height(4.dp))
                            FilledTonalButton(
                                onClick = { openHealthConnectPermissions() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.Key, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Abrir permisos de Health Connect")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Aplicación ───────────────────────────────────────────────────
            SectionLabel("Aplicación")
            Card(modifier = Modifier.fillMaxWidth()) {
                SettingsItem(
                    icon = Icons.Filled.Info,
                    title = "Versión",
                    subtitle = "1.0.0 — FitnessTracker",
                    onClick = null
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)?,
    trailing: @Composable (() -> Unit)? = null
) {
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium)
                Text(subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            if (trailing != null) {
                trailing()
            } else if (onClick != null) {
                Icon(Icons.Filled.ChevronRight, null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f))
            }
        }
    }
}
