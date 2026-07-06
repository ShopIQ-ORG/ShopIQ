//
//  ExchangeTrendChart.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.profile.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExchangeTrendChart(
    history: List<Pair<String, Double>>,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    if (history.isEmpty()) return

    val maxVal = history.maxOf { it.second }
    val minVal = history.minOf { it.second }
    val valRange = (maxVal - minVal).coerceAtLeast(0.01)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val width = size.width
            val height = size.height

            // Padding constants
            val padLeft = 40f
            val padRight = 20f
            val padTop = 30f
            val padBottom = 40f

            val plotWidth = width - padLeft - padRight
            val plotHeight = height - padTop - padBottom

            // Draw horizontal grid lines
            val gridLinesCount = 3
            for (i in 0..gridLinesCount) {
                val y = padTop + (plotHeight / gridLinesCount) * i
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(padLeft, y),
                    end = Offset(width - padRight, y),
                    strokeWidth = 2f
                )
            }

            // Generate plot points coordinates
            val points = history.mapIndexed { idx, pair ->
                val x = padLeft + (plotWidth / (history.size - 1)) * idx
                val normalizedY = (pair.second - minVal) / valRange
                val y = padTop + plotHeight - (normalizedY * plotHeight).toFloat()
                Offset(x, y)
            }

            // Draw Path (Line Chart)
            val chartPath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        // Draw smooth cubic curves between points
                        val prev = points[i - 1]
                        val curr = points[i]
                        val cp1x = prev.x + (curr.x - prev.x) / 2
                        val cp1y = prev.y
                        val cp2x = prev.x + (curr.x - prev.x) / 2
                        val cp2y = curr.y
                        cubicTo(cp1x, cp1y, cp2x, cp2y, curr.x, curr.y)
                    }
                }
            }

            drawPath(
                path = chartPath,
                color = primaryColor,
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )

            // Draw Gradient Fill under path
            if (points.isNotEmpty()) {
                val fillPath = Path().apply {
                    addPath(chartPath)
                    lineTo(points.last().x, padTop + plotHeight)
                    lineTo(points.first().x, padTop + plotHeight)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.24f),
                            primaryColor.copy(alpha = 0.0f)
                        ),
                        startY = points.minOf { it.y },
                        endY = padTop + plotHeight
                    )
                )
            }

            // Draw circles on point nodes
            points.forEach { point ->
                drawCircle(
                    color = primaryColor,
                    radius = 10f,
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = point
                )
            }
        }

        // Days labels row below
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            history.forEach { pair ->
                Text(
                    text = pair.first,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExchangeTrendChartPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ExchangeTrendChart(
                history = listOf(
                    "Mon" to 48.1,
                    "Tue" to 47.9,
                    "Wed" to 48.2,
                    "Thu" to 48.0,
                    "Fri" to 48.3,
                    "Sat" to 48.1,
                    "Sun" to 48.0
                ),
                primaryColor = Color(0xFF6F32E5)
            )
        }
    }
}
