package org.strawberryfoundations.reply.room

import org.strawberryfoundations.reply.room.entities.Exercise
import org.strawberryfoundations.reply.room.entities.WorkoutSession
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Exercise::class, WorkoutSession::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingDao(): ExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao

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

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE trainings ADD COLUMN alt_name TEXT NULL")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE workout_sessions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        exercise_id INTEGER NOT NULL,
                        started_at INTEGER NOT NULL,
                        ended_at INTEGER,
                        status TEXT NOT NULL,
                        current_weight REAL NOT NULL,
                        sets_completed INTEGER NOT NULL DEFAULT 0,
                        total_sets INTEGER,
                        elapsed_seconds INTEGER NOT NULL DEFAULT 0,
                        rest_timer_seconds INTEGER NOT NULL DEFAULT 0,
                        is_resting INTEGER NOT NULL DEFAULT 0,
                        sets_history TEXT NOT NULL DEFAULT '[]',
                        notes TEXT NOT NULL DEFAULT '',
                        updated_at INTEGER NOT NULL,
                        FOREIGN KEY(exercise_id) REFERENCES trainings(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                db.execSQL("CREATE INDEX index_workout_sessions_exercise_id ON workout_sessions(exercise_id)")
                db.execSQL("CREATE INDEX index_workout_sessions_status ON workout_sessions(status)")
                db.execSQL("CREATE INDEX index_workout_sessions_started_at ON workout_sessions(started_at)")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "training_db"
                )
                    .addMigrations(
                        MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6
                    )
                    .build().also { INSTANCE = it }
            }
    }
}