package com.lordj.fitnessapp.ui.theme

import androidx.compose.ui.graphics.Color

val FitnessOrange = Color(0xFFFF6D00)
val FitnessOrangeDark = Color(0xFFE65100)
val FitnessGreen = Color(0xFF43A047)
val FitnessBlue = Color(0xFF1E88E5)
val FitnessPurple = Color(0xFF7B1FA2)
val FitnessTeal = Color(0xFF00897B)
val FitnessRed = Color(0xFFE53935)

val CategoryChest = Color(0xFFE53935)
val CategoryBack = Color(0xFF1E88E5)
val CategoryShoulders = Color(0xFFFF6D00)
val CategoryArms = Color(0xFF7B1FA2)
val CategoryLegs = Color(0xFF43A047)
val CategoryCore = Color(0xFF00897B)
val CategoryCardio = Color(0xFFFF5722)

fun categoryColor(category: String): Color = when (category) {
    "Pecho" -> CategoryChest
    "Espalda" -> CategoryBack
    "Hombros" -> CategoryShoulders
    "Brazos" -> CategoryArms
    "Piernas" -> CategoryLegs
    "Core" -> CategoryCore
    "Cardio" -> CategoryCardio
    else -> Color(0xFF9E9E9E)
}

fun categoryIcon(category: String): String = when (category) {
    "Pecho" -> "💪"
    "Espalda" -> "🔙"
    "Hombros" -> "🏋️"
    "Brazos" -> "💪"
    "Piernas" -> "🦵"
    "Core" -> "🎯"
    "Cardio" -> "❤️"
    else -> "🏃"
}
