// com/example/collegecompanion/data/local/AppDatabase.kt
package com.example.collegecompanion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.collegecompanion.domain.model.ClassSlot
import com.example.collegecompanion.domain.model.Task

@Database(
    entities = [Task::class, ClassSlot::class],
    version = 4,                  // ← bumped from 3 to 4
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun classSlotDao(): ClassSlotDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Only add columns that don't already exist in your Task table.
                // If Task already has dueDate, remove that line. Same for others.
                database.execSQL(
                    "ALTER TABLE tasks ADD COLUMN description TEXT NOT NULL DEFAULT ''"
                )
                database.execSQL(
                    "ALTER TABLE tasks ADD COLUMN dueDate INTEGER"
                )
                database.execSQL(
                    "ALTER TABLE tasks ADD COLUMN taskType TEXT NOT NULL DEFAULT 'ASSIGNMENT'"
                )
                database.execSQL(
                    "ALTER TABLE tasks ADD COLUMN subject TEXT"
                )
            }
        }
    }
}