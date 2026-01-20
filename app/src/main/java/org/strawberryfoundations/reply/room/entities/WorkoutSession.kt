package org.strawberryfoundations.reply.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

enum class SessionStatus {
    ACTIVE,
    PAUSED,
    COMPLETED,
    CANCELLED
}

@Serializable
data class WorkoutSet(
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "workout_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["exercise_id"]),
        Index(value = ["status"]),
        Index(value = ["started_at"])
    ]
)
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    
    @ColumnInfo(name = "exercise_id") val exerciseId: Long,
    @ColumnInfo(name = "started_at") val startedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "ended_at") val endedAt: Long? = null,
    
    val status: SessionStatus = SessionStatus.ACTIVE,

    @ColumnInfo(name = "current_weight") val currentWeight: Double,
    @ColumnInfo(name = "sets_completed") val setsCompleted: Int = 0,
    @ColumnInfo(name = "total_sets") val totalSets: Int? = null,

    @ColumnInfo(name = "elapsed_seconds") val elapsedSeconds: Long = 0L,
    @ColumnInfo(name = "rest_timer_seconds") val restTimerSeconds: Int = 0,
    @ColumnInfo(name = "is_resting") val isResting: Boolean = false,

    @ColumnInfo(name = "sets_history") val setsHistory: String = "[]", // JSON Array of WorkoutSet

    @ColumnInfo(name = "notes") val notes: String = "",
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
