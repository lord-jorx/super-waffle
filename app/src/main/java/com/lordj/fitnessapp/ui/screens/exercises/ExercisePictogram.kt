package com.lordj.fitnessapp.ui.screens.exercises

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class MovementType {
    SQUAT, DEADLIFT, BENCH_PRESS, OVERHEAD_PRESS, PULLDOWN, ROW,
    CURL, TRICEP_EXT, PLANK, CRUNCH, HIP_THRUST, LEG_PRESS,
    LEG_EXT, LEG_CURL, CALF_RAISE, LATERAL_RAISE, LUNGE, CARDIO, GENERIC
}

fun exerciseNameToMovement(name: String): MovementType {
    val lower = name.lowercase()
    return when {
        "sentadilla" in lower || "squat" in lower -> MovementType.SQUAT
        "peso muerto" in lower || "deadlift" in lower || "rdl" in lower -> MovementType.DEADLIFT
        "press banca" in lower || "bench" in lower || ("pecho" in lower && "press" in lower) -> MovementType.BENCH_PRESS
        "press militar" in lower || "overhead" in lower || "ohp" in lower -> MovementType.OVERHEAD_PRESS
        "jalón" in lower || "jalon" in lower || "pulldown" in lower || "polea alta" in lower -> MovementType.PULLDOWN
        "remo" in lower || "row" in lower -> MovementType.ROW
        "curl" in lower || "bícep" in lower || "bicep" in lower -> MovementType.CURL
        "trícep" in lower || "tricep" in lower -> MovementType.TRICEP_EXT
        "plancha" in lower || "plank" in lower -> MovementType.PLANK
        "crunch" in lower || "abdominal" in lower -> MovementType.CRUNCH
        "hip thrust" in lower || "puente" in lower -> MovementType.HIP_THRUST
        "prensa" in lower || "leg press" in lower -> MovementType.LEG_PRESS
        "extensión de pierna" in lower || "leg extension" in lower -> MovementType.LEG_EXT
        "curl de pierna" in lower || "leg curl" in lower || "femoral" in lower -> MovementType.LEG_CURL
        "gemelo" in lower || "pantorrilla" in lower || "calf" in lower -> MovementType.CALF_RAISE
        "lateral" in lower && ("hombro" in lower || "elevación" in lower) -> MovementType.LATERAL_RAISE
        "zancada" in lower || "lunge" in lower -> MovementType.LUNGE
        "cardio" in lower || "carrera" in lower || "running" in lower || "bicicleta" in lower -> MovementType.CARDIO
        else -> MovementType.GENERIC
    }
}

// Joint indices in the flat FloatArray (15 joints × 2 coords = 30 values):
// 0:head  1:neck  2:hip_center
// 3:l_shou  4:l_elb  5:l_hand
// 6:r_shou  7:r_elb  8:r_hand
// 9:l_hip  10:l_knee  11:l_foot
// 12:r_hip  13:r_knee  14:r_foot

private data class Pose(val label: String, val j: FloatArray)

private fun DrawScope.drawPose(pose: Pose, color: Color, sw: Float, hr: Float) {
    val j = pose.j
    val w = size.width; val h = size.height
    fun o(i: Int) = Offset(j[i * 2] * w, j[i * 2 + 1] * h)
    fun l(a: Int, b: Int) = drawLine(color, o(a), o(b), sw, cap = StrokeCap.Round)
    drawCircle(color, hr, o(0))     // head
    l(1, 2)                          // spine
    l(1, 3); l(3, 4); l(4, 5)       // left arm
    l(1, 6); l(6, 7); l(7, 8)       // right arm
    l(2, 9); l(9, 10); l(10, 11)    // left leg
    l(2, 12); l(12, 13); l(13, 14)  // right leg
}

private fun pose(label: String, vararg v: Float) = Pose(label, v)

private fun getPoses(movement: MovementType): List<Pose> = when (movement) {

    MovementType.SQUAT -> listOf(
        pose("De pie",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .24f,.38f, .20f,.52f,
            .70f,.22f, .76f,.38f, .80f,.52f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f),
        pose("Descenso",
            .47f,.18f, .46f,.27f, .50f,.57f,
            .27f,.29f, .22f,.44f, .20f,.60f,
            .67f,.29f, .72f,.44f, .80f,.60f,
            .36f,.59f, .25f,.76f, .22f,.93f,
            .62f,.59f, .73f,.76f, .78f,.93f),
        pose("Arriba",
            .50f,.10f, .50f,.20f, .50f,.53f,
            .30f,.23f, .24f,.39f, .20f,.53f,
            .70f,.23f, .76f,.39f, .80f,.53f,
            .38f,.55f, .37f,.77f, .36f,.96f,
            .62f,.55f, .63f,.77f, .64f,.96f)
    )

    MovementType.DEADLIFT -> listOf(
        pose("Inicial (hinge)",
            .46f,.22f, .44f,.30f, .48f,.62f,
            .25f,.33f, .22f,.50f, .22f,.67f,
            .65f,.33f, .68f,.50f, .68f,.67f,
            .35f,.63f, .32f,.80f, .30f,.96f,
            .60f,.63f, .63f,.80f, .65f,.96f),
        pose("Tirón",
            .48f,.14f, .47f,.22f, .50f,.54f,
            .28f,.26f, .24f,.42f, .22f,.57f,
            .68f,.26f, .72f,.42f, .78f,.57f,
            .37f,.55f, .34f,.75f, .32f,.95f,
            .62f,.55f, .65f,.75f, .68f,.95f),
        pose("De pie",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .24f,.38f, .22f,.54f,
            .70f,.22f, .76f,.38f, .78f,.54f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f)
    )

    MovementType.BENCH_PRESS -> listOf(
        pose("Barra al pecho",
            .50f,.20f, .50f,.30f, .50f,.58f,
            .20f,.30f, .18f,.44f, .20f,.56f,
            .80f,.30f, .82f,.44f, .80f,.56f,
            .38f,.60f, .38f,.80f, .38f,.96f,
            .62f,.60f, .62f,.80f, .62f,.96f),
        pose("Subida",
            .50f,.20f, .50f,.30f, .50f,.58f,
            .20f,.30f, .16f,.36f, .18f,.46f,
            .80f,.30f, .84f,.36f, .82f,.46f,
            .38f,.60f, .38f,.80f, .38f,.96f,
            .62f,.60f, .62f,.80f, .62f,.96f),
        pose("Extensión",
            .50f,.20f, .50f,.30f, .50f,.58f,
            .20f,.30f, .18f,.20f, .20f,.10f,
            .80f,.30f, .82f,.20f, .80f,.10f,
            .38f,.60f, .38f,.80f, .38f,.96f,
            .62f,.60f, .62f,.80f, .62f,.96f)
    )

    MovementType.OVERHEAD_PRESS -> listOf(
        pose("Barra hombros",
            .50f,.10f, .50f,.20f, .50f,.54f,
            .28f,.22f, .24f,.36f, .24f,.22f,
            .72f,.22f, .76f,.36f, .76f,.22f,
            .38f,.56f, .37f,.76f, .36f,.96f,
            .62f,.56f, .63f,.76f, .64f,.96f),
        pose("Mitad",
            .50f,.10f, .50f,.20f, .50f,.54f,
            .28f,.22f, .22f,.18f, .22f,.06f,
            .72f,.22f, .78f,.18f, .78f,.06f,
            .38f,.56f, .37f,.76f, .36f,.96f,
            .62f,.56f, .63f,.76f, .64f,.96f),
        pose("Bloqueado",
            .50f,.10f, .50f,.20f, .50f,.54f,
            .28f,.22f, .28f,.12f, .50f,.02f,
            .72f,.22f, .72f,.12f, .50f,.02f,
            .38f,.56f, .37f,.76f, .36f,.96f,
            .62f,.56f, .63f,.76f, .64f,.96f)
    )

    MovementType.PULLDOWN -> listOf(
        pose("Agarre arriba",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .20f,.12f, .18f,.06f, .30f,.03f,
            .80f,.12f, .82f,.06f, .70f,.03f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f),
        pose("Tirón",
            .50f,.10f, .50f,.20f, .50f,.54f,
            .24f,.20f, .20f,.12f, .28f,.04f,
            .76f,.20f, .80f,.12f, .72f,.04f,
            .38f,.56f, .37f,.76f, .36f,.96f,
            .62f,.56f, .63f,.76f, .64f,.96f),
        pose("Al pecho",
            .50f,.10f, .50f,.20f, .50f,.54f,
            .24f,.22f, .20f,.30f, .38f,.28f,
            .76f,.22f, .80f,.30f, .62f,.28f,
            .38f,.56f, .37f,.76f, .36f,.96f,
            .62f,.56f, .63f,.76f, .64f,.96f)
    )

    MovementType.ROW -> listOf(
        pose("Inclinado",
            .42f,.20f, .42f,.28f, .50f,.54f,
            .25f,.26f, .22f,.40f, .22f,.56f,
            .58f,.28f, .60f,.42f, .62f,.58f,
            .38f,.56f, .34f,.74f, .32f,.94f,
            .60f,.56f, .64f,.74f, .68f,.94f),
        pose("Tirón",
            .42f,.20f, .42f,.28f, .50f,.54f,
            .25f,.26f, .22f,.32f, .26f,.40f,
            .58f,.28f, .60f,.32f, .58f,.42f,
            .38f,.56f, .34f,.74f, .32f,.94f,
            .60f,.56f, .64f,.74f, .68f,.94f),
        pose("Codo atrás",
            .42f,.20f, .42f,.28f, .50f,.54f,
            .25f,.26f, .20f,.28f, .24f,.36f,
            .58f,.28f, .62f,.28f, .60f,.36f,
            .38f,.56f, .34f,.74f, .32f,.94f,
            .60f,.56f, .64f,.74f, .68f,.94f)
    )

    MovementType.CURL -> listOf(
        pose("Brazos abajo",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .24f,.40f, .22f,.58f,
            .70f,.22f, .76f,.40f, .78f,.58f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f),
        pose("A 90°",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .22f,.36f, .22f,.26f,
            .70f,.22f, .78f,.36f, .78f,.26f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f),
        pose("Curl completo",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .22f,.28f, .30f,.16f,
            .70f,.22f, .78f,.28f, .70f,.16f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f)
    )

    MovementType.TRICEP_EXT -> listOf(
        pose("Codos arriba",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .26f,.12f, .34f,.04f,
            .70f,.22f, .74f,.12f, .66f,.04f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f),
        pose("Doblado",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .26f,.12f, .26f,.22f,
            .70f,.22f, .74f,.12f, .74f,.22f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f),
        pose("Extensión",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .26f,.12f, .34f,.04f,
            .70f,.22f, .74f,.12f, .66f,.04f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f)
    )

    MovementType.PLANK -> listOf(
        pose("Posición",
            .14f,.38f, .22f,.40f, .68f,.50f,
            .24f,.36f, .24f,.50f, .24f,.64f,
            .22f,.36f, .22f,.50f, .22f,.64f,
            .60f,.50f, .72f,.52f, .84f,.56f,
            .64f,.50f, .76f,.52f, .88f,.56f),
        pose("Core activo",
            .14f,.34f, .22f,.36f, .68f,.46f,
            .24f,.32f, .24f,.46f, .24f,.60f,
            .22f,.32f, .22f,.46f, .22f,.60f,
            .60f,.46f, .72f,.48f, .84f,.52f,
            .64f,.46f, .76f,.48f, .88f,.52f),
        pose("Mantén",
            .14f,.38f, .22f,.40f, .68f,.50f,
            .24f,.36f, .24f,.50f, .24f,.64f,
            .22f,.36f, .22f,.50f, .22f,.64f,
            .60f,.50f, .72f,.52f, .84f,.56f,
            .64f,.50f, .76f,.52f, .88f,.56f)
    )

    MovementType.CRUNCH -> listOf(
        pose("Tumbado",
            .50f,.28f, .50f,.38f, .50f,.64f,
            .26f,.34f, .26f,.48f, .34f,.60f,
            .74f,.34f, .74f,.48f, .66f,.60f,
            .40f,.66f, .38f,.82f, .36f,.97f,
            .60f,.66f, .62f,.82f, .64f,.97f),
        pose("Sube",
            .50f,.20f, .50f,.30f, .50f,.58f,
            .26f,.26f, .30f,.38f, .38f,.48f,
            .74f,.26f, .70f,.38f, .62f,.48f,
            .40f,.60f, .38f,.78f, .36f,.96f,
            .60f,.60f, .62f,.78f, .64f,.96f),
        pose("Contracción",
            .50f,.16f, .50f,.26f, .50f,.54f,
            .26f,.22f, .32f,.30f, .40f,.40f,
            .74f,.22f, .68f,.30f, .60f,.40f,
            .40f,.56f, .38f,.74f, .36f,.94f,
            .60f,.56f, .62f,.74f, .64f,.94f)
    )

    MovementType.HIP_THRUST -> listOf(
        pose("Posición baja",
            .50f,.26f, .50f,.36f, .50f,.62f,
            .28f,.34f, .26f,.48f, .28f,.60f,
            .72f,.34f, .74f,.48f, .72f,.60f,
            .38f,.64f, .34f,.82f, .30f,.97f,
            .62f,.64f, .66f,.82f, .70f,.97f),
        pose("Empuje",
            .50f,.20f, .50f,.30f, .50f,.54f,
            .28f,.28f, .26f,.42f, .28f,.56f,
            .72f,.28f, .74f,.42f, .72f,.56f,
            .38f,.56f, .36f,.78f, .32f,.96f,
            .62f,.56f, .64f,.78f, .68f,.96f),
        pose("Arriba",
            .50f,.16f, .50f,.26f, .50f,.46f,
            .28f,.24f, .26f,.38f, .28f,.52f,
            .72f,.24f, .74f,.38f, .72f,.52f,
            .38f,.48f, .36f,.72f, .32f,.94f,
            .62f,.48f, .64f,.72f, .68f,.94f)
    )

    MovementType.LUNGE -> listOf(
        pose("De pie",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .24f,.38f, .20f,.52f,
            .70f,.22f, .76f,.38f, .80f,.52f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f),
        pose("Paso adelante",
            .44f,.14f, .44f,.24f, .46f,.56f,
            .26f,.26f, .20f,.40f, .18f,.54f,
            .64f,.26f, .68f,.38f, .70f,.52f,
            .34f,.58f, .28f,.74f, .22f,.92f,
            .58f,.58f, .66f,.78f, .72f,.96f),
        pose("Rodilla abajo",
            .42f,.18f, .42f,.28f, .44f,.58f,
            .24f,.28f, .18f,.40f, .16f,.54f,
            .62f,.28f, .66f,.40f, .68f,.54f,
            .32f,.60f, .22f,.76f, .18f,.94f,
            .58f,.60f, .62f,.80f, .68f,.96f)
    )

    MovementType.LATERAL_RAISE -> listOf(
        pose("Brazos abajo",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .26f,.38f, .22f,.54f,
            .70f,.22f, .74f,.38f, .78f,.54f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f),
        pose("A 45°",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .18f,.30f, .08f,.40f,
            .70f,.22f, .82f,.30f, .92f,.40f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f),
        pose("A 90°",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .10f,.22f, .02f,.22f,
            .70f,.22f, .90f,.22f, .98f,.22f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f)
    )

    MovementType.CALF_RAISE -> listOf(
        pose("Talones abajo",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .24f,.38f, .20f,.52f,
            .70f,.22f, .76f,.38f, .80f,.52f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f),
        pose("Subida",
            .50f,.06f, .50f,.16f, .50f,.50f,
            .30f,.20f, .24f,.36f, .20f,.50f,
            .70f,.20f, .76f,.36f, .80f,.50f,
            .38f,.52f, .37f,.72f, .38f,.90f,
            .62f,.52f, .63f,.72f, .62f,.90f),
        pose("Puntillas",
            .50f,.04f, .50f,.14f, .50f,.48f,
            .30f,.18f, .24f,.34f, .20f,.48f,
            .70f,.18f, .76f,.34f, .80f,.48f,
            .38f,.50f, .37f,.70f, .40f,.88f,
            .62f,.50f, .63f,.70f, .60f,.88f)
    )

    MovementType.LEG_PRESS, MovementType.LEG_EXT, MovementType.LEG_CURL -> listOf(
        pose("Piernas dobladas",
            .50f,.10f, .50f,.20f, .50f,.50f,
            .30f,.22f, .26f,.36f, .30f,.48f,
            .70f,.22f, .74f,.36f, .70f,.48f,
            .36f,.52f, .26f,.68f, .30f,.84f,
            .64f,.52f, .74f,.68f, .70f,.84f),
        pose("Empuje",
            .50f,.10f, .50f,.20f, .50f,.50f,
            .30f,.22f, .26f,.36f, .30f,.48f,
            .70f,.22f, .74f,.36f, .70f,.48f,
            .36f,.52f, .22f,.58f, .14f,.68f,
            .64f,.52f, .78f,.58f, .86f,.68f),
        pose("Extensión",
            .50f,.10f, .50f,.20f, .50f,.50f,
            .30f,.22f, .26f,.36f, .30f,.48f,
            .70f,.22f, .74f,.36f, .70f,.48f,
            .36f,.52f, .20f,.52f, .10f,.56f,
            .64f,.52f, .80f,.52f, .90f,.56f)
    )

    MovementType.CARDIO -> listOf(
        pose("Impulso",
            .52f,.08f, .52f,.18f, .54f,.50f,
            .34f,.22f, .28f,.36f, .32f,.50f,
            .70f,.22f, .76f,.30f, .72f,.42f,
            .42f,.52f, .50f,.72f, .44f,.92f,
            .64f,.52f, .60f,.68f, .66f,.86f),
        pose("Vuelo",
            .50f,.06f, .50f,.16f, .52f,.48f,
            .32f,.20f, .20f,.28f, .18f,.42f,
            .72f,.20f, .84f,.24f, .82f,.36f,
            .40f,.50f, .32f,.68f, .26f,.88f,
            .64f,.50f, .68f,.70f, .74f,.90f),
        pose("Aterrizaje",
            .50f,.10f, .50f,.20f, .52f,.50f,
            .34f,.22f, .28f,.36f, .22f,.50f,
            .70f,.22f, .76f,.32f, .82f,.44f,
            .40f,.52f, .36f,.72f, .32f,.92f,
            .64f,.52f, .68f,.72f, .72f,.92f)
    )

    else -> listOf(
        pose("Inicio",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .24f,.38f, .20f,.52f,
            .70f,.22f, .76f,.38f, .80f,.52f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f),
        pose("Movimiento",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .20f,.28f, .16f,.40f,
            .70f,.22f, .80f,.28f, .84f,.40f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f),
        pose("Final",
            .50f,.08f, .50f,.18f, .50f,.52f,
            .30f,.22f, .24f,.38f, .20f,.52f,
            .70f,.22f, .76f,.38f, .80f,.52f,
            .38f,.54f, .37f,.76f, .36f,.96f,
            .62f,.54f, .63f,.76f, .64f,.96f)
    )
}

@Composable
fun ExerciseStepsCard(
    exerciseName: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val poses = getPoses(exerciseNameToMovement(exerciseName))

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Técnica paso a paso",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                poses.forEachIndexed { idx, p ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.65f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color.copy(alpha = 0.08f))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawPose(p, color, size.width * 0.055f, size.width * 0.082f)
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color.copy(alpha = 0.18f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    "${idx + 1}",
                                    color = color,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            p.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
