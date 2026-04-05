package com.example.collegecompanion.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.collegecompanion.data.local.AppDatabase
import com.example.collegecompanion.data.local.TaskDao
import com.example.collegecompanion.data.repository.TaskRepository
import com.example.collegecompanion.data.repository.TaskRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// ✅ Migration object (1 → 2)
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE tasks ADD COLUMN taskType TEXT NOT NULL DEFAULT 'GENERAL'"
        )
    }
}

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
            .addMigrations(MIGRATION_1_2)
            // Optional during development (remove later)
            // .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao =
        database.taskDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository
}