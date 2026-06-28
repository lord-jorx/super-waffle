package com.lordj.fitnessapp.ui.theme

import androidx.compose.ui.graphics.Color

val Indigo500 = Color(0xFF6366F1)
val Indigo400 = Color(0xFF818CF8)
val Indigo700 = Color(0xFF4338CA)
val Emerald500 = Color(0xFF10B981)
val Emerald300 = Color(0xFF6EE7B7)
val Amber500 = Color(0xFFF59E0B)
val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Slate100 = Color(0xFFF1F5F9)
val Rose500 = Color(0xFFF43F5E)

val CategoryChest = Color(0xFFEF4444)
val CategoryBack = Color(0xFF3B82F6)
val CategoryShoulders = Color(0xFFF97316)
val CategoryArms = Color(0xFFA855F7)
val CategoryLegs = Color(0xFF22C55E)
val CategoryCore = Color(0xFF14B8A6)
val CategoryCardio = Color(0xFFF43F5E)

fun categoryColor(category: String): androidx.compose.ui.graphics.Color = when (category) {
    "Pecho" -> CategoryChest
    "Espalda" -> CategoryBack
    "Hombros" -> CategoryShoulders
    "Brazos" -> CategoryArms
    "Piernas" -> CategoryLegs
    "Core" -> CategoryCore
    "Cardio" -> CategoryCardio
    else -> Color(0xFF94A3B8)
}

fun categoryIcon(category: String): String = when (category) {
    "Pecho" -> "💪"
    "Espalda" -> "🔷"
    "Hombros" -> "🏋️"
    "Brazos" -> "💪"
    "Piernas" -> "🦵"
    "Core" -> "🎯"
    "Cardio" -> "❤️"
    else -> "🏃"
}
