package com.lordj.fitnessapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val dayLabel: String = "",
    val colorHex: String = "#FF6200EE",
    val estimatedMinutes: Int = 60,
    val isUserRoutine: Boolean = false,
    val orderIndex: Int = 0
)
