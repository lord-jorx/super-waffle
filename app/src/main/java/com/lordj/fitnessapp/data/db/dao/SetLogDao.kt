package com.lordj.fitnessapp.data.db.dao

import androidx.room.*
import com.lordj.fitnessapp.data.model.SetLog
import kotlinx.coroutines.flow.Flow

@Dao
interface SetLogDao {
    @Query("SELECT * FROM set_logs WHERE sessionId = :sessionId ORDER BY exerciseName, setNumber")
    fun getSetsBySession(sessionId: Long): Flow<List<SetLog>>

    @Query("SELECT * FROM set_logs WHERE exerciseId = :exerciseId ORDER BY completedAt DESC")
    fun getSetsByExercise(exerciseId: Long): Flow<List<SetLog>>

    @Query("SELECT * FROM set_logs WHERE exerciseId = :exerciseId ORDER BY completedAt DESC LIMIT :limit")
    suspend fun getRecentSetsByExercise(exerciseId: Long, limit: Int = 100): List<SetLog>

    @Query("SELECT MAX(weightKg) FROM set_logs WHERE exerciseId = :exerciseId AND isWarmup = 0")
    suspend fun getMaxWeightForExercise(exerciseId: Long): Double?

    @Query("""
        SELECT * FROM set_logs
        WHERE exerciseId = :exerciseId
        AND sessionId = (
            SELECT sessionId FROM set_logs
            WHERE exerciseId = :exerciseId
            ORDER BY completedAt DESC LIMIT 1
        )
        ORDER BY setNumber
    """)
    suspend fun getLastSessionSetsForExercise(exerciseId: Long): List<SetLog>

    @Query("SELECT * FROM set_logs ORDER BY completedAt DESC")
    fun getAllSets(): Flow<List<SetLog>>

    @Insert
    suspend fun insertSet(setLog: SetLog): Long

    @Insert
    suspend fun insertSets(sets: List<SetLog>)

    @Update
    suspend fun updateSet(setLog: SetLog)

    @Delete
    suspend fun deleteSet(setLog: SetLog)

    @Query("DELETE FROM set_logs WHERE sessionId = :sessionId")
    suspend fun deleteSessionSets(sessionId: Long)
}
