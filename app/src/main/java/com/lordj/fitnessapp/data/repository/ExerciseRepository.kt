package com.lordj.fitnessapp.data.repository

import com.lordj.fitnessapp.data.db.dao.ExerciseDao
import com.lordj.fitnessapp.data.db.dao.SetLogDao
import com.lordj.fitnessapp.data.model.Exercise
import com.lordj.fitnessapp.data.model.SetLog
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(
    private val exerciseDao: ExerciseDao,
    private val setLogDao: SetLogDao
) {
    fun getAllExercises(): Flow<List<Exercise>> = exerciseDao.getAllExercises()
    fun getExercisesByCategory(category: String): Flow<List<Exercise>> = exerciseDao.getExercisesByCategory(category)
    fun searchExercises(query: String): Flow<List<Exercise>> = exerciseDao.searchExercises(query)
    fun getAllCategories(): Flow<List<String>> = exerciseDao.getAllCategories()
    fun getSetsByExercise(exerciseId: Long): Flow<List<SetLog>> = setLogDao.getSetsByExercise(exerciseId)

    suspend fun getExerciseById(id: Long): Exercise? = exerciseDao.getExerciseById(id)
    suspend fun searchExerciseByName(name: String): Exercise? = exerciseDao.getExerciseByName(name)
    suspend fun getMaxWeightForExercise(id: Long): Double? = setLogDao.getMaxWeightForExercise(id)
    suspend fun getRecentSetsForExercise(id: Long, limit: Int = 100): List<SetLog> = setLogDao.getRecentSetsByExercise(id, limit)
    suspend fun insertExercise(exercise: Exercise): Long = exerciseDao.insertExercise(exercise)
    suspend fun updateExercise(exercise: Exercise) = exerciseDao.updateExercise(exercise)
    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.deleteExercise(exercise)
}
