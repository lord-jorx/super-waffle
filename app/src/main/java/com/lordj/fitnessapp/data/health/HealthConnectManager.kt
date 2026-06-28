package com.lordj.fitnessapp.data.health

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

data class GarminSession(
    val id: String,
    val title: String,
    val exerciseTypeName: String,
    val exerciseTypeEmoji: String,
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Int,
    val calories: Double,
    val avgHeartRateBpm: Int?,
    val maxHeartRateBpm: Int?,
    val sourceApp: String
)

class HealthConnectManager(private val context: Context) {

    companion object {
        const val HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata"

        val PERMISSIONS = setOf(
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(StepsRecord::class),
        )
    }

    private val client: HealthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }

    fun getSdkStatus(): Int = HealthConnectClient.getSdkStatus(context)

    fun isAvailable(): Boolean =
        getSdkStatus() == HealthConnectClient.SDK_AVAILABLE

    fun needsInstall(): Boolean =
        getSdkStatus() == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED

    suspend fun hasPermissions(): Boolean {
        if (!isAvailable()) return false
        return client.permissionController.getGrantedPermissions().containsAll(PERMISSIONS)
    }

    suspend fun getRecentSessions(days: Int = 60): List<GarminSession> {
        val now = Instant.now()
        val start = now.minus(days.toLong(), ChronoUnit.DAYS)
        val timeRange = TimeRangeFilter.between(start, now)

        val sessions = client.readRecords(
            ReadRecordsRequest(ExerciseSessionRecord::class, timeRange)
        ).records

        return sessions.map { session ->
            val sessionRange = TimeRangeFilter.between(session.startTime, session.endTime)

            val hrSamples = try {
                client.readRecords(ReadRecordsRequest(HeartRateRecord::class, sessionRange))
                    .records.flatMap { it.samples }
            } catch (_: Exception) { emptyList() }

            val avgHr = if (hrSamples.isNotEmpty())
                hrSamples.map { it.beatsPerMinute }.average().roundToInt() else null
            val maxHr = hrSamples.maxOfOrNull { it.beatsPerMinute }?.toInt()

            val totalCal = try {
                client.readRecords(ReadRecordsRequest(ActiveCaloriesBurnedRecord::class, sessionRange))
                    .records.sumOf { it.energy.inKilocalories }
            } catch (_: Exception) { 0.0 }

            val durationSecs = session.endTime.epochSecond - session.startTime.epochSecond
            val source = session.metadata.dataOrigin.packageName
                .removePrefix("com.")
                .split(".")
                .firstOrNull()
                ?.replaceFirstChar { it.uppercase() } ?: "Health Connect"

            GarminSession(
                id = session.metadata.id,
                title = session.title?.takeIf { it.isNotBlank() } ?: exerciseTypeName(session.exerciseType),
                exerciseTypeName = exerciseTypeName(session.exerciseType),
                exerciseTypeEmoji = exerciseTypeEmoji(session.exerciseType),
                startTime = session.startTime.toEpochMilli(),
                endTime = session.endTime.toEpochMilli(),
                durationMinutes = (durationSecs / 60).toInt(),
                calories = totalCal,
                avgHeartRateBpm = avgHr,
                maxHeartRateBpm = maxHr,
                sourceApp = source
            )
        }.sortedByDescending { it.startTime }
    }

    fun getInstallIntent(): Intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("market://details?id=$HEALTH_CONNECT_PACKAGE")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    private fun exerciseTypeName(type: Int): String = when (type) {
        ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> "Carrera"
        ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> "Caminata"
        ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> "Ciclismo"
        ExerciseSessionRecord.EXERCISE_TYPE_BIKING_STATIONARY -> "Bicicleta Estática"
        ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING -> "Entrenamiento de Fuerza"
        ExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING -> "Pesas"
        ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER -> "Natación Aguas Abiertas"
        ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL -> "Natación Piscina"
        ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> "Yoga"
        ExerciseSessionRecord.EXERCISE_TYPE_PILATES -> "Pilates"
        ExerciseSessionRecord.EXERCISE_TYPE_HIKING -> "Senderismo"
        ExerciseSessionRecord.EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING -> "HIIT"
        ExerciseSessionRecord.EXERCISE_TYPE_ELLIPTICAL -> "Elíptica"
        ExerciseSessionRecord.EXERCISE_TYPE_ROWING_MACHINE -> "Remo"
        ExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING -> "Escaleras"
        ExerciseSessionRecord.EXERCISE_TYPE_STRETCHING -> "Estiramiento"
        ExerciseSessionRecord.EXERCISE_TYPE_DANCING -> "Baile"
        ExerciseSessionRecord.EXERCISE_TYPE_GOLF -> "Golf"
        ExerciseSessionRecord.EXERCISE_TYPE_BASKETBALL -> "Baloncesto"
        ExerciseSessionRecord.EXERCISE_TYPE_VOLLEYBALL -> "Voleibol"
        ExerciseSessionRecord.EXERCISE_TYPE_BADMINTON -> "Bádminton"
        ExerciseSessionRecord.EXERCISE_TYPE_TENNIS -> "Tenis"
        ExerciseSessionRecord.EXERCISE_TYPE_FOOTBALL_AMERICAN -> "Fútbol Americano"
        ExerciseSessionRecord.EXERCISE_TYPE_FOOTBALL_AUSTRALIAN -> "Fútbol Australiano"
        else -> "Ejercicio"
    }

    private fun exerciseTypeEmoji(type: Int): String = when (type) {
        ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> "🏃"
        ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> "🚶"
        ExerciseSessionRecord.EXERCISE_TYPE_BIKING,
        ExerciseSessionRecord.EXERCISE_TYPE_BIKING_STATIONARY -> "🚴"
        ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING,
        ExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING -> "🏋️"
        ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER,
        ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL -> "🏊"
        ExerciseSessionRecord.EXERCISE_TYPE_YOGA,
        ExerciseSessionRecord.EXERCISE_TYPE_PILATES -> "🧘"
        ExerciseSessionRecord.EXERCISE_TYPE_HIKING -> "🥾"
        ExerciseSessionRecord.EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING -> "⚡"
        ExerciseSessionRecord.EXERCISE_TYPE_ELLIPTICAL -> "🔄"
        ExerciseSessionRecord.EXERCISE_TYPE_ROWING_MACHINE -> "🚣"
        ExerciseSessionRecord.EXERCISE_TYPE_TENNIS -> "🎾"
        ExerciseSessionRecord.EXERCISE_TYPE_BASKETBALL -> "🏀"
        ExerciseSessionRecord.EXERCISE_TYPE_VOLLEYBALL -> "🏐"
        ExerciseSessionRecord.EXERCISE_TYPE_GOLF -> "⛳"
        ExerciseSessionRecord.EXERCISE_TYPE_DANCING -> "💃"
        else -> "🏃"
    }
}
