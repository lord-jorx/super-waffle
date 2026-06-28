package com.lordj.fitnessapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val nameEn: String = "",
    val category: String,
    val primaryMuscle: String,
    val secondaryMuscles: String = "",
    val equipment: String,
    val description: String,
    val executionSteps: String = "",
    val tips: String = "",
    val isCustom: Boolean = false,
    val isFromUserRoutine: Boolean = false
)
