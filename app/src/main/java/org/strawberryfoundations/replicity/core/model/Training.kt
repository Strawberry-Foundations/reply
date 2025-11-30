package org.strawberryfoundations.replicity.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Serializable
@Entity
data class Training(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val weight: String = "",
    val note: String = "",
    val group: String = "Sonstiges",
    val color: String = ""
)