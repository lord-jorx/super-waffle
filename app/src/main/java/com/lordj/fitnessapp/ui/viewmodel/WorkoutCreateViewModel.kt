package com.lordj.fitnessapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lordj.fitnessapp.data.model.Exercise
import com.lordj.fitnessapp.data.model.Workout
import com.lordj.fitnessapp.data.model.WorkoutExercise
import com.lordj.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WorkoutExerciseDraft(
    val exercise: Exercise,
    val targetSets: Int = 3,
    val targetReps: Int = 10,
    val restSeconds: Int = 90
)

class WorkoutCreateViewModel(private val repo: WorkoutRepository) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _dayLabel = MutableStateFlow("")
    val dayLabel = _dayLabel.asStateFlow()

    private val _exercises = MutableStateFlow<List<WorkoutExerciseDraft>>(emptyList())
    val exercises = _exercises.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved = _saved.asStateFlow()

    fun setName(v: String) { _name.value = v }
    fun setDescription(v: String) { _description.value = v }
    fun setDayLabel(v: String) { _dayLabel.value = v }

    fun addExercise(exercise: Exercise) {
        if (_exercises.value.none { it.exercise.id == exercise.id }) {
            _exercises.value = _exercises.value + WorkoutExerciseDraft(exercise)
        }
    }

    fun removeExercise(exerciseId: Long) {
        _exercises.value = _exercises.value.filter { it.exercise.id != exerciseId }
    }

    fun updateDraft(exerciseId: Long, sets: Int? = null, reps: Int? = null, rest: Int? = null) {
        _exercises.value = _exercises.value.map { d ->
            if (d.exercise.id == exerciseId) d.copy(
                targetSets = sets ?: d.targetSets,
                targetReps = reps ?: d.targetReps,
                restSeconds = rest ?: d.restSeconds
            ) else d
        }
    }

    fun saveWorkout() {
        val workoutName = _name.value.trim()
        if (workoutName.isBlank()) return
        viewModelScope.launch {
            val workout = Workout(
                name = workoutName,
                description = _description.value.trim(),
                dayLabel = _dayLabel.value.trim(),
                colorHex = "#6366F1",
                estimatedMinutes = (_exercises.value.sumOf { it.targetSets * (it.targetReps / 3 + it.restSeconds) } / 60).coerceAtLeast(15),
                isUserRoutine = true,
                orderIndex = 999
            )
            val workoutId = repo.insertWorkout(workout)
            _exercises.value.forEachIndexed { idx, draft ->
                repo.insertWorkoutExercise(
                    WorkoutExercise(
                        workoutId = workoutId,
                        exerciseId = draft.exercise.id,
                        orderIndex = idx,
                        targetSets = draft.targetSets,
                        targetReps = draft.targetReps,
                        restSeconds = draft.restSeconds
                    )
                )
            }
            _saved.value = true
        }
    }

    class Factory(private val repo: WorkoutRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            WorkoutCreateViewModel(repo) as T
    }
}
