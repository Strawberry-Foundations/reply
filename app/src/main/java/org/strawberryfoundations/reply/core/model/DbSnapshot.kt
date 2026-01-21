package org.strawberryfoundations.reply.core.model

import kotlinx.serialization.Serializable
import org.strawberryfoundations.reply.room.entities.Exercise
import org.strawberryfoundations.reply.room.entities.WorkoutSession

@Serializable
data class DbSnapshot(
    val exercises: List<Exercise>,
    val workoutSessions: List<WorkoutSession>
)