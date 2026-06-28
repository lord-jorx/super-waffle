package com.lordj.fitnessapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lordj.fitnessapp.data.db.dao.*
import com.lordj.fitnessapp.data.model.*

@Database(
    entities = [Exercise::class, Workout::class, WorkoutExercise::class, WorkoutSession::class, SetLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun setLogDao(): SetLogDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "fitness_db")
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
