package org.strawberryfoundations.reply.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.filled.Weight
import org.strawberryfoundations.material.symbols.outlined.Delete
import org.strawberryfoundations.material.symbols.outlined.Edit
import org.strawberryfoundations.reply.R
import org.strawberryfoundations.reply.core.AppSettings
import org.strawberryfoundations.reply.room.entities.Exercise
import org.strawberryfoundations.reply.room.entities.SessionStatus
import org.strawberryfoundations.reply.room.entities.WorkoutSession
import org.strawberryfoundations.reply.room.entities.WorkoutSet
import org.strawberryfoundations.reply.room.entities.getExerciseGroupEmoji
import org.strawberryfoundations.reply.room.entities.getExerciseGroupStringResource
import org.strawberryfoundations.reply.room.viewmodels.ExerciseViewModel
import org.strawberryfoundations.reply.room.viewmodels.WorkoutSessionViewModel
import org.strawberryfoundations.reply.service.SessionManager
import org.strawberryfoundations.reply.ui.composable.DeleteExerciseDialog
import org.strawberryfoundations.reply.ui.composable.EditExerciseDialog
import org.strawberryfoundations.reply.ui.composable.ExerciseProgressGraph
import org.strawberryfoundations.reply.ui.composable.ExerciseVolumeGraph
import org.strawberryfoundations.reply.ui.composable.StatCard
import org.strawberryfoundations.reply.ui.composable.ToolbarAction

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExerciseDetail(
    exercise: Exercise,
    exerciseViewModel: ExerciseViewModel = viewModel(),
    sessionViewModel: WorkoutSessionViewModel = viewModel(),
    onStartTraining: (Exercise) -> Unit,
    settings: AppSettings,
    onExerciseDelete: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    var showEditSheet by remember { mutableStateOf(false) }
    var startTraining by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }
    var trainingToDelete by remember { mutableStateOf<Exercise?>(null) }

    val workoutSessions = sessionViewModel.allSessions.collectAsState(initial = emptyList())
    val exerciseSessions = workoutSessions.value.filter { it.exerciseId == exercise.id }
    val completedSessions = exerciseSessions.filter { it.status == SessionStatus.COMPLETED }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Exercise name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = MaterialShapes.VerySunny.toShape(),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = getExerciseGroupEmoji(exercise.group),
                                fontSize = 20.sp
                            )
                        }
                    }

                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 24.sp,
                        lineHeight = 25.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Alternative name
            if (!exercise.altName.isNullOrEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Label,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = exercise.altName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Exercise group
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Layers,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = getExerciseGroupStringResource(exercise.group),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            if (exercise.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = exercise.note,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            if (completedSessions.isNotEmpty()) {
                val bestWeight = completedSessions.maxOf { it.currentWeight }
                val lastSession = completedSessions.maxByOrNull { it.startedAt }
                val avgWeight = completedSessions.map { it.currentWeight }.average()
                val totalSets = completedSessions.sumOf { it.setsCompleted }
                val totalSessions = completedSessions.size
                
                // Calculate total volume (weight × reps × sets)
                val totalVolume = completedSessions.sumOf { session ->
                    val sets = try {
                        Json.decodeFromString<List<WorkoutSet>>(session.setsHistory)
                    } catch (e: Exception) {
                        emptyList()
                    }
                    sets.sumOf { workoutSet ->
                        (workoutSet.weight * workoutSet.reps).toLong()
                    }
                }.toDouble()
                val avgVolume = if (totalSessions > 0) totalVolume / totalSessions else 0.0
                
                val recentSessions = completedSessions.sortedByDescending { it.startedAt }.take(5)
                val trend = if (recentSessions.size >= 2) {
                    val newest = recentSessions.first().currentWeight
                    val oldest = recentSessions.last().currentWeight
                    when {
                        newest > oldest -> "📈 ${stringResource(R.string.trend_rising)}"
                        newest < oldest -> "📉 ${stringResource(R.string.trend_falling)}"
                        else -> "➡️ ${stringResource(R.string.trend_stable)}"
                    }
                } else {
                    "➖ ${stringResource(R.string.trend_insufficient_data)}"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.best_performance),
                        value = "%.1f kg".format(bestWeight),
                        icon = "🏆",
                        modifier = Modifier.weight(1f),
                        shapeColor = Color(0xFFD77F10)
                    )
                    StatCard(
                        label = stringResource(R.string.last_session),
                        value = "%.1f kg".format(lastSession?.currentWeight ?: exercise.weight),
                        icon = "⏱️",
                        modifier = Modifier.weight(1f),
                        shapeColor = Color(0xFF4CAF50)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.average),
                        value = "%.1f kg".format(avgWeight),
                        icon = "📊",
                        modifier = Modifier.weight(1f),
                        shapeColor = Color(0xFF2196F3)
                    )
                    StatCard(
                        label = stringResource(R.string.trend),
                        value = trend.substringAfter(" "),
                        icon = trend.substringBefore(" "),
                        modifier = Modifier.weight(1f),
                        shapeColor = Color(0xFF9C27B0)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.total_sessions),
                        value = "$totalSessions",
                        icon = "🎯",
                        modifier = Modifier.weight(1f),
                        shapeColor = Color(0xFFFF5722)
                    )
                    StatCard(
                        label = stringResource(R.string.total_sets),
                        value = "$totalSets",
                        icon = "💪",
                        modifier = Modifier.weight(1f),
                        shapeColor = Color(0xFF00BCD4)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.total_volume),
                        value = "%.0f kg".format(totalVolume),
                        icon = "📦",
                        modifier = Modifier.weight(1f),
                        shapeColor = Color(0xFFE91E63)
                    )
                    StatCard(
                        label = stringResource(R.string.avg_volume),
                        value = "%.0f kg".format(avgVolume),
                        icon = "📊",
                        modifier = Modifier.weight(1f),
                        shapeColor = Color(0xFF3F51B5)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.best_performance),
                        value = "- kg",
                        icon = "🏆",
                        modifier = Modifier.weight(1f),
                        shapeColor = Color(0xFFD77F10)
                    )
                    StatCard(
                        label = stringResource(R.string.last_performance),
                        value = "${exercise.weight ?: 0.0} kg",
                        icon = "⏱️",
                        modifier = Modifier.weight(1f),
                        shapeColor = Color(0xFF4CAF50)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.weight_progress),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExerciseProgressGraph(
                exerciseSessions = exerciseSessions
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.volume_progress),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExerciseVolumeGraph(
                exerciseSessions = exerciseSessions
            )

            Spacer(modifier = Modifier.height(120.dp))
        }

        HorizontalFloatingToolbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = -ScreenOffset)
                .zIndex(1f),
            expanded = true,
            colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
            expandedShadowElevation = 6.dp,
            floatingActionButton = {
                // Start exercise button
                TooltipBox(
                    positionProvider =
                        TooltipDefaults.rememberTooltipPositionProvider(
                            positioning = TooltipAnchorPosition.Above
                        ),
                    tooltip = { PlainTooltip { Text(text = stringResource(R.string.start_exercise)) } },
                    state = rememberTooltipState(),
                ) {
                    FloatingToolbarDefaults.VibrantFloatingActionButton(
                        onClick = {
                            startTraining = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = stringResource(R.string.start_exercise)
                        )
                    }
                }
            }
        ) {
            // Delete
            ToolbarAction(
                icon = MaterialSymbols.Outlined.Delete,
                description = stringResource(R.string.delete_training),
                onClick = {
                    trainingToDelete = exercise
                    showDeleteDialog = true
                    if (settings.useHapticFeedback) {
                        haptic.performHapticFeedback(HapticFeedbackType.Reject)
                    }
                },
                tint = MaterialTheme.colorScheme.error
            )

            // History
            ToolbarAction(
                icon = Icons.Rounded.History,
                description = stringResource(R.string.show_history),
                onClick = { showHistorySheet = true }
            )

            /* Statistics
            ToolbarAction(
                icon = Icons.Rounded.BarChart,
                description = stringResource(R.string.statistics),
                onClick = { }
            ) */

            // Edit
            ToolbarAction(
                icon = MaterialSymbols.Outlined.Edit,
                description = stringResource(R.string.edit_exercise),
                onClick = { showEditSheet = true }
            )
        }
    }

    if (startTraining) {
        ModalBottomSheet(
            onDismissRequest = {
                startTraining = false
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            StartTrainingContent(
                exercise = exercise,
                onStartConfirm = { exerciseToStart, weight ->
                    onStartTraining(exerciseToStart)
                    startTraining = false
                }
            )
        }
    }

    if (showEditSheet) {
        EditExerciseDialog(
            exercise = exercise,
            onSave = { updatedExercise ->
                exerciseViewModel.update(updatedExercise)
                showEditSheet = false
            },
            onDismiss = { showEditSheet = false },
            settings = settings
        )
    }

    if (showDeleteDialog && trainingToDelete != null) {
        DeleteExerciseDialog(
            exercise = trainingToDelete!!,
            onConfirm = {
                exerciseViewModel.delete(trainingToDelete!!)
                onExerciseDelete()
                        },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            ExerciseHistoryContent(
                exercise = exercise,
                sessions = exerciseSessions,
                sessionViewModel = sessionViewModel,
                onDismiss = { showHistorySheet = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StartTrainingContent(
    exercise: Exercise,
    onStartConfirm: (Exercise, Double) -> Unit
) {
    val context = LocalContext.current
    var currentWeight by remember { mutableDoubleStateOf(exercise.weight ?: 0.0) }
    var showCountdown by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = showCountdown,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f) togetherWith
                    fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
        },
        label = "countdown_transition"
    ) { isCountdown ->
        if (isCountdown) {
            SessionManager.startSession(
                context = context,
                exerciseId = exercise.id,
                weight = currentWeight
            )
            onStartConfirm(exercise, currentWeight)

            /* CountdownView(
                onCountdownFinish = {
                    // Starte den WorkoutService
                    org.strawberryfoundations.reply.service.SessionManager.startSession(
                        context = context,
                        exerciseId = exercise.id,
                        weight = currentWeight
                    )
                    onStartConfirm(exercise, currentWeight)
                }
            ) */
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.start_training_question),
                    style = MaterialTheme.typography.displayMedium,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = MaterialShapes.VerySunny.toShape(),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = getExerciseGroupEmoji(exercise.group),
                            fontSize = 20.sp
                        )
                    }
                }

                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = MaterialSymbols.Filled.Weight,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "${exercise.weight} ${exercise.weightUnit}",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 26.sp
                        )
                    }
                }


                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { showCountdown = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.start_session),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CountdownView(
    onCountdownFinish: () -> Unit,
    startSeconds: Int = 3
) {
    var countdown by remember { mutableIntStateOf(startSeconds) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        repeat(startSeconds) {
            countdown = startSeconds - it
            rotation.animateTo(
                targetValue = rotation.value + 90f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                )
            )
        }
        countdown = 0
        delay(300)
        onCountdownFinish()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp, top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            Surface(
                shape = MaterialShapes.Cookie9Sided.toShape(),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(180.dp)
                    .rotate(rotation.value)
            ) {}

            AnimatedContent(
                targetState = countdown,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) + scaleIn(
                        initialScale = 0.5f,
                        animationSpec = tween(200)
                    ) togetherWith
                            fadeOut(animationSpec = tween(200)) + scaleOut(
                        targetScale = 1.5f,
                        animationSpec = tween(200)
                    )
                },
                label = "countdown_number"
            ) { count ->
                Text(
                    text = if (count > 0) count.toString() else stringResource(R.string.go),
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = if (count > 0) 72.sp else 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.get_ready),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}





