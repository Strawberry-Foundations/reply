package org.strawberryfoundations.reply.ui.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.json.Json
import org.strawberryfoundations.reply.R
import org.strawberryfoundations.reply.room.entities.SessionStatus
import org.strawberryfoundations.reply.room.entities.WorkoutSession
import org.strawberryfoundations.reply.room.entities.WorkoutSet

@Composable
fun ExerciseVolumeGraph(
    exerciseSessions: List<WorkoutSession>
) {
    val completedSessions = exerciseSessions
        .filter { it.status == SessionStatus.COMPLETED }
        .sortedBy { it.startedAt }
        .map { session ->
            val sets = try {
                Json.decodeFromString<List<WorkoutSet>>(session.setsHistory)
            } catch (e: Exception) {
                emptyList()
            }
            val volume = sets.sumOf { it.weight * it.reps }
            session to volume
        }

    val primaryColor = MaterialTheme.colorScheme.tertiary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val textMeasurer = rememberTextMeasurer()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(24.dp)
    ) {
        if (completedSessions.size < 2) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📦",
                        fontSize = 48.sp
                    )
                    Text(
                        text = stringResource(R.string.complete_two_trainings),
                        style = MaterialTheme.typography.bodyMedium,
                        color = onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.to_see_progress),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurfaceVariant
                    )
                }
            }
        } else {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 48.dp, end = 24.dp, top = 32.dp, bottom = 40.dp)
            ) {
                val minVolume = completedSessions.minOf { it.second }.toFloat()
                val maxVolume = completedSessions.maxOf { it.second }.toFloat()
                val volumeRange = (maxVolume - minVolume).coerceAtLeast(1f)
                val paddedMin = (minVolume - volumeRange * 0.1f).coerceAtLeast(0f)
                val paddedMax = maxVolume + volumeRange * 0.1f
                val paddedRange = paddedMax - paddedMin

                val gridLines = 5
                for (i in 0..gridLines) {
                    val y = size.height * (i.toFloat() / gridLines)
                    val volume = paddedMax - (paddedRange * (i.toFloat() / gridLines))

                    drawLine(
                        color = onSurfaceVariant.copy(alpha = 0.15f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )

                    val volumeText = "%.0f".format(volume)
                    val textLayoutResult = textMeasurer.measure(
                        text = volumeText,
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = onSurfaceVariant
                        )
                    )
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(
                            x = -textLayoutResult.size.width - 8.dp.toPx(),
                            y = y - textLayoutResult.size.height / 2
                        )
                    )
                }

                val points = completedSessions.mapIndexed { index, (_, volume) ->
                    val x = size.width * (index.toFloat() / (completedSessions.size - 1))
                    val normalizedVolume = (volume.toFloat() - paddedMin) / paddedRange
                    val y = size.height * (1 - normalizedVolume)
                    Offset(x, y)
                }

                val fillPath = Path().apply {
                    moveTo(points.first().x, size.height)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(points.last().x, size.height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.3f),
                            primaryColor.copy(alpha = 0.05f)
                        )
                    )
                )

                val path = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }

                drawPath(
                    path = path,
                    color = primaryColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                points.forEachIndexed { index, point ->
                    val volume = completedSessions[index].second

                    drawCircle(
                        color = primaryColor,
                        radius = 6.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = point
                    )

                    val volumeText = "%.0f".format(volume)
                    val valueLayoutResult = textMeasurer.measure(
                        text = volumeText,
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    drawText(
                        textLayoutResult = valueLayoutResult,
                        topLeft = Offset(
                            x = point.x - valueLayoutResult.size.width / 2,
                            y = point.y - valueLayoutResult.size.height - 12.dp.toPx()
                        )
                    )
                }

                completedSessions.forEachIndexed { index, _ ->
                    if (index % maxOf(1, completedSessions.size / 6) == 0 || index == completedSessions.size - 1) {
                        val x = size.width * (index.toFloat() / (completedSessions.size - 1))
                        val labelText = "#${index + 1}"
                        val labelLayoutResult = textMeasurer.measure(
                            text = labelText,
                            style = TextStyle(
                                fontSize = 10.sp,
                                color = onSurfaceVariant
                            )
                        )
                        drawText(
                            textLayoutResult = labelLayoutResult,
                            topLeft = Offset(
                                x = x - labelLayoutResult.size.width / 2,
                                y = size.height + 8.dp.toPx()
                            )
                        )
                    }
                }

                val yAxisLabel = "kg"
                val yLabelResult = textMeasurer.measure(
                    text = yAxisLabel,
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                )
                drawText(
                    textLayoutResult = yLabelResult,
                    topLeft = Offset(
                        x = -yLabelResult.size.width - 8.dp.toPx(),
                        y = -20.dp.toPx()
                    )
                )
            }
        }
    }
}