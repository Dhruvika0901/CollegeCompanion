// com/example/collegecompanion/data/local/AppDatabase.kt
package com.example.collegecompanion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.collegecompanion.domain.model.Task

@Database(
    entities = [Task::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "college_companion_db"
    }
}
