package com.lordj.fitnessapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "set_logs",
    foreignKeys = [
        ForeignKey(entity = WorkoutSession::class, parentColumns = ["id"], childColumns = ["sessionId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Exercise::class, parentColumns = ["id"], childColumns = ["exerciseId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("sessionId"), Index("exerciseId")]
)
data class SetLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val setNumber: Int,
    val reps: Int,
    val weightKg: Double = 0.0,
    val isBodyweight: Boolean = false,
    val isWarmup: Boolean = false,
    val rpe: Int? = null,
    val durationSeconds: Int? = null,
    val notes: String = "",
    val completedAt: Long
) {
    val volume: Double get() = reps * weightKg
    val e1RM: Double get() = if (reps > 0 && weightKg > 0) weightKg * (1.0 + reps / 30.0) else 0.0
}
