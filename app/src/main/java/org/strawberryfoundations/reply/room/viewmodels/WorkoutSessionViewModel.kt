package org.strawberryfoundations.reply.room.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.strawberryfoundations.reply.room.AppDatabase
import org.strawberryfoundations.reply.room.entities.SessionStatus
import org.strawberryfoundations.reply.room.entities.WorkoutSession

class WorkoutSessionViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.Companion.getInstance(application).workoutSessionDao()

    val allSessions: StateFlow<List<WorkoutSession>> = dao.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), emptyList())

    val activeSession: StateFlow<WorkoutSession?> = dao.getActiveSession()
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), null)

    fun getSessionById(sessionId: Long): StateFlow<WorkoutSession?> {
        return dao.getSessionById(sessionId)
            .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), null)
    }

    fun getSessionsByExercise(exerciseId: Long): StateFlow<List<WorkoutSession>> {
        return dao.getSessionsByExercise(exerciseId)
            .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), emptyList())
    }

    fun getSessionsByStatus(status: SessionStatus): StateFlow<List<WorkoutSession>> {
        return dao.getSessionsByStatus(status)
            .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), emptyList())
    }

    suspend fun getSessionByIdOnce(sessionId: Long): WorkoutSession? {
        return dao.getSessionByIdOnce(sessionId)
    }

    suspend fun getExerciseForSession(sessionId: Long) = dao.getExerciseById(sessionId)

    suspend fun countActiveSessions(): Int {
        return dao.countActiveSessions()
    }

    fun insert(session: WorkoutSession, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = dao.insert(session)
            onComplete(id)
        }
    }

    fun update(session: WorkoutSession) {
        viewModelScope.launch { dao.update(session) }
    }

    fun delete(session: WorkoutSession) {
        viewModelScope.launch { dao.delete(session) }
    }

    fun deleteCompletedSessions() {
        viewModelScope.launch { dao.deleteCompletedSessions() }
    }

    fun cancelAllActiveSessions() {
        viewModelScope.launch { dao.cancelAllActiveSessions() }
    }
}