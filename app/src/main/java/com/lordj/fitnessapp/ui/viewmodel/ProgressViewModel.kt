package com.lordj.fitnessapp.ui.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import com.lordj.fitnessapp.data.model.Exercise
import com.lordj.fitnessapp.data.model.SetLog
import com.lordj.fitnessapp.data.model.WorkoutSession
import com.lordj.fitnessapp.data.repository.ExerciseRepository
import com.lordj.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class DayProgress(
    val dateLabel: String,
    val maxWeightKg: Double,
    val totalVolume: Double,
    val totalReps: Int,
    val e1RM: Double
)

data class ExercisePR(
    val exercise: Exercise,
    val maxWeightKg: Double,
    val bestE1RM: Double,
    val totalSessions: Int
)

class ProgressViewModel(
    private val workoutRepo: WorkoutRepository,
    private val exerciseRepo: ExerciseRepository
) : ViewModel() {

    val allSessions: StateFlow<List<WorkoutSession>> = workoutRepo.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exercises: StateFlow<List<Exercise>> = exerciseRepo.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedExercise = MutableStateFlow<Exercise?>(null)
    val selectedExercise: StateFlow<Exercise?> = _selectedExercise

    private val _progress = MutableStateFlow<List<DayProgress>>(emptyList())
    val progress: StateFlow<List<DayProgress>> = _progress

    private val _prs = MutableStateFlow<List<ExercisePR>>(emptyList())
    val prs: StateFlow<List<ExercisePR>> = _prs

    private val fmt = DateTimeFormatter.ofPattern("dd/MM")

    fun loadExerciseProgress(exerciseId: Long) {
        viewModelScope.launch { _selectedExercise.value = exerciseRepo.getExerciseById(exerciseId) }
        exerciseRepo.getSetsByExercise(exerciseId)
            .map { sets -> groupByDay(sets) }
            .onEach { _progress.value = it }
            .launchIn(viewModelScope)
    }

    private fun groupByDay(sets: List<SetLog>): List<DayProgress> {
        return sets.filter { !it.isWarmup }
            .groupBy {
                Instant.ofEpochMilli(it.completedAt).atZone(ZoneId.systemDefault()).toLocalDate()
            }
            .entries
            .sortedBy { it.key }
            .map { (date, daySets) ->
                DayProgress(
                    dateLabel = date.format(fmt),
                    maxWeightKg = daySets.maxOf { it.weightKg },
                    totalVolume = daySets.sumOf { it.volume },
                    totalReps = daySets.sumOf { it.reps },
                    e1RM = daySets.maxOf { it.e1RM }
                )
            }
    }

    fun loadPersonalRecords() {
        viewModelScope.launch {
            val exList = exerciseRepo.getAllExercises().first()
            val result = mutableListOf<ExercisePR>()
            exList.forEach { exercise ->
                val sets = exerciseRepo.getRecentSetsForExercise(exercise.id, 500).filter { !it.isWarmup }
                if (sets.isEmpty()) return@forEach
                val sessions = sets.map { it.sessionId }.distinct().size
                result.add(
                    ExercisePR(
                        exercise = exercise,
                        maxWeightKg = sets.maxOf { it.weightKg },
                        bestE1RM = sets.maxOf { it.e1RM },
                        totalSessions = sessions
                    )
                )
            }
            _prs.value = result.sortedByDescending { it.maxWeightKg }
        }
    }

    class Factory(
        private val workoutRepo: WorkoutRepository,
        private val exerciseRepo: ExerciseRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ProgressViewModel(workoutRepo, exerciseRepo) as T
    }
}
