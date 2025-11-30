package org.strawberryfoundations.replicity.core.preferences

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream


@Serializable
data class UserPreferences(
    val username: String = "",
    val email: String = "",
    val fullName: String = "",
    val profilePictureUrl: String = "",
    val token: String = ""
)

object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences()
    override suspend fun readFrom(input: InputStream): UserPreferences {
        return try {
            val json = input.readBytes().decodeToString()
            Json.decodeFromString<UserPreferences>(json)
        } catch (e: Exception) {
            throw CorruptionException("Cannot read UserPreferences.", e)
        }
    }
    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        output.write(Json.encodeToString(t).encodeToByteArray())
    }
}