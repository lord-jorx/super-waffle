package com.lordj.fitnessapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lordj.fitnessapp.data.model.BodyWeight
import com.lordj.fitnessapp.data.repository.BodyWeightRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BodyWeightViewModel(private val repo: BodyWeightRepository) : ViewModel() {

    val entries = repo.all.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun log(kg: Double, notes: String = "") {
        viewModelScope.launch { repo.insert(kg, notes) }
    }

    fun delete(bw: BodyWeight) {
        viewModelScope.launch { repo.delete(bw) }
    }

    class Factory(private val repo: BodyWeightRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = BodyWeightViewModel(repo) as T
    }
}
