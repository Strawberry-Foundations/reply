package org.strawberryfoundations.reply.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.default.Check
import org.strawberryfoundations.material.symbols.filled.Weight
import org.strawberryfoundations.reply.R
import org.strawberryfoundations.reply.core.AppSettings
import org.strawberryfoundations.reply.room.ExerciseViewModel
import org.strawberryfoundations.reply.room.entities.SessionStatus
import org.strawberryfoundations.reply.room.entities.WorkoutSet
import org.strawberryfoundations.reply.room.entities.getExerciseGroupEmoji
import org.strawberryfoundations.reply.service.SessionManager
import org.strawberryfoundations.reply.ui.composable.RepsInputDialog
import org.strawberryfoundations.reply.ui.composable.StopActiveExerciseDialog
import org.strawberryfoundations.reply.ui.composable.ToolbarAction
import org.strawberryfoundations.reply.ui.theme.customFont
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActiveExercise(
    sessionId: Long,
    settings: AppSettings,
    viewModel: ExerciseViewModel = viewModel(),
    onSessionComplete: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    
    val session by SessionManager.currentSession.collectAsState()
    val elapsedSeconds by SessionManager.elapsedSeconds.collectAsState()
    val restTimeRemaining by SessionManager.restTimeRemaining.collectAsState()
    
    val exercises by viewModel.trainings.collectAsState()
    val currentExercise = remember(session, exercises) {
        session?.exerciseId?.let { id -> exercises.firstOrNull { it.id == id } }
    }
    
    var showRepsDialog by remember { mutableStateOf(false) }
    var showStopDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        SessionManager.bindService(context)
    }

    LaunchedEffect(session?.status) {
        if (session?.status == SessionStatus.COMPLETED || session?.status == SessionStatus.CANCELLED) {
            delay(500)
            onSessionComplete()
        }
    }
    
    if (session == null || currentExercise == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    
    val isPaused = session?.status == SessionStatus.PAUSED
    val isResting = session?.isResting == true
    
    val setsHistory = remember(session?.setsHistory) {
        try {
            Json.decodeFromString<List<WorkoutSet>>(session?.setsHistory ?: "[]")
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = MaterialShapes.VerySunny.toShape(),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = getExerciseGroupEmoji(currentExercise.group),
                                    fontSize = 20.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = currentExercise.name,
                            style = MaterialTheme.typography.displayMedium,
                            fontSize = 24.sp
                        )
                    }

                },
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Timer Display
                TimerCard(
                    elapsedSeconds = elapsedSeconds,
                    isPaused = isPaused,
                    onPauseResume = {
                        if (settings.useHapticFeedback) {
                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        }
                        if (isPaused) {
                            SessionManager.resumeSession(context)
                        } else {
                            SessionManager.pauseSession(context)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                WeightCard(
                    currentWeight = session?.currentWeight ?: 0.0,
                    onWeightChange = { newWeight ->
                        if (settings.useHapticFeedback) {
                            haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                        }
                        SessionManager.updateWeight(newWeight)
                    },
                    settings = settings
                )

                Spacer(modifier = Modifier.height(16.dp))

                /* RestTimerCard(
                    restTimeRemaining = restTimeRemaining,
                ) */

                SetsHistoryCard(
                    sets = setsHistory,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(92.dp))
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
                    // Add rep button
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
                                showRepsDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = MaterialSymbols.Default.Check,
                                contentDescription = stringResource(R.string.start_exercise)
                            )
                        }
                    }
                }
            ) {
                // Stop training
                ToolbarAction(
                    icon = Icons.Rounded.Stop,
                    description = stringResource(R.string.stop_training),
                    onClick = {
                        if (settings.useHapticFeedback) {
                            haptic.performHapticFeedback(HapticFeedbackType.Reject)
                        }

                        showStopDialog = true
                    },
                    tint = MaterialTheme.colorScheme.error
                )

                // Pause / Continue
                ToolbarAction(
                    icon = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    description = if (isPaused) stringResource(R.string.s_continue) else stringResource(R.string.pause),
                    onClick = {
                        if (settings.useHapticFeedback) {
                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        }
                        if (isPaused) {
                            SessionManager.resumeSession(context)
                        } else {
                            SessionManager.pauseSession(context)
                        }
                    }
                )
            }
        }
    }
    
    // Reps Input Dialog
    if (showRepsDialog) {
        RepsInputDialog(
            onDismiss = { showRepsDialog = false },
            onConfirm = { reps ->
                if (settings.useHapticFeedback) {
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                }
                SessionManager.completeSet(reps)
                showRepsDialog = false
                SessionManager.startRest(90)
            }
        )
    }
    
    // Stop Confirmation Dialog
    if (showStopDialog) {
        StopActiveExerciseDialog(
            context = context,
            onConfirm = { onSessionComplete() },
            onDismiss = { showStopDialog = false }
        )
    }
}

@Composable
private fun TimerCard(
    elapsedSeconds: Long,
    isPaused: Boolean,
    onPauseResume: () -> Unit
) {
    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60
    
    val timeString = if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.training_time),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedContent(
                targetState = isPaused,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (it) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Text(
                        text = timeString,
                        style = customFont.numeralMedium,
                        fontSize = 56.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

            }
        }
    }
}

@Composable
private fun RestTimerCard(restTimeRemaining: Int) {
    val progress = remember(restTimeRemaining) {
        (restTimeRemaining / 90f).coerceIn(0f, 1f)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.rest),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${restTimeRemaining}s",
                style = customFont.numeralMedium,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.tertiaryContainer,
            )
        }
    }
}

@Composable
private fun WeightCard(
    currentWeight: Double,
    onWeightChange: (Double) -> Unit,
    settings: AppSettings
) {
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = MaterialSymbols.Filled.Weight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = stringResource(R.string.current_weight),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = String.format(Locale.US, "%.1f kg", currentWeight),
                style = customFont.numeralMedium,
                fontSize = 40.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            val steps = remember { settings.weightSteps.sorted() }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                steps.forEach { stepValue ->
                    val currentStepFormatted = rememberFormattedStep(step = stepValue)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = {
                                val newWeight = (currentWeight - stepValue).coerceAtLeast(0.0)
                                onWeightChange(newWeight)

                                if (settings.useHapticFeedback) {
                                    haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Remove,
                                contentDescription = stringResource(R.string.decrease_weight_by, currentStepFormatted),
                                tint = Color.Red.copy(red = 1f, green = 0.2f, blue = 0.2f)
                            )
                        }

                        Text(
                            text = currentStepFormatted,
                            modifier = Modifier.padding(horizontal = 4.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )

                        IconButton(
                            onClick = {
                                val newWeight = currentWeight + stepValue
                                onWeightChange(newWeight)

                                if (settings.useHapticFeedback) {
                                    haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = stringResource(R.string.increase_weight_by, currentStepFormatted),
                                tint = Color.Green.copy(red = 0.2f, green = 0.8f, blue = 0.2f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetsHistoryCard(
    sets: List<WorkoutSet>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.completed_sets, sets.size),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (sets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_sets_completed),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(sets.reversed()) { index, set ->
                        SetHistoryItem(set = set, actualSetNumber = sets.size - index)
                    }
                }
            }
        }
    }
}

@Composable
private fun SetHistoryItem(set: WorkoutSet, actualSetNumber: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = actualSetNumber.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Column {
                    Text(
                        text = "${set.weight} kg",
                        style = customFont.numeralMedium,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${set.reps} ${stringResource(R.string.reps_unit)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}