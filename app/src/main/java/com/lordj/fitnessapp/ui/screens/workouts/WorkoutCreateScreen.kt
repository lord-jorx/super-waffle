package com.lordj.fitnessapp.ui.screens.workouts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.data.model.Exercise
import com.lordj.fitnessapp.ui.theme.categoryColor
import com.lordj.fitnessapp.ui.viewmodel.ExerciseViewModel
import com.lordj.fitnessapp.ui.viewmodel.WorkoutCreateViewModel
import com.lordj.fitnessapp.ui.viewmodel.WorkoutExerciseDraft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutCreateScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val app = LocalContext.current.applicationContext as FitnessApp
    val vm: WorkoutCreateViewModel = viewModel(
        factory = WorkoutCreateViewModel.Factory(app.workoutRepository)
    )
    val exVm: ExerciseViewModel = viewModel(
        factory = ExerciseViewModel.Factory(app.exerciseRepository)
    )

    val name by vm.name.collectAsStateWithLifecycle()
    val description by vm.description.collectAsStateWithLifecycle()
    val dayLabel by vm.dayLabel.collectAsStateWithLifecycle()
    val draftExercises by vm.exercises.collectAsStateWithLifecycle()
    val saved by vm.saved.collectAsStateWithLifecycle()

    val allExercises by exVm.exercises.collectAsStateWithLifecycle()

    var showExercisePicker by remember { mutableStateOf(false) }
    var expandedDraftId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(saved) {
        if (saved) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Rutina") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Volver") }
                },
                actions = {
                    TextButton(
                        onClick = { vm.saveWorkout() },
                        enabled = name.isNotBlank() && draftExercises.isNotEmpty()
                    ) {
                        Text("Guardar", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showExercisePicker = true }) {
                Icon(Icons.Filled.Add, "Añadir ejercicio")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { vm.setName(it) },
                label = { Text("Nombre de la rutina *") },
                placeholder = { Text("Ej. Push A, Piernas pesadas...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Edit, null) }
            )

            OutlinedTextField(
                value = dayLabel,
                onValueChange = { vm.setDayLabel(it) },
                label = { Text("Etiqueta de día") },
                placeholder = { Text("Ej. Lunes · Push") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.CalendarToday, null) }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { vm.setDescription(it) },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            if (draftExercises.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.FitnessCenter, null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                        Text("Añade ejercicios con el botón +",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            } else {
                Text("Ejercicios (${draftExercises.size})",
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                draftExercises.forEachIndexed { idx, draft ->
                    DraftExerciseCard(
                        draft = draft,
                        index = idx + 1,
                        expanded = expandedDraftId == draft.exercise.id,
                        onToggleExpand = {
                            expandedDraftId = if (expandedDraftId == draft.exercise.id) null else draft.exercise.id
                        },
                        onRemove = { vm.removeExercise(draft.exercise.id) },
                        onUpdate = { sets, reps, rest -> vm.updateDraft(draft.exercise.id, sets, reps, rest) }
                    )
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }

    if (showExercisePicker) {
        ExercisePickerSheet(
            exercises = allExercises,
            alreadyAdded = draftExercises.map { it.exercise.id }.toSet(),
            onPick = { ex -> vm.addExercise(ex); showExercisePicker = false },
            onDismiss = { showExercisePicker = false }
        )
    }
}

@Composable
private fun DraftExerciseCard(
    draft: WorkoutExerciseDraft,
    index: Int,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onRemove: () -> Unit,
    onUpdate: (Int?, Int?, Int?) -> Unit
) {
    val catColor = categoryColor(draft.exercise.category)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onToggleExpand).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$index",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(24.dp))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(draft.exercise.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold)
                    Text("${draft.targetSets} × ${draft.targetReps} · ${draft.restSeconds}s descanso",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.DeleteOutline, "Eliminar",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp))
                }
                Icon(
                    if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }

            if (expanded) {
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumberField(
                        label = "Series",
                        value = draft.targetSets,
                        modifier = Modifier.weight(1f),
                        onValue = { onUpdate(it, null, null) }
                    )
                    NumberField(
                        label = "Reps",
                        value = draft.targetReps,
                        modifier = Modifier.weight(1f),
                        onValue = { onUpdate(null, it, null) }
                    )
                    NumberField(
                        label = "Descanso (s)",
                        value = draft.restSeconds,
                        modifier = Modifier.weight(1f),
                        onValue = { onUpdate(null, null, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberField(label: String, value: Int, modifier: Modifier = Modifier, onValue: (Int) -> Unit) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { s -> s.toIntOrNull()?.let { if (it > 0) onValue(it) } },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExercisePickerSheet(
    exercises: List<Exercise>,
    alreadyAdded: Set<Long>,
    onPick: (Exercise) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, exercises) {
        if (query.isBlank()) exercises
        else exercises.filter { it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text("Añadir ejercicio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Buscar...") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(50)
            )
            Spacer(Modifier.height(8.dp))
        }
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(filtered, key = { it.id }) { ex ->
                val added = ex.id in alreadyAdded
                ListItem(
                    headlineContent = { Text(ex.name, style = MaterialTheme.typography.bodyMedium) },
                    supportingContent = { Text("${ex.category} · ${ex.primaryMuscle}",
                        style = MaterialTheme.typography.bodySmall) },
                    trailingContent = {
                        if (added) {
                            Icon(Icons.Filled.Check, null,
                                tint = MaterialTheme.colorScheme.secondary)
                        } else {
                            Icon(Icons.Filled.Add, "Añadir",
                                tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    modifier = Modifier.clickable(enabled = !added) { onPick(ex) }
                )
            }
            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}
