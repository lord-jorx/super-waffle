package com.lordj.fitnessapp.data.db.dao

import androidx.room.*
import com.lordj.fitnessapp.data.model.Workout
import com.lordj.fitnessapp.data.model.WorkoutExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY orderIndex, name")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): Workout?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkouts(workouts: List<Workout>)

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex")
    fun getWorkoutExercises(workoutId: Long): Flow<List<WorkoutExercise>>

    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex")
    suspend fun getWorkoutExercisesList(workoutId: Long): List<WorkoutExercise>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercise(we: WorkoutExercise): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkoutExercises(list: List<WorkoutExercise>)

    @Delete
    suspend fun deleteWorkoutExercise(we: WorkoutExercise)

    @Query("SELECT COUNT(*) FROM workouts")
    suspend fun getCount(): Int
}
