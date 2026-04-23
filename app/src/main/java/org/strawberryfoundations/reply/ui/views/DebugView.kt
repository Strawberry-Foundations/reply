package org.strawberryfoundations.reply.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import org.strawberryfoundations.reply.R
import org.strawberryfoundations.reply.room.entities.SessionStatus
import org.strawberryfoundations.reply.room.entities.WorkoutSession
import org.strawberryfoundations.reply.room.viewmodels.ExerciseViewModel
import org.strawberryfoundations.reply.room.viewmodels.WorkoutSessionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugView(
    exerciseVm: ExerciseViewModel = viewModel(),
    sessionVm: WorkoutSessionViewModel = viewModel(),
) {
    val sessions by sessionVm.allSessions.collectAsState()
    val exercises by exerciseVm.trainings.collectAsState()
    
    var sessionToDelete by remember { mutableStateOf<WorkoutSession?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.debug_sessions_title),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            val activeSessions = remember(sessions) {
                sessions.count { it.status == SessionStatus.ACTIVE || it.status == SessionStatus.PAUSED }
            }

            Text(
                text = stringResource(
                    if (sessions.size == 1) {
                        R.string.debug_entry_count_single
                    } else {
                        R.string.debug_entry_count_plural
                    },
                    sessions.size
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (activeSessions > 0) {
                Surface(
                    color = if (activeSessions > 1) 
                        MaterialTheme.colorScheme.errorContainer 
                    else 
                        MaterialTheme.colorScheme.primaryContainer,
                    contentColor = if (activeSessions > 1) 
                        MaterialTheme.colorScheme.onErrorContainer 
                    else 
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = if (activeSessions > 1)
                            stringResource(R.string.debug_active_sessions_warning, activeSessions)
                        else
                            stringResource(R.string.debug_active_session_ok, activeSessions),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.debug_no_sessions),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sessions, key = { it.id }) { session ->
                        val exercise = remember(session.exerciseId, exercises) {
                            exercises.firstOrNull { it.id == session.exerciseId }
                        }

                        WorkoutSessionCard(
                            session = session,
                            exerciseName = exercise?.name
                                ?: stringResource(R.string.debug_unknown_exercise, session.exerciseId),
                            onDelete = { sessionToDelete = session }
                        )
                    }
                }
            }
        }
    }
    // Delete confirmation dialog
    sessionToDelete?.let { session ->
        AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            title = { Text(stringResource(R.string.debug_delete_session_title)) },
            text = { Text(stringResource(R.string.debug_delete_session_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        sessionVm.delete(session)
                        sessionToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun WorkoutSessionCard(
    session: WorkoutSession,
    exerciseName: String,
    onDelete: () -> Unit
) {
    val dateFormat = remember {
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    }
    
    val statusColor = when (session.status) {
        SessionStatus.ACTIVE -> MaterialTheme.colorScheme.primary
        SessionStatus.PAUSED -> MaterialTheme.colorScheme.tertiary
        SessionStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
        SessionStatus.CANCELLED -> MaterialTheme.colorScheme.error
    }
    
    val statusText = when (session.status) {
        SessionStatus.ACTIVE -> stringResource(R.string.status_active)
        SessionStatus.PAUSED -> stringResource(R.string.status_paused)
        SessionStatus.COMPLETED -> stringResource(R.string.status_completed)
        SessionStatus.CANCELLED -> stringResource(R.string.status_cancelled)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Surface(
                        color = statusColor.copy(alpha = 0.2f),
                        contentColor = statusColor,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = statusText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Session Info
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                InfoRow(stringResource(R.string.debug_label_id), session.id.toString())
                InfoRow(
                    stringResource(R.string.debug_label_started),
                    dateFormat.format(Date(session.startedAt))
                )
                
                session.endedAt?.let { endedAt ->
                    InfoRow(stringResource(R.string.debug_label_ended), dateFormat.format(Date(endedAt)))
                    val durationMinutes = (endedAt - session.startedAt) / 1000 / 60
                    InfoRow(stringResource(R.string.debug_label_duration), stringResource(R.string.debug_duration_minutes, durationMinutes))
                }
                
                InfoRow(stringResource(R.string.debug_label_weight), stringResource(R.string.debug_weight_value, session.currentWeight))
                InfoRow(stringResource(R.string.debug_label_sets_completed), session.setsCompleted.toString())
                
                session.totalSets?.let {
                    InfoRow(stringResource(R.string.debug_label_total_sets), it.toString())
                }
                
                val hours = session.elapsedSeconds / 3600
                val minutes = (session.elapsedSeconds % 3600) / 60
                val seconds = session.elapsedSeconds % 60
                val timeStr = if (hours > 0) {
                    String.format(LocalLocale.current.platformLocale, "%d:%02d:%02d", hours, minutes, seconds)
                } else {
                    String.format(LocalLocale.current.platformLocale, "%02d:%02d", minutes, seconds)
                }
                InfoRow(stringResource(R.string.debug_label_timer), timeStr)
                
                if (session.isResting) {
                    InfoRow(stringResource(R.string.debug_label_rest_timer), stringResource(R.string.debug_seconds_short, session.restTimerSeconds))
                }
                
                if (session.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.debug_note_value, session.notes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}