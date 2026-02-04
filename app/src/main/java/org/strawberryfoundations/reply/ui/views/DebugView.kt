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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.strawberryfoundations.reply.room.entities.SessionStatus
import org.strawberryfoundations.reply.room.entities.WorkoutSession
import org.strawberryfoundations.reply.room.viewmodels.ExerciseViewModel
import org.strawberryfoundations.reply.room.viewmodels.WorkoutSessionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                text = "Workout Sessions Debug",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            val activeSessions = remember(sessions) {
                sessions.count { it.status == SessionStatus.ACTIVE || it.status == SessionStatus.PAUSED }
            }

            Text(
                text = "${sessions.size} ${if (sessions.size == 1) "Eintrag" else "Einträge"}",
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
                            "⚠️ $activeSessions aktive Sessions (sollte max. 1 sein!)" 
                        else 
                            "✓ $activeSessions aktive Session",
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
                        text = "Keine Workout Sessions vorhanden",
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
                                ?: "Unbekannt (ID: ${session.exerciseId})",
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
            title = { Text("Session löschen?") },
            text = { Text("Möchtest du diese Workout Session wirklich löschen?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        sessionVm.delete(session)
                        sessionToDelete = null
                    }
                ) {
                    Text("Löschen", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) {
                    Text("Abbrechen")
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
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    
    val statusColor = when (session.status) {
        SessionStatus.ACTIVE -> MaterialTheme.colorScheme.primary
        SessionStatus.PAUSED -> MaterialTheme.colorScheme.tertiary
        SessionStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
        SessionStatus.CANCELLED -> MaterialTheme.colorScheme.error
    }
    
    val statusText = when (session.status) {
        SessionStatus.ACTIVE -> "Aktiv"
        SessionStatus.PAUSED -> "Pausiert"
        SessionStatus.COMPLETED -> "Abgeschlossen"
        SessionStatus.CANCELLED -> "Abgebrochen"
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
                        contentDescription = "Löschen",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Session Info
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                InfoRow("ID:", session.id.toString())
                InfoRow("Gestartet:", dateFormat.format(Date(session.startedAt)))
                
                session.endedAt?.let { endedAt ->
                    InfoRow("Beendet:", dateFormat.format(Date(endedAt)))
                    val durationMinutes = (endedAt - session.startedAt) / 1000 / 60
                    InfoRow("Dauer:", "$durationMinutes min")
                }
                
                InfoRow("Gewicht:", "${session.currentWeight} kg")
                InfoRow("Sätze absolviert:", "${session.setsCompleted}")
                
                session.totalSets?.let {
                    InfoRow("Gesamt Sätze:", it.toString())
                }
                
                val hours = session.elapsedSeconds / 3600
                val minutes = (session.elapsedSeconds % 3600) / 60
                val seconds = session.elapsedSeconds % 60
                val timeStr = if (hours > 0) {
                    String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
                } else {
                    String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                }
                InfoRow("Timer:", timeStr)
                
                if (session.isResting) {
                    InfoRow("Rest-Timer:", "${session.restTimerSeconds}s")
                }
                
                if (session.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Notiz: ${session.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
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