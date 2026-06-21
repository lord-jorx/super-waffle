package com.lordj.fitnessapp.ui.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import com.lordj.fitnessapp.data.model.*
import com.lordj.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class ExerciseState(
    val workoutExercise: WorkoutExercise,
    val exercise: Exercise,
    val loggedSets: List<SetLog> = emptyList(),
    val lastSets: List<SetLog> = emptyList(),
    val suggestedWeight: Double = 0.0
)

sealed class WorkoutPhase {
    object NotStarted : WorkoutPhase()
    data class InProgress(val exerciseIndex: Int) : WorkoutPhase()
    data class Resting(val secondsLeft: Int, val exerciseIndex: Int) : WorkoutPhase()
    object Finished : WorkoutPhase()
}

class ActiveWorkoutViewModel(private val repo: WorkoutRepository) : ViewModel() {

    private val _phase = MutableStateFlow<WorkoutPhase>(WorkoutPhase.NotStarted)
    val phase: StateFlow<WorkoutPhase> = _phase

    private val _exerciseStates = MutableStateFlow<List<ExerciseState>>(emptyList())
    val exerciseStates: StateFlow<List<ExerciseState>> = _exerciseStates

    private val _sessionId = MutableStateFlow<Long?>(null)
    val sessionId: StateFlow<Long?> = _sessionId

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds

    private val _restSecondsLeft = MutableStateFlow(0)
    val restSecondsLeft: StateFlow<Int> = _restSecondsLeft

    private var startTime = 0L
    private var timerJob: Job? = null

    fun startWorkout(workout: Workout, wes: List<WorkoutExercise>) {
        viewModelScope.launch {
            startTime = System.currentTimeMillis()
            val id = repo.startSession(WorkoutSession(workoutId = workout.id, workoutName = workout.name, startTime = startTime))
            _sessionId.value = id

            val states = wes.mapNotNull { we ->
                val exercise = repo.getExerciseById(we.exerciseId) ?: return@mapNotNull null
                val suggested = repo.getLastWeightForExercise(we.exerciseId)
                ExerciseState(workoutExercise = we, exercise = exercise, suggestedWeight = suggested)
            }
            _exerciseStates.value = states
            _phase.value = WorkoutPhase.InProgress(0)
            startElapsedTimer()
        }
    }

    private fun startElapsedTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _elapsedSeconds.value = ((System.currentTimeMillis() - startTime) / 1000).toInt()
            }
        }
    }

    fun logSet(exerciseIndex: Int, reps: Int, weightKg: Double, isWarmup: Boolean = false) {
        val sessionId = _sessionId.value ?: return
        val states = _exerciseStates.value
        val state = states.getOrNull(exerciseIndex) ?: return

        viewModelScope.launch {
            val set = SetLog(
                sessionId = sessionId,
                exerciseId = state.exercise.id,
                exerciseName = state.exercise.name,
                setNumber = state.loggedSets.size + 1,
                reps = reps,
                weightKg = weightKg,
                isWarmup = isWarmup,
                completedAt = System.currentTimeMillis()
            )
            val setId = repo.logSet(set)
            val updated = states.toMutableList()
            updated[exerciseIndex] = state.copy(loggedSets = state.loggedSets + set.copy(id = setId))
            _exerciseStates.value = updated

            val restSecs = state.workoutExercise.restSeconds
            if (restSecs > 0) startRestTimer(restSecs, exerciseIndex)
        }
    }

    private fun startRestTimer(seconds: Int, exerciseIndex: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _phase.value = WorkoutPhase.Resting(seconds, exerciseIndex)
            for (remaining in seconds downTo 0) {
                _restSecondsLeft.value = remaining
                _phase.value = WorkoutPhase.Resting(remaining, exerciseIndex)
                if (remaining == 0) break
                delay(1000)
            }
            _phase.value = WorkoutPhase.InProgress(exerciseIndex)
            startElapsedTimer()
        }
    }

    fun skipRest() {
        val p = _phase.value
        if (p is WorkoutPhase.Resting) {
            timerJob?.cancel()
            _phase.value = WorkoutPhase.InProgress(p.exerciseIndex)
            startElapsedTimer()
        }
    }

    fun removeLastSet(exerciseIndex: Int) {
        val states = _exerciseStates.value
        val state = states.getOrNull(exerciseIndex) ?: return
        if (state.loggedSets.isEmpty()) return
        viewModelScope.launch {
            val last = state.loggedSets.last()
            repo.deleteSet(last)
            val updated = states.toMutableList()
            updated[exerciseIndex] = state.copy(loggedSets = state.loggedSets.dropLast(1))
            _exerciseStates.value = updated
        }
    }

    fun goToExercise(index: Int) {
        timerJob?.cancel()
        val size = _exerciseStates.value.size
        if (index in 0 until size) {
            _phase.value = WorkoutPhase.InProgress(index)
            startElapsedTimer()
        }
    }

    fun finishWorkout() {
        timerJob?.cancel()
        viewModelScope.launch {
            val id = _sessionId.value ?: return@launch
            val session = repo.getSessionById(id) ?: return@launch
            val allSets = _exerciseStates.value.flatMap { it.loggedSets }
            repo.updateSession(
                session.copy(
                    endTime = System.currentTimeMillis(),
                    totalVolumeKg = allSets.filter { !it.isWarmup }.sumOf { it.volume },
                    totalSets = allSets.count { !it.isWarmup }
                )
            )
        }
        _phase.value = WorkoutPhase.Finished
    }

    class Factory(private val repo: WorkoutRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ActiveWorkoutViewModel(repo) as T
    }
}
