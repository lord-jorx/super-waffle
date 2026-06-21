package com.lordj.fitnessapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lordj.fitnessapp.ui.viewmodel.DayProgress

@Composable
fun WeightProgressChart(
    data: List<DayProgress>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    if (data.isEmpty()) {
        Box(modifier.height(180.dp), contentAlignment = Alignment.Center) {
            Text("Sin datos aún", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        return
    }

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(fontSize = 10.sp, color = textColor)
    val paddingLeft = 48.dp
    val paddingBottom = 32.dp

    Canvas(modifier = modifier.height(180.dp).padding(end = 8.dp)) {
        val w = size.width - paddingLeft.toPx()
        val h = size.height - paddingBottom.toPx()
        val maxVal = data.maxOf { it.maxWeightKg }.coerceAtLeast(1.0)
        val minVal = (data.minOf { it.maxWeightKg } * 0.9).coerceAtLeast(0.0)
        val range = (maxVal - minVal).coerceAtLeast(1.0)

        // Grid lines
        repeat(5) { i ->
            val y = h * i / 4f
            drawLine(gridColor, Offset(paddingLeft.toPx(), y), Offset(size.width, y), strokeWidth = 1f)
            val value = maxVal - (range * i / 4)
            val label = "${value.toInt()} kg"
            drawText(textMeasurer, label, Offset(0f, y - 8.dp.toPx()), labelStyle)
        }

        // Data line and fill
        val points = data.mapIndexed { i, d ->
            val x = paddingLeft.toPx() + w * i / (data.size - 1).coerceAtLeast(1)
            val y = h - h * ((d.maxWeightKg - minVal) / range).toFloat()
            Offset(x, y)
        }

        if (points.size >= 2) {
            val fillPath = Path().apply {
                moveTo(points.first().x, h)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, h)
                close()
            }
            drawPath(fillPath, Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent),
                startY = 0f, endY = h
            ))
            for (i in 0 until points.size - 1) {
                drawLine(lineColor, points[i], points[i + 1], strokeWidth = 3f, cap = StrokeCap.Round)
            }
        }

        // Dots and x-labels
        val showEvery = (data.size / 5).coerceAtLeast(1)
        points.forEachIndexed { i, pt ->
            drawCircle(lineColor, radius = 5f, center = pt)
            drawCircle(Color.White, radius = 3f, center = pt)
            if (i % showEvery == 0) {
                drawText(textMeasurer, data[i].dateLabel,
                    Offset(pt.x - 12.dp.toPx(), h + 4.dp.toPx()), labelStyle)
            }
        }
    }
}

@Composable
fun VolumeBarChart(
    data: List<DayProgress>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.secondary,
    gridColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    if (data.isEmpty()) {
        Box(modifier.height(160.dp), contentAlignment = Alignment.Center) {
            Text("Sin datos aún", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        return
    }

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(fontSize = 10.sp, color = textColor)
    val paddingLeft = 48.dp
    val paddingBottom = 32.dp

    Canvas(modifier = modifier.height(160.dp).padding(end = 8.dp)) {
        val w = size.width - paddingLeft.toPx()
        val h = size.height - paddingBottom.toPx()
        val maxVol = data.maxOf { it.totalVolume }.coerceAtLeast(1.0)

        repeat(4) { i ->
            val y = h * i / 3f
            drawLine(gridColor, Offset(paddingLeft.toPx(), y), Offset(size.width, y), 1f)
            val label = "${(maxVol * (3 - i) / 3).toInt()}"
            drawText(textMeasurer, label, Offset(0f, y - 8.dp.toPx()), labelStyle)
        }

        val barWidth = (w / data.size * 0.6f)
        val gap = w / data.size
        data.forEachIndexed { i, d ->
            val barH = (h * d.totalVolume / maxVol).toFloat()
            val x = paddingLeft.toPx() + gap * i + gap * 0.2f
            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, h - barH),
                size = androidx.compose.ui.geometry.Size(barWidth, barH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
            )
            val showEvery = (data.size / 5).coerceAtLeast(1)
            if (i % showEvery == 0) {
                drawText(textMeasurer, d.dateLabel, Offset(x - 4.dp.toPx(), h + 4.dp.toPx()), labelStyle)
            }
        }
    }
}
