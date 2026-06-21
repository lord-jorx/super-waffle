package com.lordj.fitnessapp.ui.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import com.lordj.fitnessapp.data.model.*
import com.lordj.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WorkoutViewModel(private val repo: WorkoutRepository) : ViewModel() {

    val workouts: StateFlow<List<Workout>> = repo.getAllWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSessions: StateFlow<List<WorkoutSession>> = repo.getRecentSessions(10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSessions: StateFlow<List<WorkoutSession>> = repo.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _workout = MutableStateFlow<Workout?>(null)
    val workout: StateFlow<Workout?> = _workout

    private val _workoutExercises = MutableStateFlow<List<WorkoutExercise>>(emptyList())
    val workoutExercises: StateFlow<List<WorkoutExercise>> = _workoutExercises

    fun loadWorkout(id: Long) {
        viewModelScope.launch { _workout.value = repo.getWorkoutById(id) }
        repo.getWorkoutExercises(id)
            .onEach { _workoutExercises.value = it }
            .launchIn(viewModelScope)
    }

    fun deleteSession(session: WorkoutSession) {
        viewModelScope.launch { repo.deleteSession(session) }
    }

    fun getSessionSets(sessionId: Long): Flow<List<SetLog>> = repo.getSetsBySession(sessionId)

    class Factory(private val repo: WorkoutRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = WorkoutViewModel(repo) as T
    }
}
