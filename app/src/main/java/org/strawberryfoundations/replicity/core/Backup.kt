package org.strawberryfoundations.replicity.core

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.strawberryfoundations.replicity.core.model.Training
import org.strawberryfoundations.replicity.core.preferences.AppSettings
import org.strawberryfoundations.replicity.core.preferences.SettingsDataStore
import org.strawberryfoundations.replicity.core.preferences.UserPreferences
import org.strawberryfoundations.replicity.core.preferences.getUserDataFlow
import org.strawberryfoundations.replicity.core.preferences.saveUserData
import org.strawberryfoundations.replicity.data.AppDatabase
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Serializable
data class AppBackup(
    val version: Int = 1,
    val timestamp: String,
    val userPreferences: UserPreferences,
    val appSettings: AppSettings,
    val trainingData: List<Training> = emptyList(),
)

class BackupManager(private val context: Context) {
    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    private val database = AppDatabase.getInstance(context)
    private val trainingDao = database.trainingDao()
    
    suspend fun createBackup(): AppBackup {
        val userPrefs = getUserDataFlow(context).first()
        val settingsDataStore = SettingsDataStore(context)
        val appSettings = settingsDataStore.settingsFlow.first()
        val trainings = trainingDao.getAll().first()
        
        return AppBackup(
            timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            userPreferences = userPrefs,
            appSettings = appSettings,
            trainingData = trainings
        )
    }
    
    suspend fun exportBackup(outputStream: OutputStream): Boolean {
        return try {
            val backup = createBackup()
            val jsonString = json.encodeToString(backup)
            outputStream.use { it.write(jsonString.toByteArray()) }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    suspend fun importBackup(inputStream: InputStream): Boolean {
        return try {
            val jsonString = inputStream.use { it.readBytes().decodeToString() }
            val backup = json.decodeFromString<AppBackup>(jsonString)
            
            saveUserData(context, backup.userPreferences)
            val settingsDataStore = SettingsDataStore(context)
            settingsDataStore.updateSettings { backup.appSettings }
            
            val currentTrainings = trainingDao.getAll().first()
            currentTrainings.forEach { trainingDao.delete(it) }
            
            backup.trainingData.forEach { training ->
                trainingDao.insert(training.copy(id = 0))
            }
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    suspend fun importBackupMerge(inputStream: InputStream): Boolean {
        return try {
            val jsonString = inputStream.use { it.readBytes().decodeToString() }
            val backup = json.decodeFromString<AppBackup>(jsonString)
            
            saveUserData(context, backup.userPreferences)
            val settingsDataStore = SettingsDataStore(context)
            settingsDataStore.updateSettings { backup.appSettings }
            
            backup.trainingData.forEach { training ->
                trainingDao.insert(training.copy(id = 0))
            }
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}