package com.lordj.fitnessapp.data.db.dao

import androidx.room.*
import com.lordj.fitnessapp.data.model.BodyWeight
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyWeightDao {
    @Query("SELECT * FROM body_weights ORDER BY timestamp DESC")
    fun getAll(): Flow<List<BodyWeight>>

    @Query("SELECT * FROM body_weights ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): BodyWeight?

    @Insert
    suspend fun insert(bw: BodyWeight)

    @Delete
    suspend fun delete(bw: BodyWeight)
}
