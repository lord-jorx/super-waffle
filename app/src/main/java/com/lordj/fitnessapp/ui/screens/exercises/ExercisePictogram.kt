package com.lordj.fitnessapp.ui.screens.exercises

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class MovementType {
    SQUAT, DEADLIFT, BENCH_PRESS, OVERHEAD_PRESS,
    PULLDOWN, ROW, CURL, TRICEP_EXT,
    PLANK, CRUNCH, HIP_THRUST, LEG_PRESS,
    LEG_EXT, LEG_CURL, CALF_RAISE, LATERAL_RAISE,
    LUNGE, CARDIO, GENERIC
}

fun exerciseNameToMovement(name: String): MovementType {
    val n = name.lowercase()
    return when {
        "sentadilla" in n || "squat" in n -> MovementType.SQUAT
        "peso muerto" in n || "deadlift" in n -> MovementType.DEADLIFT
        "press de banca" in n || "press inclinado" in n || "aperturas" in n || "fondos" in n -> MovementType.BENCH_PRESS
        "press militar" in n || "press de hombros" in n || "overhead press" in n -> MovementType.OVERHEAD_PRESS
        "jalón" in n || "jalones" in n || "pulldown" in n || "dominada" in n -> MovementType.PULLDOWN
        "remo" in n || "row" in n || "face pull" in n -> MovementType.ROW
        ("curl" in n && "femoral" !in n) || ("bíceps" in n && "curl" !in n) -> MovementType.CURL
        "tríceps" in n || "tricep" in n || "press francés" in n -> MovementType.TRICEP_EXT
        "plancha" in n || "plank" in n -> MovementType.PLANK
        "crunch" in n || "abdominal" in n || "elevación de piernas" in n -> MovementType.CRUNCH
        "hip thrust" in n || "puente de glúteos" in n -> MovementType.HIP_THRUST
        "prensa" in n || "leg press" in n -> MovementType.LEG_PRESS
        ("extensión" in n || "extension" in n) && ("cuádr" in n || "cuad" in n || "quad" in n) -> MovementType.LEG_EXT
        "curl femoral" in n || "leg curl" in n -> MovementType.LEG_CURL
        "talón" in n || "gemelo" in n || "calf" in n -> MovementType.CALF_RAISE
        ("lateral" in n && ("elevación" in n || "vuelo" in n)) -> MovementType.LATERAL_RAISE
        "zancada" in n || "lunge" in n -> MovementType.LUNGE
        "cardio" in n || "carrera" in n || "correr" in n || "bicicleta" in n -> MovementType.CARDIO
        else -> MovementType.GENERIC
    }
}

@Composable
fun ExercisePictogram(
    exerciseName: String,
    color: Color,
    size: Dp = 110.dp,
    modifier: Modifier = Modifier
) {
    val movement = exerciseNameToMovement(exerciseName)
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val sw = w * 0.055f
        val hr = w * 0.075f

        fun p(x: Float, y: Float) = Offset(x * w, y * h)
        fun line(x1: Float, y1: Float, x2: Float, y2: Float) = drawLine(
            color = color, start = p(x1, y1), end = p(x2, y2),
            strokeWidth = sw, cap = StrokeCap.Round
        )
        fun head(x: Float, y: Float) = drawCircle(color = color, radius = hr, center = p(x, y))

        when (movement) {
            MovementType.SQUAT -> {
                // Side view deep squat
                head(0.38f, 0.10f)
                line(0.38f, 0.17f, 0.52f, 0.46f) // torso leaning forward
                line(0.44f, 0.29f, 0.22f, 0.38f) // arms forward
                line(0.22f, 0.38f, 0.20f, 0.46f)
                line(0.52f, 0.46f, 0.36f, 0.68f) // thigh
                line(0.36f, 0.68f, 0.50f, 0.84f) // shin
                line(0.50f, 0.84f, 0.60f, 0.86f) // foot
                line(0.52f, 0.46f, 0.40f, 0.66f) // second leg slightly behind
                line(0.40f, 0.66f, 0.54f, 0.82f)
            }
            MovementType.DEADLIFT -> {
                // Side view hip hinge / start position
                head(0.62f, 0.11f)
                line(0.55f, 0.18f, 0.30f, 0.30f) // horizontal back
                line(0.30f, 0.30f, 0.36f, 0.56f) // front thigh
                line(0.36f, 0.56f, 0.36f, 0.80f) // shin
                line(0.36f, 0.80f, 0.52f, 0.84f) // foot
                line(0.44f, 0.24f, 0.44f, 0.65f) // arms hanging
                line(0.36f, 0.65f, 0.52f, 0.65f) // bar
                drawCircle(color, hr * 0.6f, p(0.36f, 0.65f))
                drawCircle(color, hr * 0.6f, p(0.52f, 0.65f))
            }
            MovementType.BENCH_PRESS -> {
                // Side view lying on bench
                line(0.05f, 0.72f, 0.90f, 0.72f) // bench
                line(0.62f, 0.72f, 0.62f, 0.88f) // bench leg
                head(0.80f, 0.60f)
                line(0.72f, 0.67f, 0.20f, 0.67f) // body
                line(0.20f, 0.67f, 0.16f, 0.82f) // legs bent at knees
                line(0.16f, 0.82f, 0.28f, 0.88f)
                line(0.55f, 0.67f, 0.46f, 0.42f) // upper arm
                line(0.46f, 0.42f, 0.58f, 0.36f) // forearm to bar
                line(0.40f, 0.36f, 0.72f, 0.36f) // bar
            }
            MovementType.OVERHEAD_PRESS -> {
                // Standing press
                head(0.50f, 0.09f)
                line(0.50f, 0.16f, 0.50f, 0.50f) // torso
                line(0.50f, 0.22f, 0.26f, 0.14f) // left arm up
                line(0.50f, 0.22f, 0.74f, 0.14f) // right arm up
                line(0.22f, 0.10f, 0.78f, 0.10f) // bar
                line(0.50f, 0.50f, 0.35f, 0.74f) // left leg
                line(0.35f, 0.74f, 0.31f, 0.90f)
                line(0.50f, 0.50f, 0.65f, 0.74f) // right leg
                line(0.65f, 0.74f, 0.69f, 0.90f)
            }
            MovementType.PULLDOWN -> {
                // Seated, arms overhead pulling down
                head(0.50f, 0.13f)
                line(0.50f, 0.20f, 0.52f, 0.50f) // torso slight lean
                line(0.50f, 0.28f, 0.24f, 0.14f) // left arm to bar
                line(0.50f, 0.28f, 0.76f, 0.14f) // right arm to bar
                line(0.18f, 0.12f, 0.82f, 0.12f) // bar
                line(0.50f, 0.50f, 0.28f, 0.50f) // left thigh seated
                line(0.50f, 0.50f, 0.72f, 0.50f) // right thigh
                line(0.28f, 0.50f, 0.26f, 0.74f) // left shin
                line(0.72f, 0.50f, 0.74f, 0.74f) // right shin
            }
            MovementType.ROW -> {
                // Bent over row
                head(0.65f, 0.12f)
                line(0.58f, 0.19f, 0.30f, 0.32f) // back horizontal (bent over)
                line(0.30f, 0.32f, 0.34f, 0.60f) // thigh
                line(0.34f, 0.60f, 0.34f, 0.82f) // shin
                line(0.34f, 0.82f, 0.48f, 0.86f) // foot
                line(0.44f, 0.26f, 0.40f, 0.56f) // upper arm pulling
                line(0.40f, 0.56f, 0.30f, 0.58f) // forearm toward body
                line(0.28f, 0.58f, 0.40f, 0.58f) // weight at end
                drawCircle(color, hr * 0.5f, p(0.28f, 0.58f))
            }
            MovementType.CURL -> {
                // Standing bicep curl, arm bent
                head(0.50f, 0.09f)
                line(0.50f, 0.16f, 0.50f, 0.50f)
                line(0.50f, 0.26f, 0.30f, 0.36f) // left arm relaxed
                line(0.30f, 0.36f, 0.28f, 0.52f)
                line(0.50f, 0.26f, 0.70f, 0.32f) // right upper arm
                line(0.70f, 0.32f, 0.64f, 0.16f) // forearm curled up (90°)
                drawCircle(color, hr * 0.55f, p(0.62f, 0.14f)) // dumbbell
                line(0.50f, 0.50f, 0.36f, 0.74f)
                line(0.36f, 0.74f, 0.32f, 0.90f)
                line(0.50f, 0.50f, 0.64f, 0.74f)
                line(0.64f, 0.74f, 0.68f, 0.90f)
            }
            MovementType.TRICEP_EXT -> {
                // Standing overhead tricep extension
                head(0.50f, 0.09f)
                line(0.50f, 0.16f, 0.50f, 0.50f)
                line(0.50f, 0.22f, 0.42f, 0.11f) // left upper arm up
                line(0.42f, 0.11f, 0.48f, 0.26f) // forearm bent behind head
                line(0.50f, 0.22f, 0.58f, 0.11f) // right upper arm
                line(0.58f, 0.11f, 0.52f, 0.26f)
                drawCircle(color, hr * 0.5f, p(0.50f, 0.08f))
                line(0.50f, 0.50f, 0.36f, 0.74f)
                line(0.36f, 0.74f, 0.32f, 0.90f)
                line(0.50f, 0.50f, 0.64f, 0.74f)
                line(0.64f, 0.74f, 0.68f, 0.90f)
            }
            MovementType.PLANK -> {
                // Horizontal plank on forearms
                head(0.82f, 0.40f)
                line(0.74f, 0.47f, 0.18f, 0.47f) // body horizontal
                line(0.64f, 0.47f, 0.60f, 0.62f) // right forearm
                line(0.60f, 0.62f, 0.44f, 0.62f) // forearm on ground
                line(0.18f, 0.47f, 0.16f, 0.62f) // feet
                line(0.16f, 0.62f, 0.24f, 0.65f)
                line(0.08f, 0.66f, 0.92f, 0.66f) // ground line
            }
            MovementType.CRUNCH -> {
                // Crunching: on floor, upper body raised
                head(0.75f, 0.40f)
                line(0.67f, 0.47f, 0.38f, 0.56f) // torso angled
                line(0.38f, 0.56f, 0.22f, 0.56f) // lower body flat
                line(0.22f, 0.56f, 0.14f, 0.74f) // thighs up (knees bent)
                line(0.14f, 0.74f, 0.24f, 0.84f) // feet
                line(0.58f, 0.46f, 0.38f, 0.46f) // arms reaching forward
                line(0.08f, 0.86f, 0.92f, 0.86f) // ground
            }
            MovementType.HIP_THRUST -> {
                // Back on bench, hips raised, feet on floor
                line(0.55f, 0.64f, 0.92f, 0.64f) // bench
                head(0.80f, 0.53f)
                line(0.73f, 0.60f, 0.42f, 0.60f) // torso angled down from bench
                line(0.42f, 0.60f, 0.30f, 0.40f) // thigh going up (hips raised)
                line(0.30f, 0.40f, 0.20f, 0.60f) // shin going down
                line(0.20f, 0.60f, 0.28f, 0.68f) // foot flat
                line(0.08f, 0.86f, 0.55f, 0.86f) // ground
                line(0.20f, 0.68f, 0.20f, 0.86f)
            }
            MovementType.LEG_PRESS -> {
                // Seated in leg press machine
                head(0.72f, 0.18f)
                line(0.65f, 0.25f, 0.65f, 0.60f) // back reclined
                line(0.65f, 0.60f, 0.22f, 0.58f) // thighs horizontal to platform
                line(0.22f, 0.58f, 0.10f, 0.38f) // shins to pressing plate
                line(0.08f, 0.32f, 0.08f, 0.64f) // platform vertical
                line(0.08f, 0.48f, 0.20f, 0.48f) // footplate
            }
            MovementType.LEG_EXT -> {
                // Seated, one leg extended horizontally
                head(0.50f, 0.12f)
                line(0.50f, 0.19f, 0.50f, 0.52f) // torso seated
                line(0.50f, 0.52f, 0.14f, 0.52f) // thigh on seat
                line(0.50f, 0.52f, 0.76f, 0.52f) // other thigh
                // Extended leg
                line(0.14f, 0.52f, 0.06f, 0.52f) // thigh further
                line(0.06f, 0.52f, 0.08f, 0.76f) // shin hanging (resting)
                // Leg being extended
                line(0.76f, 0.52f, 0.92f, 0.44f) // shin extended up
                drawCircle(color, hr * 0.5f, p(0.92f, 0.42f)) // ankle pad
                // Seat
                line(0.08f, 0.84f, 0.92f, 0.84f)
            }
            MovementType.LEG_CURL -> {
                // Lying face down, leg curled
                head(0.14f, 0.40f)
                line(0.21f, 0.46f, 0.80f, 0.46f) // body prone
                line(0.80f, 0.46f, 0.68f, 0.46f) // right thigh flat
                line(0.80f, 0.46f, 0.62f, 0.46f)
                line(0.62f, 0.46f, 0.58f, 0.26f) // right shin curled up
                drawCircle(color, hr * 0.5f, p(0.58f, 0.24f)) // ankle pad
                line(0.68f, 0.46f, 0.65f, 0.52f) // second leg relaxed
                line(0.65f, 0.52f, 0.62f, 0.64f)
                line(0.08f, 0.56f, 0.92f, 0.56f) // bench/ground
            }
            MovementType.CALF_RAISE -> {
                // Standing on tiptoes
                head(0.50f, 0.09f)
                line(0.50f, 0.16f, 0.50f, 0.50f) // torso
                line(0.50f, 0.27f, 0.30f, 0.38f) // arms (holding support)
                line(0.50f, 0.27f, 0.70f, 0.38f)
                line(0.50f, 0.50f, 0.40f, 0.72f) // left thigh
                line(0.40f, 0.72f, 0.38f, 0.84f) // left shin
                line(0.50f, 0.50f, 0.60f, 0.72f) // right thigh
                line(0.60f, 0.72f, 0.62f, 0.84f) // right shin
                // Tiptoes (raised heels)
                line(0.38f, 0.84f, 0.35f, 0.78f) // heel up
                line(0.35f, 0.78f, 0.42f, 0.78f) // ball of foot on ground
                line(0.62f, 0.84f, 0.65f, 0.78f)
                line(0.65f, 0.78f, 0.58f, 0.78f)
                line(0.20f, 0.90f, 0.80f, 0.90f) // ground
            }
            MovementType.LATERAL_RAISE -> {
                // Standing, arms raised laterally to shoulder height
                head(0.50f, 0.09f)
                line(0.50f, 0.16f, 0.50f, 0.50f)
                line(0.50f, 0.26f, 0.16f, 0.40f) // left arm raised to side
                line(0.50f, 0.26f, 0.84f, 0.40f) // right arm raised to side
                drawCircle(color, hr * 0.5f, p(0.14f, 0.40f))
                drawCircle(color, hr * 0.5f, p(0.86f, 0.40f))
                line(0.50f, 0.50f, 0.36f, 0.74f)
                line(0.36f, 0.74f, 0.32f, 0.90f)
                line(0.50f, 0.50f, 0.64f, 0.74f)
                line(0.64f, 0.74f, 0.68f, 0.90f)
            }
            MovementType.LUNGE -> {
                // Side view lunge
                head(0.45f, 0.10f)
                line(0.45f, 0.17f, 0.46f, 0.48f) // torso upright
                line(0.46f, 0.28f, 0.30f, 0.38f) // arms
                line(0.46f, 0.28f, 0.62f, 0.38f)
                line(0.46f, 0.48f, 0.24f, 0.66f) // front leg thigh
                line(0.24f, 0.66f, 0.21f, 0.84f) // front shin
                line(0.21f, 0.84f, 0.34f, 0.87f) // front foot
                line(0.46f, 0.48f, 0.65f, 0.62f) // back leg thigh
                line(0.65f, 0.62f, 0.68f, 0.85f) // back shin
                line(0.68f, 0.85f, 0.78f, 0.87f) // back foot
            }
            MovementType.CARDIO -> {
                // Running pose
                head(0.55f, 0.09f)
                line(0.55f, 0.16f, 0.50f, 0.48f) // torso (forward lean)
                line(0.53f, 0.26f, 0.33f, 0.16f) // left arm forward+up
                line(0.53f, 0.26f, 0.68f, 0.38f) // right arm back
                line(0.50f, 0.48f, 0.62f, 0.66f) // left leg back
                line(0.62f, 0.66f, 0.72f, 0.52f) // shin kick back
                line(0.50f, 0.48f, 0.34f, 0.62f) // right leg forward
                line(0.34f, 0.62f, 0.27f, 0.80f) // shin down
                line(0.27f, 0.80f, 0.36f, 0.84f) // foot
            }
            MovementType.GENERIC -> {
                // Simple standing figure
                head(0.50f, 0.09f)
                line(0.50f, 0.16f, 0.50f, 0.52f)
                line(0.50f, 0.26f, 0.28f, 0.42f)
                line(0.50f, 0.26f, 0.72f, 0.42f)
                line(0.50f, 0.52f, 0.35f, 0.74f)
                line(0.35f, 0.74f, 0.31f, 0.90f)
                line(0.50f, 0.52f, 0.65f, 0.74f)
                line(0.65f, 0.74f, 0.69f, 0.90f)
            }
        }
    }
}
