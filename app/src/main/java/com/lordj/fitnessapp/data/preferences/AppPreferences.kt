package com.lordj.fitnessapp.data.preferences

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode { SYSTEM, LIGHT, DARK }

class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(getThemeModeFromPrefs())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private fun getThemeModeFromPrefs(): ThemeMode = when (prefs.getString("theme_mode", "SYSTEM")) {
        "LIGHT" -> ThemeMode.LIGHT
        "DARK" -> ThemeMode.DARK
        else -> ThemeMode.SYSTEM
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
    }
}
