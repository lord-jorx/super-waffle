package com.lordj.fitnessapp.data.db.dao

import androidx.room.*
import com.lordj.fitnessapp.data.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): WorkoutSession?

    @Query("SELECT * FROM workout_sessions ORDER BY startTime DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 10): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE startTime >= :fromTime ORDER BY startTime DESC")
    fun getSessionsFrom(fromTime: Long): Flow<List<WorkoutSession>>

    @Insert
    suspend fun insertSession(session: WorkoutSession): Long

    @Update
    suspend fun updateSession(session: WorkoutSession)

    @Delete
    suspend fun deleteSession(session: WorkoutSession)
}
