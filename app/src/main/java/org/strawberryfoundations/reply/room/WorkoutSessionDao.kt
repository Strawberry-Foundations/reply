package org.strawberryfoundations.reply.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.strawberryfoundations.reply.room.entities.SessionStatus
import org.strawberryfoundations.reply.room.entities.WorkoutSession

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions WHERE status = 'ACTIVE' OR status = 'PAUSED' ORDER BY started_at DESC LIMIT 1")
    fun getActiveSession(): Flow<WorkoutSession?>
    
    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    fun getSessionById(sessionId: Long): Flow<WorkoutSession?>
    
    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    suspend fun getSessionByIdOnce(sessionId: Long): WorkoutSession?
    
    @Query("SELECT * FROM workout_sessions WHERE exercise_id = :exerciseId ORDER BY started_at DESC")
    fun getSessionsByExercise(exerciseId: Long): Flow<List<WorkoutSession>>
    
    @Query("SELECT * FROM workout_sessions WHERE status = :status ORDER BY started_at DESC")
    fun getSessionsByStatus(status: SessionStatus): Flow<List<WorkoutSession>>
    
    @Query("SELECT * FROM workout_sessions ORDER BY started_at DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: WorkoutSession): Long
    
    @Update
    suspend fun update(session: WorkoutSession)
    
    @Delete
    suspend fun delete(session: WorkoutSession)
    
    @Query("DELETE FROM workout_sessions WHERE status = 'COMPLETED' OR status = 'CANCELLED'")
    suspend fun deleteCompletedSessions()
}
