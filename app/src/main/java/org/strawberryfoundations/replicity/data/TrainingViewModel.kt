package org.strawberryfoundations.replicity.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.strawberryfoundations.replicity.core.model.Training


class TrainingViewModel(application: Application): AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).trainingDao()

    val trainings: StateFlow<List<Training>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(training: Training) {
        viewModelScope.launch { dao.insert(training) }
    }

    fun update(training: Training) {
        viewModelScope.launch { dao.update(training) }
    }

    fun delete(training: Training) {
        viewModelScope.launch { dao.delete(training) }
    }
}