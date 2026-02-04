package org.strawberryfoundations.reply.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.json.Json
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.filled.Weight
import org.strawberryfoundations.reply.R
import org.strawberryfoundations.reply.room.entities.Exercise
import org.strawberryfoundations.reply.room.entities.SessionStatus
import org.strawberryfoundations.reply.room.entities.WorkoutSession
import org.strawberryfoundations.reply.room.entities.WorkoutSet
import org.strawberryfoundations.reply.room.viewmodels.WorkoutSessionViewModel
import org.strawberryfoundations.reply.ui.composable.DeleteSessionDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseHistoryContent(
    exercise: Exercise,
    sessions: List<WorkoutSession>,
    sessionViewModel: WorkoutSessionViewModel,
) {
    val sortedSessions = sessions.sortedByDescending { it.startedAt }
    var sessionToDelete by remember { mutableStateOf<WorkoutSession?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.history),
                style = MaterialTheme.typography.displayMedium,
                fontSize = 22.sp
            )
            Text(
                text = "${sortedSessions.size} ${stringResource(R.string.total_sessions)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (sortedSessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📋",
                        fontSize = 48.sp
                    )
                    Text(
                        text = stringResource(R.string.no_history_yet),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedSessions) { session ->
                    SessionHistoryCard(
                        session = session,
                        onDelete = { sessionToDelete = it }
                    )
                }
            }
        }
    }
    
    if (sessionToDelete != null) {
        DeleteSessionDialog(
            onConfirm = {
                sessionViewModel.delete(sessionToDelete!!)
                sessionToDelete = null
            },
            onDismiss = { sessionToDelete = null }
        )
    }
}

@Composable
fun SessionHistoryCard(
    session: WorkoutSession,
    onDelete: (WorkoutSession) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    val startDate = remember(session.startedAt) { Date(session.startedAt) }
    
    val sets = remember(session.setsHistory) {
        try {
            Json.decodeFromString<List<WorkoutSet>>(session.setsHistory)
        } catch (_: Exception) {
            emptyList()
        }
    }
    
    val totalVolume = remember(sets) {
        sets.sumOf { it.weight * it.reps }
    }
    
    val statusIcon = when (session.status) {
        SessionStatus.COMPLETED -> Icons.Rounded.CheckCircle
        SessionStatus.CANCELLED -> Icons.Rounded.Cancel
        SessionStatus.PAUSED -> Icons.Rounded.Pause
        SessionStatus.ACTIVE -> Icons.Rounded.AccessTime
    }
    
    val statusColor = when (session.status) {
        SessionStatus.COMPLETED -> MaterialTheme.colorScheme.primary
        SessionStatus.CANCELLED -> MaterialTheme.colorScheme.error
        SessionStatus.PAUSED -> MaterialTheme.colorScheme.tertiary
        SessionStatus.ACTIVE -> MaterialTheme.colorScheme.secondary
    }
    
    val statusText = when (session.status) {
        SessionStatus.COMPLETED -> stringResource(R.string.status_completed)
        SessionStatus.CANCELLED -> stringResource(R.string.status_cancelled)
        SessionStatus.PAUSED -> stringResource(R.string.status_paused)
        SessionStatus.ACTIVE -> stringResource(R.string.status_active)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Datum und Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                
                IconButton(
                    onClick = { onDelete(session) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = dateFormat.format(startDate),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SessionStatChip(
                    icon = MaterialSymbols.Filled.Weight,
                    label = stringResource(R.string.weight_kg),
                    value = "%.1f kg".format(session.currentWeight),
                    modifier = Modifier.weight(1f)
                )

                SessionStatChip(
                    label = stringResource(R.string.sets),
                    value = "${session.setsCompleted}",
                    emoji = "💪",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SessionStatChip(
                    label = stringResource(R.string.time),
                    value = formatDuration(session.elapsedSeconds),
                    emoji = "⏱️",
                    modifier = Modifier.weight(1f)
                )
                
                SessionStatChip(
                    label = stringResource(R.string.total_volume),
                    value = "%.0f kg".format(totalVolume),
                    emoji = "📦",
                    modifier = Modifier.weight(1f)
                )
            }
            
            if (sets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = stringResource(R.string.completed_sets, sets.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    sets.forEach { set ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${stringResource(R.string.set)} ${set.setNumber}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "%.1f kg".format(set.weight),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "×",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${set.reps} ${stringResource(R.string.reps_unit)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionStatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    emoji: String? = null
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            } else if (emoji != null) {
                Text(
                    text = emoji,
                    fontSize = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    
    return when {
        hours > 0 -> "%d:%02d:%02d".format(hours, minutes, secs)
        minutes > 0 -> "%d:%02d".format(minutes, secs)
        else -> "0:%02d".format(secs)
    }
}
