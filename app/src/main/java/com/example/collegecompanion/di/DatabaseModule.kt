// com/example/collegecompanion/di/DatabaseModule.kt
package com.example.collegecompanion.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.collegecompanion.data.local.AppDatabase
import com.example.collegecompanion.data.local.ClassSlotDao
import com.example.collegecompanion.data.local.TaskDao
import com.example.collegecompanion.data.repository.ClassSlotRepository
import com.example.collegecompanion.data.repository.ClassSlotRepositoryImpl
import com.example.collegecompanion.data.repository.TaskRepository
import com.example.collegecompanion.data.repository.TaskRepositoryImpl
import com.example.collegecompanion.data.local.AttendanceDao
import com.example.collegecompanion.data.repository.AttendanceRepository
import com.example.collegecompanion.data.repository.AttendanceRepositoryImpl
import com.example.collegecompanion.notification.NotificationScheduler

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {   // renamed: database → db
        db.execSQL(
            "ALTER TABLE tasks ADD COLUMN taskType TEXT NOT NULL DEFAULT 'GENERAL'"
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // 1. Fix the tasks table — add missing columns, remove subject
        //    SQLite can't drop columns, so we recreate the table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS tasks_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL DEFAULT '',
                priority TEXT NOT NULL,
                dueDate INTEGER,
                isCompleted INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL DEFAULT 0,
                taskType TEXT NOT NULL DEFAULT 'GENERAL'
            )
        """.trimIndent())

        // 2. Copy existing data across (subject is dropped, description/createdAt get defaults)
        db.execSQL("""
            INSERT INTO tasks_new (id, title, description, priority, dueDate, isCompleted, createdAt, taskType)
            SELECT id, title, '', priority, dueDate, isCompleted, 0, taskType
            FROM tasks
        """.trimIndent())

        // 3. Swap tables
        db.execSQL("DROP TABLE tasks")
        db.execSQL("ALTER TABLE tasks_new RENAME TO tasks")

        // 4. Create class_slots
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS class_slots (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                dayOfWeek INTEGER NOT NULL,
                startTime INTEGER NOT NULL,
                endTime INTEGER NOT NULL,
                subject TEXT NOT NULL,
                room TEXT NOT NULL,
                professor TEXT,
                classType TEXT NOT NULL DEFAULT 'LECTURE'
            )
        """.trimIndent())
    }
}

//val MIGRATION_3_4 = object : Migration(3, 4) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL(
//            "ALTER TABLE tasks ADD COLUMN subject TEXT"
//        )
//    }
//}
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "college_companion_db"
        )
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5,
                AppDatabase.MIGRATION_5_6
            )
            // Remove fallbackToDestructiveMigration entirely — it's hiding crashes and wiping data
            .build()

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    @Singleton
    fun provideClassSlotDao(database: AppDatabase): ClassSlotDao = database.classSlotDao()

    @Provides
    @Singleton
    fun provideAttendanceDao(database: AppDatabase): AttendanceDao = database.attendanceDao()  // ← moved here, added @Singleton
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindClassSlotRepository(impl: ClassSlotRepositoryImpl): ClassSlotRepository

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(impl: AttendanceRepositoryImpl): AttendanceRepository  // ← moved here
}
@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationScheduler(
        @ApplicationContext context: Context
    ): NotificationScheduler = NotificationScheduler(context)
}

