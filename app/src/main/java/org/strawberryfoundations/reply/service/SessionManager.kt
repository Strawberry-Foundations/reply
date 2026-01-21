package org.strawberryfoundations.reply.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.strawberryfoundations.reply.room.entities.WorkoutSession


object SessionManager {
    private var workoutService: WorkoutService? = null
    private var isBound = false
    
    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()
    
    val currentSession: StateFlow<WorkoutSession?>
        get() = workoutService?.currentSession ?: MutableStateFlow(null)
    
    val elapsedSeconds: StateFlow<Long>
        get() = workoutService?.elapsedSeconds ?: MutableStateFlow(0L)
    
    val restTimeRemaining: StateFlow<Int>
        get() = workoutService?.restTimeRemaining ?: MutableStateFlow(0)
    
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? WorkoutService.WorkoutBinder
            workoutService = binder?.getService()
            isBound = true
            _isServiceConnected.value = true
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            workoutService = null
            isBound = false
            _isServiceConnected.value = false
        }
    }
    
    fun bindService(context: Context) {
        if (!isBound) {
            val intent = Intent(context, WorkoutService::class.java)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }
    
    fun unbindService(context: Context) {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
            _isServiceConnected.value = false
        }
    }
    
    fun startSession(context: Context, exerciseId: Long, weight: Double) {
        val intent = Intent(context, WorkoutService::class.java).apply {
            action = WorkoutService.ACTION_START_SESSION
            putExtra(WorkoutService.EXTRA_EXERCISE_ID, exerciseId)
            putExtra(WorkoutService.EXTRA_WEIGHT, weight)
        }
        context.startForegroundService(intent)
        bindService(context)
    }
    
    fun pauseSession(context: Context) {
        sendAction(context, WorkoutService.ACTION_PAUSE_SESSION)
    }
    
    fun resumeSession(context: Context) {
        sendAction(context, WorkoutService.ACTION_RESUME_SESSION)
    }
    
    fun completeSet(reps: Int) {
        workoutService?.completeSet(reps)
    }
    
    fun startRest(seconds: Int = 90) {
        workoutService?.startRest(seconds)
    }
    
    fun updateWeight(newWeight: Double) {
        workoutService?.updateWeight(newWeight)
    }
    
    fun stopSession(context: Context) {
        sendAction(context, WorkoutService.ACTION_STOP_SESSION)
        unbindService(context)
    }
    
    private fun sendAction(context: Context, action: String) {
        val intent = Intent(context, WorkoutService::class.java).apply {
            this.action = action
        }
        context.startService(intent)
    }
}
