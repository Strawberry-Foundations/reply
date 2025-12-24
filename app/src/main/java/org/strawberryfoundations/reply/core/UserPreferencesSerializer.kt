package org.strawberryfoundations.reply.core

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.json.Json
import org.strawberryfoundations.reply.core.model.UserPreferences
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences()
    override suspend fun readFrom(input: InputStream): UserPreferences {
        return try {
            val json = input.readBytes().decodeToString()
            Json.Default.decodeFromString<UserPreferences>(json)
        } catch (e: Exception) {
            throw CorruptionException("Cannot read UserPreferences.", e)
        }
    }
    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        output.write(Json.Default.encodeToString(t).encodeToByteArray())
    }
}