import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.filled.Weight
import org.strawberryfoundations.material.symbols.outlined.Delete
import org.strawberryfoundations.material.symbols.outlined.Edit
import org.strawberryfoundations.reply.R
import org.strawberryfoundations.reply.core.AppSettings
import org.strawberryfoundations.reply.room.ExerciseViewModel
import org.strawberryfoundations.reply.room.entities.Exercise
import org.strawberryfoundations.reply.room.entities.getExerciseGroupEmoji
import org.strawberryfoundations.reply.room.entities.getExerciseGroupStringResource
import org.strawberryfoundations.reply.ui.composable.DeleteExerciseDialog
import org.strawberryfoundations.reply.ui.composable.EditExerciseDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExerciseDetail(
    exercise: Exercise,
    viewModel: ExerciseViewModel = viewModel(),
    onStartTraining: (Exercise) -> Unit,
    settings: AppSettings,
) {
    val haptic = LocalHapticFeedback.current

    var showEditSheet by remember { mutableStateOf(false) }
    var startTraining by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var trainingToDelete by remember { mutableStateOf<Exercise?>(null) }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    label = stringResource(R.string.best_performance),
                    value = "160.0 kg",
                    icon = "🏆",
                    modifier = Modifier.weight(1f),
                    shapeColor = Color(0xFFD77F10)
                )
                StatCard(
                    label = stringResource(R.string.last_performance),
                    value = "${exercise.weight} kg",
                    icon = "⏱️",
                    modifier = Modifier.weight(1f),
                    shapeColor = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.history),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.progress_graph_soon),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

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
                onClick = { }
            )

            // Statistics
            ToolbarAction(
                icon = Icons.Rounded.BarChart,
                description = stringResource(R.string.statistics),
                onClick = { }
            )

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
                onStartConfirm = {
                    onStartTraining(exercise)
                    startTraining = false
                }
            )
        }
    }

    if (showEditSheet) {
        EditExerciseDialog(
            exercise = exercise,
            onSave = { updatedExercise ->
                viewModel.update(updatedExercise)
                showEditSheet = false
            },
            onDismiss = { showEditSheet = false },
            settings = settings
        )
    }

    if (showDeleteDialog && trainingToDelete != null) {
        DeleteExerciseDialog(
            exercise = trainingToDelete!!,
            onConfirm = { viewModel.delete(trainingToDelete!!) },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StartTrainingContent(
    exercise: Exercise,
    onStartConfirm: (Double) -> Unit
) {
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
            CountdownView(
                onCountdownFinish = { onStartConfirm(currentWeight) }
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolbarAction(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    tint: Color = LocalContentColor.current
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Above
        ),
        tooltip = { PlainTooltip { Text(description) } },
        state = rememberTooltipState(),
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = tint
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StatCard(
    label: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier,
    shapeColor: Color
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                shape = MaterialShapes.Cookie9Sided.toShape(),
                color = shapeColor,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = icon, fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}