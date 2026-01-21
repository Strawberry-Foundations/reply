package org.strawberryfoundations.reply.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.strawberryfoundations.reply.room.entities.Exercise
import org.strawberryfoundations.reply.room.entities.SessionStatus
import org.strawberryfoundations.reply.room.entities.WorkoutSession

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions")
    fun getAll(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE status = 'ACTIVE' OR status = 'PAUSED' ORDER BY started_at DESC LIMIT 1")
    fun getActiveSession(): Flow<WorkoutSession?>
    
    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    fun getSessionById(sessionId: Long): Flow<WorkoutSession?>
    
    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    suspend fun getSessionByIdOnce(sessionId: Long): WorkoutSession?
    
    @Query("""
        SELECT T.*
        FROM workout_sessions
        INNER JOIN trainings AS T ON workout_sessions.exercise_id = T.id
        WHERE workout_sessions.id = :sessionId
    """)
    suspend fun getExerciseById(sessionId: Long): Exercise?
    
    @Query("SELECT * FROM workout_sessions WHERE exercise_id = :exerciseId ORDER BY started_at DESC")
    fun getSessionsByExercise(exerciseId: Long): Flow<List<WorkoutSession>>
    
    @Query("SELECT * FROM workout_sessions WHERE status = :status ORDER BY started_at DESC")
    fun getSessionsByStatus(status: SessionStatus): Flow<List<WorkoutSession>>
    
    @Query("SELECT * FROM workout_sessions ORDER BY started_at DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>
    
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(session: WorkoutSession): Long
    
    @Update
    suspend fun update(session: WorkoutSession)
    
    @Delete
    suspend fun delete(session: WorkoutSession)
    
    @Query("DELETE FROM workout_sessions WHERE status = 'COMPLETED' OR status = 'CANCELLED'")
    suspend fun deleteCompletedSessions()
    
    @Query("UPDATE workout_sessions SET status = 'CANCELLED', ended_at = :timestamp, updated_at = :timestamp WHERE status = 'ACTIVE' OR status = 'PAUSED'")
    suspend fun cancelAllActiveSessions(timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT COUNT(*) FROM workout_sessions WHERE status = 'ACTIVE' OR status = 'PAUSED'")
    suspend fun countActiveSessions(): Int
}