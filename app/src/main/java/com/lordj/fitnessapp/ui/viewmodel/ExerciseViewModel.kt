package com.lordj.fitnessapp.ui.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import com.lordj.fitnessapp.data.model.Exercise
import com.lordj.fitnessapp.data.model.SetLog
import com.lordj.fitnessapp.data.repository.ExerciseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repo: ExerciseRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _category = MutableStateFlow("Todos")
    val category: StateFlow<String> = _category

    val categories: StateFlow<List<String>> = repo.getAllCategories()
        .map { listOf("Todos") + it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Todos"))

    @OptIn(ExperimentalCoroutinesApi::class)
    val exercises: StateFlow<List<Exercise>> = combine(_query, _category) { q, cat -> q to cat }
        .flatMapLatest { (q, cat) ->
            when {
                q.isNotBlank() -> repo.searchExercises(q)
                cat != "Todos" -> repo.getExercisesByCategory(cat)
                else -> repo.getAllExercises()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(q: String) { _query.value = q }
    fun setCategory(cat: String) { _category.value = cat }

    private val _exercise = MutableStateFlow<Exercise?>(null)
    val exercise: StateFlow<Exercise?> = _exercise

    private val _sets = MutableStateFlow<List<SetLog>>(emptyList())
    val sets: StateFlow<List<SetLog>> = _sets

    private val _maxWeight = MutableStateFlow(0.0)
    val maxWeight: StateFlow<Double> = _maxWeight

    fun loadExercise(id: Long) {
        viewModelScope.launch {
            _exercise.value = repo.getExerciseById(id)
            _maxWeight.value = repo.getMaxWeightForExercise(id) ?: 0.0
        }
        repo.getSetsByExercise(id)
            .onEach { _sets.value = it }
            .launchIn(viewModelScope)
    }

    class Factory(private val repo: ExerciseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ExerciseViewModel(repo) as T
    }
}
