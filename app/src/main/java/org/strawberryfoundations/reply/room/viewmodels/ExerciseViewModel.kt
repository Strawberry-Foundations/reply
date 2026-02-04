package org.strawberryfoundations.reply.room.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.strawberryfoundations.reply.room.AppDatabase
import org.strawberryfoundations.reply.room.entities.Exercise

class ExerciseViewModel(application: Application): AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).trainingDao()

    val trainings: StateFlow<List<Exercise>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(training: Exercise) {
        viewModelScope.launch { dao.insert(training) }
    }

    fun update(training: Exercise) {
        viewModelScope.launch { dao.update(training) }
    }

    fun delete(training: Exercise) {
        viewModelScope.launch { dao.delete(training) }
    }
}