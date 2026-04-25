// com/example/collegecompanion/data/local/AppDatabase.kt
package com.example.collegecompanion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.collegecompanion.domain.model.ClassSlot
import com.example.collegecompanion.domain.model.Task
import com.example.collegecompanion.domain.model.AttendanceRecord

@Database(
    entities = [Task::class, ClassSlot::class, AttendanceRecord::class],
    version = 6,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun classSlotDao(): ClassSlotDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {

        // MIGRATION_2_3 already added: description, dueDate, taskType, class_slots
        // MIGRATION_3_4 only needs to add the 'subject' column that wasn't in 2_3
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Only 'subject' is new — everything else already exists from MIGRATION_2_3
                database.execSQL(
                    "ALTER TABLE tasks ADD COLUMN subject TEXT"
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS attendance_records (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        subjectName TEXT NOT NULL,
                        date TEXT NOT NULL,
                        status TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE tasks ADD COLUMN reminderDaysBefore INTEGER"
                )
            }
        }
    }
}