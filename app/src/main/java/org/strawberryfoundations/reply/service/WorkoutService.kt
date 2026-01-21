package org.strawberryfoundations.reply.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.strawberryfoundations.reply.MainActivity
import org.strawberryfoundations.reply.R
import org.strawberryfoundations.reply.room.AppDatabase
import org.strawberryfoundations.reply.room.entities.Exercise
import org.strawberryfoundations.reply.room.entities.ExerciseGroup
import org.strawberryfoundations.reply.room.entities.SessionStatus
import org.strawberryfoundations.reply.room.entities.WorkoutSession
import org.strawberryfoundations.reply.room.entities.WorkoutSet
import org.strawberryfoundations.reply.room.entities.getExerciseGroupEmoji

class WorkoutService : Service() {
    private val binder = WorkoutBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private var wakeLock: PowerManager.WakeLock? = null
    private var timerJob: Job? = null
    private var restTimerJob: Job? = null
    
    private val _currentSession = MutableStateFlow<WorkoutSession?>(null)
    val currentSession: StateFlow<WorkoutSession?> = _currentSession.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()
    
    private val _restTimeRemaining = MutableStateFlow(0)
    val restTimeRemaining: StateFlow<Int> = _restTimeRemaining.asStateFlow()
    
    private var currentExercise: Exercise? = null
    
    private lateinit var database: AppDatabase
    private lateinit var notificationManager: NotificationManager
    
    companion object {
        const val CHANNEL_ID = "workout_service_channel"
        const val NOTIFICATION_ID = 1001
        
        const val ACTION_START_SESSION = "action_start_session"
        const val ACTION_PAUSE_SESSION = "action_pause_session"
        const val ACTION_RESUME_SESSION = "action_resume_session"
        const val ACTION_COMPLETE_SET = "action_complete_set"
        const val ACTION_START_REST = "action_start_rest"
        const val ACTION_STOP_SESSION = "action_stop_session"
        
        const val EXTRA_SESSION_ID = "extra_session_id"
        const val EXTRA_EXERCISE_ID = "extra_exercise_id"
        const val EXTRA_WEIGHT = "extra_weight"
        const val EXTRA_REPS = "extra_reps"
        const val EXTRA_REST_SECONDS = "extra_rest_seconds"
        
        private const val DEFAULT_REST_TIME = 90 // Sekunden
    }
    
    inner class WorkoutBinder : Binder() {
        fun getService(): WorkoutService = this@WorkoutService
    }
    
    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(applicationContext)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        acquireWakeLock()

        serviceScope.launch {
            val activeSession = database.workoutSessionDao().getActiveSession().firstOrNull()
            if (activeSession != null) {
                _currentSession.value = activeSession
                _elapsedSeconds.value = activeSession.elapsedSeconds

                currentExercise = database.workoutSessionDao().getExerciseById(activeSession.id)
                
                // Starte Foreground Service wenn Session existiert
                startForeground(
                    NOTIFICATION_ID,
                    buildNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
                
                if (activeSession.status == SessionStatus.ACTIVE) {
                    startTimer()
                }
                if (activeSession.isResting && activeSession.restTimerSeconds > 0) {
                    _restTimeRemaining.value = activeSession.restTimerSeconds
                    startRestTimer()
                }
            }
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SESSION -> {
                val exerciseId = intent.getLongExtra(EXTRA_EXERCISE_ID, -1L)
                val weight = intent.getDoubleExtra(EXTRA_WEIGHT, 0.0)
                if (exerciseId != -1L) {
                    serviceScope.launch { startNewSession(exerciseId, weight) }
                }
            }
            ACTION_PAUSE_SESSION -> pauseSession()
            ACTION_RESUME_SESSION -> resumeSession()
            ACTION_COMPLETE_SET -> {
                val reps = intent.getIntExtra(EXTRA_REPS, 0)
                if (reps > 0) {
                    completeSet(reps)
                }
            }
            ACTION_START_REST -> {
                val restSeconds = intent.getIntExtra(EXTRA_REST_SECONDS, DEFAULT_REST_TIME)
                startRest(restSeconds)
            }
            ACTION_STOP_SESSION -> stopSession()
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
    
    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        restTimerJob?.cancel()
        serviceScope.cancel()
        releaseWakeLock()
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Workout Session",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows active workout session"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }
    
    private suspend fun startNewSession(exerciseId: Long, weight: Double) {
        // Zuerst alle anderen aktiven Sessions abbrechen (nur eine Session gleichzeitig erlaubt)
        database.workoutSessionDao().cancelAllActiveSessions()
        
        val session = WorkoutSession(
            exerciseId = exerciseId,
            currentWeight = weight,
            startedAt = System.currentTimeMillis(),
            status = SessionStatus.ACTIVE
        )
        
        val sessionId = database.workoutSessionDao().insert(session)
        val insertedSession = database.workoutSessionDao().getSessionByIdOnce(sessionId)

        if (insertedSession != null) {
            _currentSession.value = insertedSession
            _elapsedSeconds.value = 0L

            currentExercise = database.workoutSessionDao().getExerciseById(sessionId)
            
            startTimer()
            startForeground(
                NOTIFICATION_ID,
                buildNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        }
    }
    
    private fun pauseSession() {
        val session = _currentSession.value ?: return
        timerJob?.cancel()
        
        serviceScope.launch {
            val updated = session.copy(
                status = SessionStatus.PAUSED,
                elapsedSeconds = _elapsedSeconds.value,
                updatedAt = System.currentTimeMillis()
            )
            database.workoutSessionDao().update(updated)
            _currentSession.value = updated
            updateNotification()
        }
    }
    
    private fun resumeSession() {
        val session = _currentSession.value ?: return
        
        serviceScope.launch {
            val updated = session.copy(
                status = SessionStatus.ACTIVE,
                updatedAt = System.currentTimeMillis()
            )
            database.workoutSessionDao().update(updated)
            _currentSession.value = updated
            startTimer()
            updateNotification()
        }
    }
    
    fun completeSet(reps: Int) {
        val session = _currentSession.value ?: return
        
        serviceScope.launch {
            val setsHistory = try {
                Json.decodeFromString<List<WorkoutSet>>(session.setsHistory).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
            
            val newSet = WorkoutSet(
                setNumber = setsHistory.size + 1,
                weight = session.currentWeight,
                reps = reps,
                timestamp = System.currentTimeMillis()
            )
            setsHistory.add(newSet)
            
            val updated = session.copy(
                setsCompleted = setsHistory.size,
                setsHistory = Json.encodeToString(setsHistory),
                updatedAt = System.currentTimeMillis()
            )
            
            database.workoutSessionDao().update(updated)
            _currentSession.value = updated
            updateNotification()
        }
    }
    
    fun startRest(seconds: Int = DEFAULT_REST_TIME) {
        val session = _currentSession.value ?: return
        
        _restTimeRemaining.value = seconds
        
        serviceScope.launch {
            val updated = session.copy(
                isResting = true,
                restTimerSeconds = seconds,
                updatedAt = System.currentTimeMillis()
            )
            database.workoutSessionDao().update(updated)
            _currentSession.value = updated
            startRestTimer()
            updateNotification()
        }
    }
    
    fun updateWeight(newWeight: Double) {
        val session = _currentSession.value ?: return
        
        serviceScope.launch {
            val updated = session.copy(
                currentWeight = newWeight,
                updatedAt = System.currentTimeMillis()
            )
            database.workoutSessionDao().update(updated)
            _currentSession.value = updated
            updateNotification()
        }
    }
    
    private fun stopSession() {
        val session = _currentSession.value ?: return
        
        serviceScope.launch {
            val updated = session.copy(
                status = SessionStatus.COMPLETED,
                endedAt = System.currentTimeMillis(),
                elapsedSeconds = _elapsedSeconds.value,
                updatedAt = System.currentTimeMillis()
            )
            database.workoutSessionDao().update(updated)
            _currentSession.value = null
            
            timerJob?.cancel()
            restTimerJob?.cancel()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive && _currentSession.value?.status == SessionStatus.ACTIVE) {
                delay(1000)
                _elapsedSeconds.value += 1

                if (_elapsedSeconds.value % 10 == 0L) {
                    _currentSession.value?.let { session ->
                        val updated = session.copy(
                            elapsedSeconds = _elapsedSeconds.value,
                            updatedAt = System.currentTimeMillis()
                        )
                        database.workoutSessionDao().update(updated)
                    }
                }
                
                updateNotification()
            }
        }
    }
    
    private fun startRestTimer() {
        restTimerJob?.cancel()
        restTimerJob = serviceScope.launch {
            while (isActive && _restTimeRemaining.value > 0) {
                delay(1000)
                _restTimeRemaining.value -= 1
                updateNotification()
                
                if (_restTimeRemaining.value == 0) {
                    _currentSession.value?.let { session ->
                        val updated = session.copy(
                            isResting = false,
                            restTimerSeconds = 0,
                            updatedAt = System.currentTimeMillis()
                        )
                        database.workoutSessionDao().update(updated)
                        _currentSession.value = updated
                    }
                }
            }
        }
    }
    
    private fun acquireWakeLock() {
        wakeLock = (getSystemService(POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WorkoutService::WakeLock").apply {
                acquire(10 * 60 * 1000L)
            }
        }
    }
    
    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
    }

    private fun buildNotification(): Notification {
        val session = _currentSession.value
        val elapsed = _elapsedSeconds.value
        val hours = elapsed / 3600
        val minutes = (elapsed % 3600) / 60
        val seconds = elapsed % 60

        val timeString = if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
        
        val restInfo = if (session?.isResting == true && _restTimeRemaining.value > 0) {
            " • ${getString(R.string.rest)}: ${_restTimeRemaining.value}s"
        } else ""

        val contentTitle = "${getExerciseGroupEmoji(currentExercise?.group ?: ExerciseGroup.OTHER)} ${currentExercise?.name}"
        
        val contentText = "${getString(R.string.time)}: $timeString • ${getString(R.string.sets)}: ${session?.setsCompleted ?: 0}$restInfo"
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to_session", session?.id)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }
    
    private fun updateNotification() {
        if (_currentSession.value != null) {
            notificationManager.notify(NOTIFICATION_ID, buildNotification())
        }
    }
}
