package com.lordj.fitnessapp.data.repository

import com.lordj.fitnessapp.data.db.dao.*
import com.lordj.fitnessapp.data.model.*
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val workoutSessionDao: WorkoutSessionDao,
    private val setLogDao: SetLogDao,
    private val exerciseDao: ExerciseDao
) {
    fun getAllWorkouts(): Flow<List<Workout>> = workoutDao.getAllWorkouts()
    fun getAllSessions(): Flow<List<WorkoutSession>> = workoutSessionDao.getAllSessions()
    fun getRecentSessions(limit: Int = 10): Flow<List<WorkoutSession>> = workoutSessionDao.getRecentSessions(limit)
    fun getWorkoutExercises(workoutId: Long): Flow<List<WorkoutExercise>> = workoutDao.getWorkoutExercises(workoutId)
    fun getSetsBySession(sessionId: Long): Flow<List<SetLog>> = setLogDao.getSetsBySession(sessionId)
    fun getAllSets(): Flow<List<SetLog>> = setLogDao.getAllSets()
    fun getSessionsFrom(fromTime: Long): Flow<List<WorkoutSession>> = workoutSessionDao.getSessionsFrom(fromTime)

    suspend fun getWorkoutById(id: Long): Workout? = workoutDao.getWorkoutById(id)
    suspend fun getSessionById(id: Long): WorkoutSession? = workoutSessionDao.getSessionById(id)
    suspend fun getExerciseById(id: Long): Exercise? = exerciseDao.getExerciseById(id)
    suspend fun getWorkoutExercisesList(workoutId: Long): List<WorkoutExercise> = workoutDao.getWorkoutExercisesList(workoutId)

    suspend fun startSession(session: WorkoutSession): Long = workoutSessionDao.insertSession(session)
    suspend fun updateSession(session: WorkoutSession) = workoutSessionDao.updateSession(session)
    suspend fun logSet(setLog: SetLog): Long = setLogDao.insertSet(setLog)
    suspend fun updateSet(setLog: SetLog) = setLogDao.updateSet(setLog)
    suspend fun deleteSet(setLog: SetLog) = setLogDao.deleteSet(setLog)

    suspend fun getLastWeightForExercise(exerciseId: Long): Double {
        val sets = setLogDao.getLastSessionSetsForExercise(exerciseId)
        return sets.filter { !it.isWarmup }.maxOfOrNull { it.weightKg } ?: 0.0
    }

    suspend fun insertWorkout(workout: Workout): Long = workoutDao.insertWorkout(workout)
    suspend fun updateWorkout(workout: Workout) = workoutDao.updateWorkout(workout)
    suspend fun deleteWorkout(workout: Workout) = workoutDao.deleteWorkout(workout)
    suspend fun insertWorkoutExercise(we: WorkoutExercise): Long = workoutDao.insertWorkoutExercise(we)
    suspend fun deleteWorkoutExercise(we: WorkoutExercise) = workoutDao.deleteWorkoutExercise(we)
    suspend fun deleteSession(session: WorkoutSession) = workoutSessionDao.deleteSession(session)
}
