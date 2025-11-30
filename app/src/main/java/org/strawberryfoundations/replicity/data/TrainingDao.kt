package org.strawberryfoundations.replicity.data

import org.strawberryfoundations.replicity.core.model.Training
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface TrainingDao {
    @Query("SELECT * FROM Training")
    fun getAll(): Flow<List<Training>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(training: Training)

    @Update
    suspend fun update(training: Training)

    @Delete
    suspend fun delete(training: Training)
}