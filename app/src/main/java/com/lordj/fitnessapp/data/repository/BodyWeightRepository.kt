package com.lordj.fitnessapp.data.repository

import com.lordj.fitnessapp.data.db.dao.BodyWeightDao
import com.lordj.fitnessapp.data.model.BodyWeight
import kotlinx.coroutines.flow.Flow

class BodyWeightRepository(private val dao: BodyWeightDao) {
    val all: Flow<List<BodyWeight>> = dao.getAll()

    suspend fun insert(weightKg: Double, notes: String = "") =
        dao.insert(BodyWeight(weightKg = weightKg, notes = notes))

    suspend fun delete(bw: BodyWeight) = dao.delete(bw)
}
