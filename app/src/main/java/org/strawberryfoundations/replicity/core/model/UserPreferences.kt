package org.strawberryfoundations.replicity.core.model

import kotlinx.serialization.Serializable


@Serializable
data class UserPreferences(
    val username: String = "",
    val email: String = "",
    val fullName: String = "",
    val profilePictureUrl: String = "",
    val token: String = ""
)