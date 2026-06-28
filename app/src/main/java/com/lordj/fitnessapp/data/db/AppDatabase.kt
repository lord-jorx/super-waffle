package com.lordj.fitnessapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lordj.fitnessapp.data.db.dao.*
import com.lordj.fitnessapp.data.model.*

@Database(
    entities = [
        Exercise::class,
        Workout::class,
        WorkoutExercise::class,
        WorkoutSession::class,
        SetLog::class,
        BodyWeight::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun setLogDao(): SetLogDao
    abstract fun bodyWeightDao(): BodyWeightDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS body_weights (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        weightKg REAL NOT NULL,
                        timestamp INTEGER NOT NULL,
                        notes TEXT NOT NULL DEFAULT ''
                    )"""
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "fitness_db")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
