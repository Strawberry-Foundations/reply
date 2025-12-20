package org.strawberryfoundations.replicity.core.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import org.strawberryfoundations.replicity.R


@Serializable
enum class ExerciseGroup {
    UPPER_BODY,
    LEGS,
    CARDIO,
    OTHER
}

fun getExerciseGroupEmoji(group: ExerciseGroup): String = when (group) {
    ExerciseGroup.UPPER_BODY -> "💪"
    ExerciseGroup.LEGS -> "🦵"
    ExerciseGroup.CARDIO -> "🏃"
    ExerciseGroup.OTHER -> "🧩"
}

@Composable
fun getExerciseGroupStringResource(group: ExerciseGroup): String = when (group) {
    ExerciseGroup.UPPER_BODY -> stringResource(R.string.upper_body)
    ExerciseGroup.LEGS -> stringResource(R.string.legs)
    ExerciseGroup.CARDIO -> stringResource(R.string.cardio)
    ExerciseGroup.OTHER -> stringResource(R.string.other)
}

@Serializable
@Entity(
    tableName = "trainings",
    indices = [
        Index(value = ["name"]),
        Index(value = ["group"])
    ]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,

    val name: String,
    val weight: Double? = null,
    @ColumnInfo(name = "weight_unit") val weightUnit: String = "kg",
    val note: String = "",
    @ColumnInfo(name = "group") val group: ExerciseGroup = ExerciseGroup.OTHER,
    val color: String = "",

    // metadata
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long? = null,
    val archived: Boolean = false
)