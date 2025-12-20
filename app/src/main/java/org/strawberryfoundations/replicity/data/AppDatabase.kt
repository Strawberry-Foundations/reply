package org.strawberryfoundations.replicity.data

import org.strawberryfoundations.replicity.core.model.Exercise
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Exercise::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingDao(): ExerciseDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE trainings ADD COLUMN color TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS Training")
                
                db.execSQL("""
                    CREATE TABLE trainings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        weight REAL,
                        weight_unit TEXT NOT NULL DEFAULT 'kg',
                        note TEXT NOT NULL DEFAULT '',
                        `group` TEXT NOT NULL DEFAULT 'OTHER',
                        color TEXT NOT NULL DEFAULT '',
                        created_at INTEGER NOT NULL DEFAULT 0,
                        updated_at INTEGER,
                        archived INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                
                db.execSQL("CREATE INDEX index_trainings_name ON trainings(name)")
                db.execSQL("CREATE INDEX index_trainings_group ON trainings(`group`)")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "training_db"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build().also { INSTANCE = it }
            }
    }
}