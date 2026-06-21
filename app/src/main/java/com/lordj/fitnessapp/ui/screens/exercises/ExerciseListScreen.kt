package com.lordj.fitnessapp.ui.screens.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.data.model.Exercise
import com.lordj.fitnessapp.ui.theme.categoryColor
import com.lordj.fitnessapp.ui.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    padding: PaddingValues,
    onExerciseClick: (Long) -> Unit
) {
    val app = LocalContext.current.applicationContext as FitnessApp
    val vm: ExerciseViewModel = viewModel(factory = ExerciseViewModel.Factory(app.exerciseRepository))

    val exercises by vm.exercises.collectAsStateWithLifecycle()
    val categories by vm.categories.collectAsStateWithLifecycle()
    val query by vm.query.collectAsStateWithLifecycle()
    val category by vm.category.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            TopAppBar(title = { Text("Ejercicios", fontWeight = FontWeight.Bold) })
        }
    ) { inner ->
        Column(modifier = Modifier.fillMaxSize().padding(inner)) {
            // Search bar
            OutlinedTextField(
                value = query,
                onValueChange = { vm.setQuery(it) },
                placeholder = { Text("Buscar ejercicio...") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                trailingIcon = {
                    if (query.isNotEmpty()) IconButton(onClick = { vm.setQuery("") }) {
                        Icon(Icons.Filled.Clear, null)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(50)
            )

            // Category chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = cat == category,
                        onClick = { vm.setCategory(cat) },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (cat == "Todos")
                                MaterialTheme.colorScheme.primary
                            else categoryColor(cat).copy(alpha = 0.8f),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "${exercises.size} ejercicios",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(exercises, key = { it.id }) { exercise ->
                    ExerciseCard(exercise = exercise, onClick = { onExerciseClick(exercise.id) })
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    val catColor = categoryColor(exercise.category)
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Category color circle
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                    .background(catColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(categoryEmoji(exercise.category), style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(exercise.name, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold)
                    if (exercise.isFromUserRoutine) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier.clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Tu rutina", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                Text(exercise.primaryMuscle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EquipmentBadge(exercise.equipment)
                }
            }
            Icon(Icons.Filled.ChevronRight, null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun EquipmentBadge(equipment: String) {
    val (icon, color) = when (equipment) {
        "Barra" -> Icons.Filled.FitnessCenter to Color(0xFF1E88E5)
        "Mancuerna" -> Icons.Filled.FitnessCenter to Color(0xFF7B1FA2)
        "Cable", "Polea" -> Icons.Filled.Cable to Color(0xFF00897B)
        "Máquina", "Smith Machine" -> Icons.Filled.Settings to Color(0xFFE53935)
        "Peso Corporal" -> Icons.Filled.Person to Color(0xFF43A047)
        "Cardio" -> Icons.Filled.DirectionsRun to Color(0xFFFF5722)
        else -> Icons.Filled.FitnessCenter to Color(0xFF9E9E9E)
    }
    Icon(icon, null, modifier = Modifier.size(12.dp), tint = color.copy(alpha = 0.7f))
    Spacer(Modifier.width(4.dp))
    Text(equipment, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.8f))
}

private fun categoryEmoji(cat: String) = when (cat) {
    "Pecho" -> "💪"
    "Espalda" -> "🔷"
    "Hombros" -> "🏋️"
    "Brazos" -> "💪"
    "Piernas" -> "🦵"
    "Core" -> "🎯"
    "Cardio" -> "❤️"
    else -> "🏃"
}
