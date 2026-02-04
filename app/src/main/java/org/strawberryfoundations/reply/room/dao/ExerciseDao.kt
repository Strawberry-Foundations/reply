package org.strawberryfoundations.reply.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.strawberryfoundations.reply.room.entities.Exercise

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM trainings")
    fun getAll(): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(training: Exercise): Long

    @Update
    suspend fun update(training: Exercise)

    @Delete
    suspend fun delete(training: Exercise)
}