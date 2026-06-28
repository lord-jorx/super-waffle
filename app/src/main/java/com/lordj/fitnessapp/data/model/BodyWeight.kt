package com.lordj.fitnessapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_weights")
data class BodyWeight(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
