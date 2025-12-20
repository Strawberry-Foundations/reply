package org.strawberryfoundations.replicity.core

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.strawberryfoundations.replicity.core.model.Exercise
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
    val trainingData: List<Exercise> = emptyList(),
)

class BackupManager(private val context: Context) {
    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    // Migration map for old german enum values
    private val groupMigrationMap = mapOf(
        "Beine" to "LEGS",
        "Oberkörper" to "UPPER_BODY",
        "Cardio" to "CARDIO",
        "Sonstiges" to "OTHER"
    )
    
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
            val migratedJsonString = migrateBackupJson(jsonString)
            val backup = json.decodeFromString<AppBackup>(migratedJsonString)
            
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
            val migratedJsonString = migrateBackupJson(jsonString)
            val backup = json.decodeFromString<AppBackup>(migratedJsonString)
            
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
    
    private fun migrateBackupJson(jsonString: String): String {
        try {
            var migratedJson = jsonString
            
            // 1. Migrate group names from German to English
            migratedJson = migratedJson.replace(Regex("\"group\"\\s*:\\s*\"([^\"]+)\"")) { matchResult ->
                val oldValue = matchResult.groupValues[1]
                val newValue = groupMigrationMap[oldValue] ?: oldValue
                "\"group\":\"$newValue\""
            }

            // 2. Migrate "weight" from string to number
            migratedJson = migratedJson.replace(Regex("\"weight\"\\s*:\\s*\"([^\"]+)\"")) {
                val weightAsString = it.groupValues[1].replace(',', '.')
                "\"weight\":${weightAsString.toDoubleOrNull() ?: 0.0}"
            }
            
            return migratedJson
        } catch (e: Exception) {
            e.printStackTrace()
            return jsonString
        }
    }
}
