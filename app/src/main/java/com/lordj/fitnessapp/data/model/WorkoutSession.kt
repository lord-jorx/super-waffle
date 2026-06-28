package com.lordj.fitnessapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_sessions",
    foreignKeys = [
        ForeignKey(entity = Workout::class, parentColumns = ["id"], childColumns = ["workoutId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("workoutId")]
)
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long? = null,
    val workoutName: String,
    val startTime: Long,
    val endTime: Long? = null,
    val notes: String = "",
    val totalVolumeKg: Double = 0.0,
    val totalSets: Int = 0
) {
    val durationMinutes: Long get() = if (endTime != null) (endTime - startTime) / 60000L else 0L
}
