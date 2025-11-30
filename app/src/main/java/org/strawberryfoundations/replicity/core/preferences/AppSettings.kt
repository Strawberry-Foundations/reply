package org.strawberryfoundations.replicity.core.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable


@Serializable
data class AppSettings(
    val dynamicColor: Boolean = true,
    val useEmojisForGroups: Boolean = false,
    val weightSteps: List<Double> = listOf(0.625, 2.5, 5.0, 10.0, 15.0, 20.0)
)

object SettingsKeys {
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    val USE_EMOJIS_FOR_GROUPS = booleanPreferencesKey("use_emojis_for_groups")
    val WEIGHT_STEPS = stringPreferencesKey("weight_steps")
}

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            dynamicColor = prefs[SettingsKeys.DYNAMIC_COLOR] != false,
            useEmojisForGroups = prefs[SettingsKeys.USE_EMOJIS_FOR_GROUPS] == true,
            weightSteps = prefs[SettingsKeys.WEIGHT_STEPS]
                ?.split(",")
                ?.mapNotNull { it.toDoubleOrNull() }
                ?: listOf(0.625, 2.5, 5.0, 10.0, 15.0, 20.0)
        )
    }

    suspend fun updateSettings(update: (AppSettings) -> AppSettings) {
        context.dataStore.edit { prefs ->
            val current = AppSettings(
                dynamicColor = prefs[SettingsKeys.DYNAMIC_COLOR] ?: true,
                useEmojisForGroups = prefs[SettingsKeys.USE_EMOJIS_FOR_GROUPS] ?: false,
                weightSteps = prefs[SettingsKeys.WEIGHT_STEPS]
                    ?.split(",")
                    ?.mapNotNull { it.toDoubleOrNull() }
                    ?: listOf(0.625, 2.5, 5.0, 10.0, 15.0, 20.0)
            )
            val new = update(current)
            prefs[SettingsKeys.DYNAMIC_COLOR] = new.dynamicColor
            prefs[SettingsKeys.USE_EMOJIS_FOR_GROUPS] = new.useEmojisForGroups
            prefs[SettingsKeys.WEIGHT_STEPS] = new.weightSteps.joinToString(",")
        }
    }
}