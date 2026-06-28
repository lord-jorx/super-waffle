package com.lordj.fitnessapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lordj.fitnessapp.FitnessApp
import com.lordj.fitnessapp.data.health.GarminSession
import com.lordj.fitnessapp.data.health.HealthConnectManager
import com.lordj.fitnessapp.data.model.WorkoutSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class GarminSyncState {
    object Loading : GarminSyncState()
    object Unavailable : GarminSyncState()
    object NeedsInstall : GarminSyncState()
    object NeedsPermissions : GarminSyncState()
    data class Ready(
        val sessions: List<GarminSession>,
        val importedIds: Set<String> = emptySet()
    ) : GarminSyncState()
    data class Error(val message: String) : GarminSyncState()
}

class GarminSyncViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as FitnessApp
    private val manager: HealthConnectManager = app.healthConnectManager

    private val _state = MutableStateFlow<GarminSyncState>(GarminSyncState.Loading)
    val state: StateFlow<GarminSyncState> = _state

    private val _importMessage = MutableStateFlow<String?>(null)
    val importMessage: StateFlow<String?> = _importMessage

    init {
        checkStatus()
    }

    fun checkStatus() {
        viewModelScope.launch {
            _state.value = GarminSyncState.Loading
            when {
                !manager.isAvailable() && manager.needsInstall() -> _state.value = GarminSyncState.NeedsInstall
                !manager.isAvailable() -> _state.value = GarminSyncState.Unavailable
                !manager.hasPermissions() -> _state.value = GarminSyncState.NeedsPermissions
                else -> loadSessions()
            }
        }
    }

    fun onPermissionsResult(granted: Boolean) {
        if (granted) {
            viewModelScope.launch { loadSessions() }
        } else {
            _state.value = GarminSyncState.NeedsPermissions
        }
    }

    fun importSession(session: GarminSession) {
        viewModelScope.launch {
            try {
                val workoutSession = WorkoutSession(
                    workoutName = "${session.exerciseTypeEmoji} ${session.title}",
                    startTime = session.startTime,
                    endTime = session.endTime,
                    notes = buildString {
                        append("Importado desde ${session.sourceApp}")
                        session.avgHeartRateBpm?.let { append(" • FC media: ${it} bpm") }
                        session.maxHeartRateBpm?.let { append(" • FC máx: ${it} bpm") }
                        if (session.calories > 0) append(" • ${session.calories.toInt()} kcal")
                    },
                    totalVolumeKg = 0.0,
                    totalSets = 0
                )
                app.workoutRepository.startSession(workoutSession)

                val current = _state.value
                if (current is GarminSyncState.Ready) {
                    _state.value = current.copy(importedIds = current.importedIds + session.id)
                }
                _importMessage.value = "${session.exerciseTypeEmoji} ${session.title} importado"
            } catch (e: Exception) {
                _importMessage.value = "Error al importar: ${e.message}"
            }
        }
    }

    fun clearImportMessage() {
        _importMessage.value = null
    }

    private suspend fun loadSessions() {
        try {
            val sessions = manager.getRecentSessions(days = 60)
            _state.value = GarminSyncState.Ready(sessions)
        } catch (e: Exception) {
            _state.value = GarminSyncState.Error(e.message ?: "Error desconocido")
        }
    }

    fun getInstallIntent() = manager.getInstallIntent()
}
