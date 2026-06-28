package com.lordj.fitnessapp.ui.screens.programs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lordj.fitnessapp.data.programs.Difficulty
import com.lordj.fitnessapp.data.programs.ProgramLibrary
import com.lordj.fitnessapp.data.programs.ProgramTemplate

@Composable
fun ProgramsScreen(onProgramClick: (String) -> Unit) {
    var selectedBodyPart by remember { mutableStateOf("Todos") }
    var selectedGoal by remember { mutableStateOf("Todos") }

    val filtered = remember(selectedBodyPart, selectedGoal) {
        ProgramLibrary.all.filter { p ->
            (selectedBodyPart == "Todos" || p.bodyPart == selectedBodyPart) &&
            (selectedGoal == "Todos" || p.goal == selectedGoal)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Body part filter
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ProgramLibrary.bodyParts) { part ->
                FilterChip(
                    selected = part == selectedBodyPart,
                    onClick = { selectedBodyPart = part },
                    label = { Text(part) }
                )
            }
        }

        // Goal filter
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ProgramLibrary.goals) { goal ->
                FilterChip(
                    selected = goal == selectedGoal,
                    onClick = { selectedGoal = goal },
                    label = { Text(goal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            "${filtered.size} programa${if (filtered.size != 1) "s" else ""}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(4.dp))

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay programas para este filtro",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { program ->
                    ProgramCard(program = program, onClick = { onProgramClick(program.id) })
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun ProgramCard(program: ProgramTemplate, onClick: () -> Unit) {
    val color = parseProgramColor(program.colorHex)
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Color accent bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(color)
            )
            Column(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(program.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold)
                        Text(program.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 2)
                    }
                    Spacer(Modifier.width(8.dp))
                    DifficultyBadge(program.difficulty)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BodyPartChip(program.bodyPart, color)
                    GoalChip(program.goal)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoItem(Icons.Filled.Timer, "${program.estimatedMinutes} min")
                    InfoItem(Icons.Filled.DateRange, program.frequency)
                    InfoItem(Icons.Filled.List, "${program.exercises.size} ejercicios")
                }
            }
            Icon(
                Icons.Filled.ChevronRight, null,
                modifier = Modifier.align(Alignment.CenterVertically).padding(end = 12.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: Difficulty) {
    val (bg, fg) = when (difficulty) {
        Difficulty.BEGINNER -> Color(0xFF14B8A6).copy(alpha = 0.15f) to Color(0xFF14B8A6)
        Difficulty.INTERMEDIATE -> Color(0xFFF59E0B).copy(alpha = 0.15f) to Color(0xFFF59E0B)
        Difficulty.ADVANCED -> Color(0xFFEF4444).copy(alpha = 0.15f) to Color(0xFFEF4444)
    }
    Box(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(bg).padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(difficulty.label, style = MaterialTheme.typography.labelSmall,
            color = fg, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BodyPartChip(bodyPart: String, color: Color) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(bodyPart, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun GoalChip(goal: String) {
    val color = if (goal == "Fuerza") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    Box(
        modifier = Modifier.clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(goal, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
private fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Icon(icon, null, modifier = Modifier.size(12.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        Text(text, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
}

private fun parseProgramColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) {
    Color(0xFF6366F1)
}
