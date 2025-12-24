package org.strawberryfoundations.reply.core

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable


@Serializable
data class AppSettings(
    val useDynamicColors: Boolean = true,
    val useHapticFeedback: Boolean = false,
    val weightSteps: List<Double> = listOf(2.5, 5.0, 10.0, 15.0)
)

object SettingsKeys {
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
    val WEIGHT_STEPS = stringPreferencesKey("weight_steps")
}

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            useDynamicColors = prefs[SettingsKeys.DYNAMIC_COLOR] ?: true,
            useHapticFeedback = prefs[SettingsKeys.HAPTIC_FEEDBACK] ?: false,
            weightSteps = prefs[SettingsKeys.WEIGHT_STEPS]
                ?.split(",")
                ?.mapNotNull { it.toDoubleOrNull() }
                ?: listOf(0.625, 2.5, 5.0, 10.0, 15.0, 20.0)
        )
    }

    suspend fun updateSettings(update: (AppSettings) -> AppSettings) {
        context.dataStore.edit { prefs ->
            val current = AppSettings(
                useDynamicColors = prefs[SettingsKeys.DYNAMIC_COLOR] ?: true,
                useHapticFeedback = prefs[SettingsKeys.HAPTIC_FEEDBACK] ?: false,
                weightSteps = prefs[SettingsKeys.WEIGHT_STEPS]
                    ?.split(",")
                    ?.mapNotNull { it.toDoubleOrNull() }
                    ?: listOf(0.625, 2.5, 5.0, 10.0, 15.0, 20.0)
            )
            val new = update(current)
            prefs[SettingsKeys.DYNAMIC_COLOR] = new.useDynamicColors
            prefs[SettingsKeys.HAPTIC_FEEDBACK] = new.useHapticFeedback
            prefs[SettingsKeys.WEIGHT_STEPS] = new.weightSteps.joinToString(",")
        }
    }
}