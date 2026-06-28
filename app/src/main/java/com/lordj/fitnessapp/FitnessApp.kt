package com.lordj.fitnessapp

import android.app.Application
import com.lordj.fitnessapp.data.db.AppDatabase
import com.lordj.fitnessapp.data.health.HealthConnectManager
import com.lordj.fitnessapp.data.preferences.AppPreferences
import com.lordj.fitnessapp.data.repository.BodyWeightRepository
import com.lordj.fitnessapp.data.repository.ExerciseRepository
import com.lordj.fitnessapp.data.repository.WorkoutRepository
import com.lordj.fitnessapp.data.seeder.DatabaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FitnessApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    val exerciseRepository by lazy {
        ExerciseRepository(database.exerciseDao(), database.setLogDao())
    }

    val workoutRepository by lazy {
        WorkoutRepository(database.workoutDao(), database.workoutSessionDao(), database.setLogDao(), database.exerciseDao())
    }

    val bodyWeightRepository by lazy {
        BodyWeightRepository(database.bodyWeightDao())
    }

    val healthConnectManager by lazy { HealthConnectManager(this) }

    val preferences by lazy { AppPreferences(this) }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseSeeder.seed(database)
            DatabaseSeeder.patchNotes(database)
        }
    }
}
