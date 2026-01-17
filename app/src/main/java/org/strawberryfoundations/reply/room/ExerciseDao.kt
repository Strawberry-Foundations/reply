package org.strawberryfoundations.reply.room

import org.strawberryfoundations.reply.room.entities.Exercise
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ExerciseDao {
    @Query("SELECT * FROM trainings")
    fun getAll(): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(training: Exercise)

    @Update
    suspend fun update(training: Exercise)

    @Delete
    suspend fun delete(training: Exercise)
}